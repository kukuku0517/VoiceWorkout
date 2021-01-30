package com.example.domain.workout

import com.example.data.entity.workout.WorkoutAction
import java.lang.Exception

class TextParser {
    companion object {
        val SET = listOf("set", "sets", "세트", "셋")
        val UNIT_COUNT = listOf("회", "번", "개")
        val DURATION = listOf("분", "초", "시간")
        val WEIGHT = listOf("kg", "킬로", "킬로그램", "키로그램", "그램")
    }

    enum class WordType {
        SET,
        UNIT,
        META
    }

    data class WordNode(
        val type: WordType,
        val startIdx: Int,
        val endIdx: Int,

        val count: Int,
        val unit: String
    )

    fun extractWorkout(text: String): Pair<List<WorkoutAction>, String> {
        return listOf<WorkoutAction>() to text
    }

    fun extractWorkoutForTest(text: String): Pair<List<WorkoutActionForTest>, String> {
        return listOf<WorkoutActionForTest>() to text
    }

    fun extractWorkoutSingleForTest(text: String): Pair<WorkoutActionForTest?, String> {
        val nodes = mutableListOf<WordNode>()

        val words = text.split(" ").toMutableList()

        words.forEachIndexed { index, word ->
            SET.forEach { unit ->
                if (word.contains(unit)) {

                    checkPreviousWordForNumber(words, unit, index)?.let { (number, start) ->
                        nodes.add(
                            WordNode(
                                type = WordType.SET,
                                startIdx = start,
                                endIdx = index,
                                count = number,
                                unit = unit
                            )
                        )
                        (start..index).forEach {
                            words[it] = ""
                        }
                    }

                    return@forEach
                }
            }
        }

        words.forEachIndexed { index, word ->
            UNIT_COUNT.forEach { unit ->
                if (word.contains(unit)) {

                    checkPreviousWordForNumber(words, unit, index)?.let { (number, start) ->
                        nodes.add(
                            WordNode(
                                type = WordType.UNIT,
                                startIdx = start,
                                endIdx = index,
                                count = number,
                                unit = unit

                            )
                        )
                        (start..index).forEach {
                            words[it] = ""
                        }
                    }
                    return@forEach
                }
            }
        }

        words.forEachIndexed { index, word ->
            WEIGHT.forEach { unit ->
                if (word.contains(unit)) {

                    checkPreviousWordForNumber(words, unit, index)?.let { (number, start) ->
                        nodes.add(
                            WordNode(
                                type = WordType.META,
                                startIdx = start,
                                endIdx = index,
                                count = number,
                                unit = unit

                            )
                        )
                        (start..index).forEach {
                            words[it] = ""
                        }
                    }
                    return@forEach
                }
            }
        }

//        println(nodes)

        val action = trimIntoActions(words, nodes)
        println(action)
        return action to ""

    }

    private fun trimIntoActions(
        words: MutableList<String>,
        nodes: MutableList<WordNode>
    ): WorkoutActionForTest {
        var name = words.subList(0, words.indexOfFirst { it == "" }).reduce { acc, s -> "$acc $s" }
        var set = nodes.filter { it.type == WordType.SET }.firstOrNull()
        var unit = nodes.filter { it.type == WordType.UNIT }.firstOrNull()
        var meta = nodes.filter { it.type == WordType.META }.firstOrNull()


        val action = WorkoutActionForTest(
            name,
            set?.count,
            unit?.unit,
            unit?.count,
            meta?.let { "${meta.count}${meta.unit}" }
        )
        return action
    }

    private fun checkPreviousWordForNumber(
        words: List<String>,
        unit: String,
        index: Int
    ): Pair<Int, Int>? {
        if (words[index] != unit) {
            val left = words[index].replace(unit, "")
            try {
                return left.toInt() to index
            } catch (e: Exception) {

            }
        }

        val prev = words.getOrNull(index - 1) ?: return null
        return try {
            prev.toInt() to index - 1
        } catch (e: Exception) {
            null
        }
    }


}

data class WorkoutActionForTest(
    val name: String,
    val set: Int?,
    val unit: String?,
    val unitCount: Int?,
    val meta: String?
)
