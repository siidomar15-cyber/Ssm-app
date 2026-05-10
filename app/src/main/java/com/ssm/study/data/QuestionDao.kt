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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions ORDER BY yearStyle DESC, topic ASC")
    fun observeQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topic = :topic ORDER BY yearStyle DESC, subtopic ASC")
    fun observeQuestionsByTopic(topic: Topic): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topic = :topic AND subtopic = :subtopic ORDER BY yearStyle DESC")
    fun observeQuestionsBySubtopic(topic: Topic, subtopic: String): Flow<List<QuestionEntity>>

    @Query(
        """
        SELECT * FROM questions
        WHERE (:query = '' OR stem LIKE '%' || :query || '%' OR subtopic LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        AND (:difficulty = '' OR difficulty = :difficulty)
        ORDER BY topic ASC, subtopic ASC, yearStyle DESC
        LIMIT :limit
        """
    )
    fun observeSearch(query: String, difficulty: String, limit: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT questionId FROM question_flags WHERE bookmarked = 1 OR difficult = 1) ORDER BY topic ASC")
    fun observeFlaggedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT questionId FROM question_flags WHERE bookmarked = 1) ORDER BY topic ASC")
    fun observeBookmarkedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id IN (SELECT questionId FROM question_flags WHERE difficult = 1) ORDER BY topic ASC")
    fun observeDifficultQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    fun observeRandomQuestions(limit: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topic = :topic ORDER BY RANDOM() LIMIT :limit")
    fun observeRandomQuestionsByTopic(topic: Topic, limit: Int): Flow<List<QuestionEntity>>

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
