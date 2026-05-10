package com.ssm.study

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class QuestionBankAssetsTest {
    @Test
    fun bundledQuestionBankHasRequiredScaleAndFields() {
        val root = listOf(File("app/src/main/assets/questions"), File("src/main/assets/questions")).first { it.exists() }
        val topicFiles = root.resolve("index.json").readText()
            .lines()
            .map { it.trim().trim(',', '"') }
            .filter { it.endsWith(".json") }

        assertEquals(22, topicFiles.size)
        var total = 0
        topicFiles.forEach { fileName ->
            val raw = root.resolve(fileName).readText()
            total += Regex("\"id\"\\s*:").findAll(raw).count()
            listOf(
                "topic",
                "subtopic",
                "difficulty",
                "yearStyle",
                "stem",
                "options",
                "correctAnswerIndex",
                "conciseExplanation",
                "highYieldTakeaway",
                "tags"
            ).forEach { field -> assertTrue("$fileName missing $field", raw.contains("\"$field\"")) }
        }
        assertEquals(2_500, total)
    }
}
