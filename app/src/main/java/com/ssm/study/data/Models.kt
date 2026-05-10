package com.ssm.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Topic(val displayName: String) {
    CARDIOLOGY("Cardiology"),
    PNEUMOLOGY("Pneumology"),
    NEPHROLOGY("Nephrology"),
    ENDOCRINOLOGY("Endocrinology"),
    NEUROLOGY("Neurology"),
    PSYCHIATRY("Psychiatry"),
    PEDIATRICS("Pediatrics"),
    INFECTIOUS_DISEASES("Infectious diseases"),
    SURGERY("Surgery"),
    EMERGENCY_MEDICINE("Emergency medicine"),
    ONCOLOGY("Oncology"),
    RHEUMATOLOGY("Rheumatology"),
    DERMATOLOGY("Dermatology"),
    PHARMACOLOGY("Pharmacology"),
    STATISTICS("Statistics"),
    GYNECOLOGY("Gynecology"),
    GASTROENTEROLOGY("Gastroenterology"),
    OPHTHALMOLOGY("Ophthalmology"),
    ENT("ENT"),
    ORTHOPEDICS("Orthopedics")
}

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val topic: Topic,
    val year: Int,
    val stem: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val optionE: String,
    val correctIndex: Int,
    val explanation: String,
    val takeaway: String
) {
    val options: List<String>
        get() = listOf(optionA, optionB, optionC, optionD, optionE)
}

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val elapsedMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "question_flags")
data class QuestionFlagEntity(
    @PrimaryKey val questionId: String,
    val bookmarked: Boolean = false,
    val difficult: Boolean = false
)

data class TopicProgress(
    val topic: Topic,
    val total: Int,
    val attempted: Int,
    val correct: Int
) {
    val accuracy: Float = if (attempted == 0) 0f else correct.toFloat() / attempted
}

data class QuestionWithFlag(
    val question: QuestionEntity,
    val bookmarked: Boolean,
    val difficult: Boolean
)
