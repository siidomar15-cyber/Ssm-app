package com.ssm.study

import com.ssm.study.data.QuestionBank
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankJsonTest {
    private val questionsDir = File("src/main/assets/questions")

    @Test
    fun questionFilesAreSmallUtf8JsonFilesGroupedByTopic() {
        val files = questionFiles()
        assertTrue("Expected question JSON files", files.isNotEmpty())

        files.forEach { file ->
            assertTrue("${file.name} should be smaller than 256 KiB", file.length() < 256 * 1024)
            val bytes = file.readBytes()
            assertFalse("${file.name} should not contain NUL bytes", bytes.contains(0))
            val text = bytes.toString(Charsets.UTF_8)
            val questions = QuestionBank.parseQuestions(text, file.name)
            assertTrue("${file.name} should contain at least one question", questions.isNotEmpty())
            assertEquals(
                "${file.name} should contain questions for a single topic matching its file name",
                setOf(file.nameWithoutExtension.uppercase()),
                questions.map { it.topic.name }.toSet()
            )
        }
    }

    @Test
    fun questionFilesHaveValidQuestionContent() {
        val questions = questionFiles().flatMap { file ->
            QuestionBank.parseQuestions(file.readText(), file.name)
        }
        assertTrue("Expected questions", questions.isNotEmpty())
        assertEquals("Question IDs should be unique", questions.size, questions.map { it.id }.toSet().size)

        questions.forEach { question ->
            assertEquals(5, question.options.size)
            assertTrue(question.correctIndex in 0..4)
            assertTrue(question.stem.isNotBlank())
            assertTrue(question.options.all { it.isNotBlank() })
            assertTrue(question.explanation.isNotBlank())
            assertTrue(question.takeaway.isNotBlank())
            assertTrue(question.explanation.split('.').filter { it.isNotBlank() }.size <= 4)
        }
    }

    private fun questionFiles(): List<File> = questionsDir
        .listFiles { file -> file.isFile && file.extension == "json" }
        ?.sortedBy { it.name }
        .orEmpty()
}
