package com.ssm.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Topic(val displayName: String, val assetName: String) {
    CARDIOLOGY("Cardiology", "cardiology"),
    PNEUMOLOGY("Pneumology", "pneumology"),
    NEPHROLOGY("Nephrology", "nephrology"),
    ENDOCRINOLOGY("Endocrinology", "endocrinology"),
    GASTROENTEROLOGY("Gastroenterology", "gastroenterology"),
    NEUROLOGY("Neurology", "neurology"),
    PSYCHIATRY("Psychiatry", "psychiatry"),
    PEDIATRICS("Pediatrics", "pediatrics"),
    INFECTIOUS_DISEASES("Infectious diseases", "infectious_diseases"),
    HEMATOLOGY("Hematology", "hematology"),
    ONCOLOGY("Oncology", "oncology"),
    RHEUMATOLOGY("Rheumatology", "rheumatology"),
    DERMATOLOGY("Dermatology", "dermatology"),
    GENERAL_SURGERY("General surgery", "general_surgery"),
    EMERGENCY_MEDICINE("Emergency medicine", "emergency_medicine"),
    PHARMACOLOGY("Pharmacology", "pharmacology"),
    STATISTICS_EPIDEMIOLOGY("Statistics and epidemiology", "statistics_epidemiology"),
    GYNECOLOGY_OBSTETRICS("Gynecology and obstetrics", "gynecology_obstetrics"),
    OPHTHALMOLOGY("Ophthalmology", "ophthalmology"),
    ENT("ENT", "ent"),
    ORTHOPEDICS("Orthopedics", "orthopedics"),
    PUBLIC_HEALTH("Public health", "public_health");

    companion object {
        fun fromDisplayName(value: String): Topic = entries.first { it.displayName == value }
    }
}

enum class QuizMode(val label: String) {
    TOPIC("Topic practice"),
    MIXED("Random mixed quiz"),
    WEAK("Weak-topic mode"),
    BOOKMARKS("Bookmark mode"),
    DIFFICULT("Difficult-question mode"),
    SIMULATION("140-question timed simulation")
}

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val topic: Topic,
    val subtopic: String,
    val difficulty: String,
    val yearStyle: Int,
    val stem: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val optionE: String,
    val correctIndex: Int,
    val explanation: String,
    val takeaway: String,
    val tags: List<String>
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
    val mastery: Int = ((accuracy * 70f) + ((attempted.toFloat() / total.coerceAtLeast(1)).coerceAtMost(1f) * 30f)).toInt()
}

data class QuestionWithFlag(
    val question: QuestionEntity,
    val bookmarked: Boolean,
    val difficult: Boolean
)
