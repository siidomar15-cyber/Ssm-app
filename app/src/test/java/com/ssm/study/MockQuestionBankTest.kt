package com.ssm.study

import com.ssm.study.data.MockQuestionBank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockQuestionBankTest {
    @Test
    fun mockQuestionsUseFiveAnswersAndConciseExplanations() {
        assertTrue(MockQuestionBank.questions.isNotEmpty())
        MockQuestionBank.questions.forEach { question ->
            assertEquals(5, question.options.size)
            assertTrue(question.correctIndex in 0..4)
            assertTrue(question.explanation.split('.').filter { it.isNotBlank() }.size <= 4)
            assertTrue(question.takeaway.isNotBlank())
        }
    }
}
