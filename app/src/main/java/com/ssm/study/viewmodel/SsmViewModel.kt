package com.ssm.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssm.study.data.QuestionWithFlag
import com.ssm.study.data.QuizMode
import com.ssm.study.data.SsmRepository
import com.ssm.study.data.Topic
import com.ssm.study.data.TopicProgress
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalQuestions: Int = 0,
    val attemptedQuestions: Int = 0,
    val accuracy: Float = 0f,
    val bookmarked: Int = 0,
    val difficult: Int = 0,
    val dailyStreak: Int = 0,
    val averageMastery: Int = 0,
    val weakAreas: List<TopicProgress> = emptyList()
)

data class SearchUiState(
    val query: String = "",
    val difficulty: String = "",
    val results: List<QuestionWithFlag> = emptyList()
)

data class AiTutorUiState(
    val visible: Boolean = false,
    val response: String = "AI Tutor is optional. Offline mode still gives a concise explanation and high-yield takeaway."
)

data class QuizUiState(
    val questions: List<QuestionWithFlag> = emptyList(),
    val mode: QuizMode = QuizMode.TOPIC,
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val answered: Int = 0,
    val correct: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val timedMode: Boolean = false,
    val secondsRemaining: Int = 0,
    val aiTutor: AiTutorUiState = AiTutorUiState()
) {
    val current: QuestionWithFlag? = questions.getOrNull(currentIndex)
    val isComplete: Boolean = questions.isNotEmpty() && currentIndex >= questions.lastIndex && selectedIndex != null
}

class SsmViewModel(private val repository: SsmRepository) : ViewModel() {
    private var quizJob: Job? = null
    private var timerJob: Job? = null
    private var searchJob: Job? = null

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
        val weakAreas = progress.filter { it.attempted > 0 }.sortedBy { it.mastery }.take(4)
        val masteredTopics = progress.filter { it.total > 0 }
        DashboardUiState(
            totalQuestions = questions.size,
            attemptedQuestions = attemptedIds.size,
            accuracy = if (attempts.isEmpty()) 0f else attempts.count { it.isCorrect }.toFloat() / attempts.size,
            bookmarked = flags.count { it.bookmarked },
            difficult = flags.count { it.difficult },
            dailyStreak = attempts.dailyStreak(),
            averageMastery = if (masteredTopics.isEmpty()) 0 else masteredTopics.sumOf { it.mastery } / masteredTopics.size,
            weakAreas = weakAreas
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    val bookmarks: StateFlow<List<QuestionWithFlag>> = repository.flaggedQuestions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _search = MutableStateFlow(SearchUiState())
    val search: StateFlow<SearchUiState> = _search

    private val _quiz = MutableStateFlow(QuizUiState())
    val quiz: StateFlow<QuizUiState> = _quiz

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
        updateSearch()
    }

    fun updateSearch(query: String = _search.value.query, difficulty: String = _search.value.difficulty) {
        _search.value = _search.value.copy(query = query, difficulty = difficulty)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            repository.searchQuestions(query, difficulty).collect { results ->
                _search.value = _search.value.copy(results = results)
            }
        }
    }

    fun startTopicQuiz(topic: Topic) = collectQuiz(QuizMode.TOPIC, timed = false) {
        repository.questionsForTopic(topic)
    }

    fun startMixedQuiz(limit: Int = 40) = collectQuiz(QuizMode.MIXED, timed = false) {
        repository.randomQuestions(limit)
    }

    fun startBookmarkQuiz() = collectQuiz(QuizMode.BOOKMARKS, timed = false) {
        repository.bookmarkedQuestions
    }

    fun startDifficultQuiz() = collectQuiz(QuizMode.DIFFICULT, timed = false) {
        repository.difficultQuestions
    }

    fun startWeakTopicQuiz(limit: Int = 40) {
        timerJob?.cancel()
        quizJob?.cancel()
        quizJob = viewModelScope.launch {
            val weakTopic = repository.weakestTopic() ?: Topic.entries.first()
            repository.randomQuestionsForTopic(weakTopic, limit).collect { questions ->
                _quiz.value = QuizUiState(questions = questions, mode = QuizMode.WEAK, startedAt = System.currentTimeMillis())
            }
        }
    }

    fun startSimulation(limit: Int = 140, minutes: Int = 140) = collectQuiz(QuizMode.SIMULATION, timed = true, seconds = minutes * 60) {
        repository.randomQuestions(limit)
    }

    fun startSearchQuiz() {
        val results = _search.value.results
        _quiz.value = QuizUiState(questions = results, mode = QuizMode.MIXED, startedAt = System.currentTimeMillis())
    }

    private fun collectQuiz(
        mode: QuizMode,
        timed: Boolean,
        seconds: Int = 0,
        source: () -> kotlinx.coroutines.flow.Flow<List<QuestionWithFlag>>
    ) {
        timerJob?.cancel()
        quizJob?.cancel()
        quizJob = viewModelScope.launch {
            source().collect { questions ->
                _quiz.value = QuizUiState(
                    questions = questions,
                    mode = mode,
                    timedMode = timed,
                    secondsRemaining = seconds,
                    startedAt = System.currentTimeMillis()
                )
                if (timed) startTimer()
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
                aiTutor = AiTutorUiState(),
                startedAt = System.currentTimeMillis()
            )
        }
    }

    fun showAiTutor() {
        val current = _quiz.value.current?.question ?: return
        _quiz.value = _quiz.value.copy(
            aiTutor = AiTutorUiState(
                visible = true,
                response = "Offline tutor summary: ${current.takeaway} The key discriminator is ${current.subtopic.lowercase()} in ${current.topic.displayName}. Optional cloud AI can expand this when configured, but the quiz remains fully usable offline."
            )
        )
    }

    fun hideAiTutor() {
        _quiz.value = _quiz.value.copy(aiTutor = AiTutorUiState())
    }

    fun toggleBookmark(question: QuestionWithFlag) {
        viewModelScope.launch { repository.setBookmark(question.question.id, !question.bookmarked) }
    }

    fun toggleDifficult(question: QuestionWithFlag) {
        viewModelScope.launch { repository.setDifficult(question.question.id, !question.difficult) }
    }

    private fun List<com.ssm.study.data.AttemptEntity>.dailyStreak(): Int {
        val days = map { Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()
        if (days.isEmpty()) return 0
        var streak = 0
        var cursor = LocalDate.now()
        if (cursor !in days) cursor = cursor.minusDays(1)
        while (cursor in days) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
