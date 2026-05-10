package com.ssm.study.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object QuestionBank {
    private const val QUESTION_ASSET_DIR = "questions"

    fun loadFromAssets(context: Context): List<QuestionEntity> {
        val assetManager = context.assets
        val files = assetManager.list(QUESTION_ASSET_DIR)
            ?.filter { it.endsWith(".json") }
            ?.sorted()
            .orEmpty()

        return files.flatMap { fileName ->
            val path = "$QUESTION_ASSET_DIR/$fileName"
            assetManager.open(path).bufferedReader().use { reader ->
                parseQuestions(reader.readText(), path)
            }
        }
    }

    fun parseQuestions(json: String, sourceName: String = "questions.json"): List<QuestionEntity> {
        val questions = JSONArray(json)
        return List(questions.length()) { index ->
            questions.getJSONObject(index).toQuestionEntity(sourceName, index)
        }
    }

    private fun JSONObject.toQuestionEntity(sourceName: String, index: Int): QuestionEntity {
        val options = getJSONArray("options").toStringList()
        require(options.size == 5) { "$sourceName[$index] must contain exactly five options" }

        val correctIndex = getInt("correctIndex")
        require(correctIndex in options.indices) { "$sourceName[$index] correctIndex must be between 0 and 4" }

        return QuestionEntity(
            id = getString("id"),
            topic = Topic.valueOf(getString("topic")),
            year = getInt("year"),
            stem = getString("stem"),
            optionA = options[0],
            optionB = options[1],
            optionC = options[2],
            optionD = options[3],
            optionE = options[4],
            correctIndex = correctIndex,
            explanation = getString("explanation"),
            takeaway = getString("takeaway")
        )
    }

    private fun JSONArray.toStringList(): List<String> = List(length()) { index -> getString(index) }
}
