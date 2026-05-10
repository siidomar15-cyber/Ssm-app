package com.ssm.study.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object QuestionBank {
    private const val QUESTION_ASSET_DIR = "questions"
    private const val INSERT_CHUNK_SIZE = 75

    fun assetQuestionFiles(context: Context): List<String> = context.assets
        .list(QUESTION_ASSET_DIR)
        ?.filter { it.endsWith(".json") }
        ?.sorted()
        .orEmpty()

    suspend fun importAssetsIfEmpty(context: Context, dao: QuestionDao): QuestionBankImportResult {
        if (dao.countQuestions() > 0) return QuestionBankImportResult(fileCount = 0, questionCount = 0)

        var imported = 0
        var files = 0
        assetQuestionFiles(context).forEach { fileName ->
            val path = "$QUESTION_ASSET_DIR/$fileName"
            files += 1
            context.assets.open(path).bufferedReader().use { reader ->
                parseQuestions(reader.readText(), path)
                    .chunked(INSERT_CHUNK_SIZE)
                    .forEach { chunk ->
                        dao.insertQuestions(chunk)
                        imported += chunk.size
                    }
            }
        }
        require(imported > 0) { "No question JSON files were found in assets/$QUESTION_ASSET_DIR" }
        return QuestionBankImportResult(fileCount = files, questionCount = imported)
    }

    fun parseQuestions(json: String, sourceName: String = "questions.json"): List<QuestionEntity> {
        val questions = JSONArray(json)
        return List(questions.length()) { index ->
            questions.getJSONObject(index).toQuestionEntity(sourceName, index)
        }
    }

    private fun JSONObject.toQuestionEntity(sourceName: String, index: Int): QuestionEntity {
        val id = getString("id").trim()
        require(id.isNotEmpty()) { "$sourceName[$index] id must not be blank" }

        val topicName = getString("topic")
        val topic = runCatching { Topic.valueOf(topicName) }
            .getOrElse { error("$sourceName[$index] has unknown topic '$topicName'") }

        val options = getJSONArray("options").toStringList()
        require(options.size == 5) { "$sourceName[$index] must contain exactly five options" }
        require(options.all { it.isNotBlank() }) { "$sourceName[$index] options must not be blank" }

        val correctIndex = getInt("correctIndex")
        require(correctIndex in options.indices) { "$sourceName[$index] correctIndex must be between 0 and 4" }

        return QuestionEntity(
            id = id,
            topic = topic,
            year = getInt("year"),
            stem = getString("stem").trim().also { require(it.isNotEmpty()) { "$sourceName[$index] stem must not be blank" } },
            optionA = options[0],
            optionB = options[1],
            optionC = options[2],
            optionD = options[3],
            optionE = options[4],
            correctIndex = correctIndex,
            explanation = getString("explanation").trim().also { require(it.isNotEmpty()) { "$sourceName[$index] explanation must not be blank" } },
            takeaway = getString("takeaway").trim().also { require(it.isNotEmpty()) { "$sourceName[$index] takeaway must not be blank" } }
        )
    }

    private fun JSONArray.toStringList(): List<String> = List(length()) { index -> getString(index).trim() }
}
