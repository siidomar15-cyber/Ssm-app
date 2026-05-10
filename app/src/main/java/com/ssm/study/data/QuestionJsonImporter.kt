package com.ssm.study.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class QuestionJsonImporter(private val context: Context) {
    fun loadAllQuestions(): List<QuestionEntity> {
        val index = context.assets.open("questions/index.json").bufferedReader().use { it.readText() }
        val files = JSONArray(index)
        return buildList {
            for (i in 0 until files.length()) {
                addAll(loadTopicFile(files.getString(i)))
            }
        }
    }

    private fun loadTopicFile(fileName: String): List<QuestionEntity> {
        val raw = context.assets.open("questions/$fileName").bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val questions = root.getJSONArray("questions")
        return List(questions.length()) { index -> questions.getJSONObject(index).toQuestionEntity() }
    }

    private fun JSONObject.toQuestionEntity(): QuestionEntity {
        val options = getJSONArray("options")
        val tagArray = getJSONArray("tags")
        return QuestionEntity(
            id = getString("id"),
            topic = Topic.fromDisplayName(getString("topic")),
            subtopic = getString("subtopic"),
            difficulty = getString("difficulty"),
            yearStyle = getInt("yearStyle"),
            stem = getString("stem"),
            optionA = options.getString(0),
            optionB = options.getString(1),
            optionC = options.getString(2),
            optionD = options.getString(3),
            optionE = options.getString(4),
            correctIndex = getInt("correctAnswerIndex"),
            explanation = getString("conciseExplanation"),
            takeaway = getString("highYieldTakeaway"),
            tags = List(tagArray.length()) { tagArray.getString(it) }
        )
    }
}
