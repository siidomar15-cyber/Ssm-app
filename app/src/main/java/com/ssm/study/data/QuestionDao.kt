package com.ssm.study.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT COUNT(*) FROM questions")
    suspend fun countQuestions(): Int

    @Query("SELECT COUNT(*) FROM questions")
    fun observeQuestionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT topic, COUNT(*) AS total FROM questions GROUP BY topic")
    fun observeTopicQuestionCounts(): Flow<List<TopicQuestionCount>>

    @Query("SELECT id, topic FROM questions")
    fun observeQuestionTopicRefs(): Flow<List<QuestionTopicRef>>

    @Query("SELECT * FROM questions WHERE topic = :topic ORDER BY year DESC, id ASC")
    fun observeQuestionsByTopic(topic: Topic): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT questionId FROM question_flags WHERE bookmarked = 1 OR difficult = 1) ORDER BY topic ASC, year DESC")
    fun observeFlaggedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    fun observeSimulationQuestions(limit: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM attempts ORDER BY timestamp DESC")
    fun observeAttempts(): Flow<List<AttemptEntity>>

    @Insert
    suspend fun insertAttempt(attempt: AttemptEntity)

    @Query("SELECT * FROM question_flags")
    fun observeFlags(): Flow<List<QuestionFlagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlag(flag: QuestionFlagEntity)

    @Query("SELECT * FROM question_flags WHERE questionId = :questionId LIMIT 1")
    suspend fun getFlag(questionId: String): QuestionFlagEntity?
}
