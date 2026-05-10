package com.ssm.study.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class SsmRepository(
    private val appContext: Context,
    private val dao: QuestionDao
) {
    val totalQuestions: Flow<Int> = dao.observeQuestionCount()
    val attempts: Flow<List<AttemptEntity>> = dao.observeAttempts()
    val flags: Flow<List<QuestionFlagEntity>> = dao.observeFlags()

    val flaggedQuestions: Flow<List<QuestionWithFlag>> = combine(
        dao.observeFlaggedQuestions(),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    val topicProgress: Flow<List<TopicProgress>> = combine(
        dao.observeTopicQuestionCounts(),
        dao.observeQuestionTopicRefs(),
        attempts
    ) { counts, questionTopics, attempts ->
        val totals = counts.associate { it.topic to it.total }
        val topicByQuestionId = questionTopics.associate { it.id to it.topic }
        val attemptsByTopic = attempts.groupBy { topicByQuestionId[it.questionId] }
        Topic.entries.map { topic ->
            val topicAttempts = attemptsByTopic[topic].orEmpty()
            TopicProgress(
                topic = topic,
                total = totals[topic] ?: 0,
                attempted = topicAttempts.map { it.questionId }.distinct().size,
                correct = topicAttempts.count { it.isCorrect }
            )
        }
    }

    fun questionsForTopic(topic: Topic): Flow<List<QuestionWithFlag>> = combine(
        dao.observeQuestionsByTopic(topic),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    fun simulationQuestions(limit: Int): Flow<List<QuestionWithFlag>> = combine(
        dao.observeSimulationQuestions(limit),
        flags
    ) { questions, flags -> questions.withFlags(flags) }

    suspend fun seedIfEmpty(): QuestionBankImportResult = withContext(Dispatchers.IO) {
        QuestionBank.importAssetsIfEmpty(appContext, dao)
    }

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
}
