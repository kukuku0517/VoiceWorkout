package com.example.domain.workout

import com.example.data.entity.workout.WorkoutAction
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class TextParserTest {

    val testSets = mapOf(
        "스쿼트 3 세트 20 회 10kg" to WorkoutActionForTest("스쿼트", 3, "회", 20, "10kg"),
        "푸쉬업 스탑 엔 고 10회 3세트" to WorkoutActionForTest("푸쉬업 스탑 엔 고", 3, "회", 10, null),
        "원암숄더프레스 스탑엔고 5회 3세트" to WorkoutActionForTest("원암숄더프레스 스탑엔고", 3, "회", 5, null),
        "원암돔벨컬 스탑엔고 5회 1세트" to WorkoutActionForTest("원암돔벨컬 스탑엔고", 1, "회", 5, null)

        )

    @Test
    fun extractWorkoutTest() {
        val parser = TextParser()

        testSets.forEach { (s, action) ->
            val result = parser.extractWorkoutSingleForTest(s)
            assertEquals(parser.extractWorkoutSingleForTest(s).first, action)
        }
    }
}
