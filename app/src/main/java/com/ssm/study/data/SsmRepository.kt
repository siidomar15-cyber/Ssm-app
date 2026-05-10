package com.ssm.study.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class SsmRepository(
    private val dao: QuestionDao,
    private val importer: QuestionJsonImporter
) {
    val questions: Flow<List<QuestionEntity>> = dao.observeQuestions()
    val attempts: Flow<List<AttemptEntity>> = dao.observeAttempts()
    val flags: Flow<List<QuestionFlagEntity>> = dao.observeFlags()

    val flaggedQuestions: Flow<List<QuestionWithFlag>> = combine(
        dao.observeFlaggedQuestions(),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    val bookmarkedQuestions: Flow<List<QuestionWithFlag>> = combine(
        dao.observeBookmarkedQuestions(),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    val difficultQuestions: Flow<List<QuestionWithFlag>> = combine(
        dao.observeDifficultQuestions(),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    val topicProgress: Flow<List<TopicProgress>> = combine(questions, attempts) { questions, attempts ->
        Topic.entries.map { topic ->
            val topicQuestions = questions.filter { it.topic == topic }
            val questionIds = topicQuestions.map { it.id }.toSet()
            val topicAttempts = attempts.filter { it.questionId in questionIds }
            TopicProgress(
                topic = topic,
                total = topicQuestions.size,
                attempted = topicAttempts.map { it.questionId }.distinct().size,
                correct = topicAttempts.count { it.isCorrect }
            )
        }
    }

    fun questionsForTopic(topic: Topic): Flow<List<QuestionWithFlag>> = combine(
        dao.observeQuestionsByTopic(topic),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    fun randomQuestions(limit: Int): Flow<List<QuestionWithFlag>> = combine(
        dao.observeRandomQuestions(limit),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    fun randomQuestionsForTopic(topic: Topic, limit: Int): Flow<List<QuestionWithFlag>> = combine(
        dao.observeRandomQuestionsByTopic(topic, limit),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    fun searchQuestions(query: String, difficulty: String = "", limit: Int = 150): Flow<List<QuestionWithFlag>> = combine(
        dao.observeSearch(query.trim(), difficulty, limit),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    suspend fun seedIfEmpty() {
        if (dao.countQuestions() == 0) dao.insertQuestions(importer.loadAllQuestions())
    }

    suspend fun weakestTopic(): Topic? = topicProgress.first()
        .filter { it.total > 0 && it.attempted > 0 }
        .minByOrNull { it.mastery }
        ?.topic

    suspend fun recordAnswer(question: QuestionEntity, selectedIndex: Int, elapsedMs: Long) {
        dao.insertAttempt(
            AttemptEntity(
                questionId = question.id,
                selectedIndex = selectedIndex,
                isCorrect = selectedIndex == question.correctIndex,
                elapsedMs = elapsedMs
            )
        )
    }

    suspend fun setBookmark(questionId: String, bookmarked: Boolean) {
        val current = dao.getFlag(questionId) ?: QuestionFlagEntity(questionId = questionId)
        dao.upsertFlag(current.copy(bookmarked = bookmarked))
    }

    suspend fun setDifficult(questionId: String, difficult: Boolean) {
        val current = dao.getFlag(questionId) ?: QuestionFlagEntity(questionId = questionId)
        dao.upsertFlag(current.copy(difficult = difficult))
    }

    private fun List<QuestionEntity>.withFlags(flags: List<QuestionFlagEntity>): List<QuestionWithFlag> {
        val byId = flags.associateBy { it.questionId }
        return map { question ->
            val flag = byId[question.id]
            QuestionWithFlag(question, flag?.bookmarked == true, flag?.difficult == true)
        }
    }

    companion object {
        fun create(context: Context, dao: QuestionDao): SsmRepository = SsmRepository(
            dao = dao,
            importer = QuestionJsonImporter(context.applicationContext)
        )
    }
}
