package com.ssm.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssm.study.data.QuestionWithFlag
import com.ssm.study.data.SsmRepository
import com.ssm.study.data.Topic
import com.ssm.study.data.TopicProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalQuestions: Int = 0,
    val attemptedQuestions: Int = 0,
    val accuracy: Float = 0f,
    val bookmarked: Int = 0,
    val difficult: Int = 0,
    val weakAreas: List<TopicProgress> = emptyList()
)

data class QuizUiState(
    val questions: List<QuestionWithFlag> = emptyList(),
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val answered: Int = 0,
    val correct: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val timedMode: Boolean = false,
    val secondsRemaining: Int = 0
) {
    val current: QuestionWithFlag? = questions.getOrNull(currentIndex)
    val isComplete: Boolean = questions.isNotEmpty() && currentIndex >= questions.lastIndex && selectedIndex != null
}

class SsmViewModel(private val repository: SsmRepository) : ViewModel() {
    private var quizJob: Job? = null
    private var timerJob: Job? = null
    val topics: StateFlow<List<TopicProgress>> = repository.topicProgress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val dashboard: StateFlow<DashboardUiState> = combine(
        repository.questions,
        repository.attempts,
        repository.flags,
        repository.topicProgress
    ) { questions, attempts, flags, progress ->
        val attemptedIds = attempts.map { it.questionId }.distinct()
        val weakAreas = progress.filter { it.attempted > 0 }.sortedBy { it.accuracy }.take(4)
        DashboardUiState(
            totalQuestions = questions.size,
            attemptedQuestions = attemptedIds.size,
            accuracy = if (attempts.isEmpty()) 0f else attempts.count { it.isCorrect }.toFloat() / attempts.size,
            bookmarked = flags.count { it.bookmarked },
            difficult = flags.count { it.difficult },
            weakAreas = weakAreas
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    val bookmarks: StateFlow<List<QuestionWithFlag>> = repository.flaggedQuestions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _quiz = kotlinx.coroutines.flow.MutableStateFlow(QuizUiState())
    val quiz: StateFlow<QuizUiState> = _quiz

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun startTopicQuiz(topic: Topic) {
        timerJob?.cancel()
        quizJob?.cancel()
        quizJob = viewModelScope.launch {
            repository.questionsForTopic(topic).collect { questions ->
                _quiz.value = QuizUiState(questions = questions, startedAt = System.currentTimeMillis())
            }
        }
    }

    fun startSimulation(limit: Int = 120, minutes: Int = 120) {
        timerJob?.cancel()
        quizJob?.cancel()
        val totalSeconds = minutes * 60
        quizJob = viewModelScope.launch {
            repository.simulationQuestions(limit).collect { questions ->
                _quiz.value = QuizUiState(
                    questions = questions,
                    timedMode = true,
                    secondsRemaining = totalSeconds,
                    startedAt = System.currentTimeMillis()
                )
                startTimer()
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quiz.value.timedMode && _quiz.value.secondsRemaining > 0) {
                delay(1_000)
                _quiz.value = _quiz.value.copy(secondsRemaining = (_quiz.value.secondsRemaining - 1).coerceAtLeast(0))
            }
        }
    }

    fun answer(index: Int) {
        val state = _quiz.value
        val question = state.current?.question ?: return
        if (state.selectedIndex != null) return
        val elapsed = System.currentTimeMillis() - state.startedAt
        val correct = index == question.correctIndex
        _quiz.value = state.copy(
            selectedIndex = index,
            answered = state.answered + 1,
            correct = state.correct + if (correct) 1 else 0
        )
        viewModelScope.launch { repository.recordAnswer(question, index, elapsed) }
    }

    fun nextQuestion() {
        val state = _quiz.value
        if (state.currentIndex < state.questions.lastIndex) {
            _quiz.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedIndex = null,
                startedAt = System.currentTimeMillis()
            )
        }
    }

    fun toggleBookmark(question: QuestionWithFlag) {
        viewModelScope.launch { repository.setBookmark(question.question.id, !question.bookmarked) }
    }

    fun toggleDifficult(question: QuestionWithFlag) {
        viewModelScope.launch { repository.setDifficult(question.question.id, !question.difficult) }
    }
}
