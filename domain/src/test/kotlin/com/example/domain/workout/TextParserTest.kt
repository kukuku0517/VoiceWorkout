package com.example.domain.workout

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class TextParserTest {

    private val workoutPhraseTest = mapOf(
        "원암 덤벨 컬 스탑앤고 5일 세트" to
                listOf(ParseResult("원암 덤벨 컬 스탑앤고 5일", 1, null, null, null)),
        "스쿼트 3 세트 20 회 10kg" to
                listOf(ParseResult("스쿼트", 3, "회", 20, 10)),
        "스쿼트 3 세트 1분 10kg" to
                listOf(ParseResult("스쿼트", 3, "분", 1, 10)),
        "푸쉬업 스탑 엔 고 10회 3세트" to
                listOf(ParseResult("푸쉬업 스탑 엔 고", 3, "회", 10, null)),
        "원암숄더프레스 스탑엔고 5회 3 세트" to
                listOf(ParseResult("원암숄더프레스 스탑엔고", 3, "회", 5, null)),
        "원암돔벨컬 스탑엔고 5 회 1세트" to
                listOf(ParseResult("원암돔벨컬 스탑엔고", 1, "회", 5, null)),
        "스쿼트 1 세트 20 회 10kg 1세트 10회 20kg" to
                listOf(
                    ParseResult("스쿼트", 1, "회", 20, 10),
                    ParseResult("스쿼트", 1, "회", 10, 20)
                ),
        "스쿼트 1 세트 20 회 1 세트 10회 10kg" to
                listOf(
                    ParseResult("스쿼트", 1, "회", 20, 10),
                    ParseResult("스쿼트", 1, "회", 10, 10)
                )

    )

    @Test
    fun extractWorkoutTest() {
        val parser = TextParser()

        workoutPhraseTest.forEach { (s, action) ->
            assertEquals(parser.extractWorkoutForTest(s).first, action)
        }
    }

    @Test
    fun checkRepeatTest(){
        val parser = TextParser()

        assertEquals(parser.checkRepeatPattern("1234"),"")
        assertEquals(parser.checkRepeatPattern("1234234"),"234")
        assertEquals(parser.checkRepeatPattern("123234"),"23")
        assertEquals(parser.checkRepeatPattern("12234"),"2")
    }
}
