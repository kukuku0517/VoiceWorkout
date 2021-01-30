package com.example.domain.workout

import com.example.data.entity.workout.WorkoutAction
import java.lang.Exception

data class ParseResult(
    val name: String?,
    val set: Int?,
    val unit: String?,
    val unitCount: Int?,
    val weight: Int?
)

class TextParser {
    companion object {
        val SET = listOf("set", "sets", "세트", "셋")
        val UNIT_COUNT = listOf("회", "번", "개")
        val DURATION = listOf("분", "초", "시간")
        val WEIGHT = listOf("kg", "킬로", "킬로그램", "키로그램", "그램")
    }

    enum class WordType {
        NAME,
        SET,
        UNIT,
        DURATION,
        WEIGHT
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

    fun extractWorkoutForTest(text: String): Pair<List<ParseResult>, String> {
        val nodes = mutableListOf<WordNode>()
        val words = text.split(" ").toMutableList()

        words.forEachIndexed { index, word ->
            SET.forEach { unit ->
                if (word.contains(unit)) {

                    checkPreviousWordForCount(words, unit, index)?.let { (number, start) ->
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

                    checkPreviousWordForCount(words, unit, index)?.let { (number, start) ->
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
            DURATION.forEach { unit ->
                if (word.contains(unit)) {

                    checkPreviousWordForCount(words, unit, index)?.let { (number, start) ->
                        nodes.add(
                            WordNode(
                                type = WordType.DURATION,
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

                    checkPreviousWordForCount(words, unit, index)?.let { (number, start) ->
                        nodes.add(
                            WordNode(
                                type = WordType.WEIGHT,
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

        val nameEndIdx = words.indexOfFirst { it == "" } - 1
        var name =
            words.subList(0, nameEndIdx + 1).reduce { acc, s -> "$acc $s" }
        nodes.add(
            WordNode(WordType.NAME, 0, nameEndIdx, 0, name)
        )
        val action = nodesToParseResults(nodes.sortedBy { it.startIdx })
        println(action)
        return action to ""

    }

    private fun nodesToParseResults(
        nodes: List<WordNode>
    ): List<ParseResult> {
        val result = checkRepeatNode(nodes)

        return result.map { nodes ->
            var name = nodes.firstOrNull { it.type == WordType.NAME }?.unit
            var set = nodes.firstOrNull { it.type == WordType.SET }
            var unit =
                nodes.firstOrNull { it.type == WordType.UNIT }
                    ?: nodes.firstOrNull { it.type == WordType.DURATION }
            var meta = nodes.firstOrNull { it.type == WordType.WEIGHT }


            ParseResult(
                name,
                set?.count,
                unit?.unit,
                unit?.count,
                meta?.count
            )
        }

    }

    private fun checkRepeatNode(nodes: List<WordNode>): List<List<WordNode>> {
        val types = nodes.joinToString("") { it.type.ordinal.toString() }

        val pattern = checkRepeatPattern(types)

        if (pattern.isEmpty()) {
            return listOf(nodes)
        }

        val left = types.replace(pattern, "")
            .splitToSequence("")
            .filter { it.isNotBlank() }
            .map { WordType.values()[it.toInt()] }
            .map { type -> nodes.first { it.type == type } }
            .toList()

        val newList = mutableListOf<List<WordNode>>()
        Regex.fromLiteral(pattern).findAll(types).toList().forEach {
            val range = it.range
            val newNodes = mutableListOf<WordNode>()
            newNodes.addAll(left.map { it.copy() })
            newNodes.addAll(nodes.subList(range.first, range.last + 1).map { it.copy() })
            newList.add(newNodes)
        }

        return newList
    }

    fun checkRepeatPattern(types: String): String {

        (1..types.length / 2).reversed().forEach { length ->
            types.forEachIndexed { index, c ->
                if (index + length >= types.lastIndex) return@forEachIndexed
                val pattern = types.substring(index, index + length)
                if (Regex.fromLiteral(pattern).findAll(types).toList().size >= 2) {
                    println(pattern)
                    return pattern
                }
            }
        }
        return ""
    }

    private fun checkPreviousWordForCount(
        words: List<String>,
        unit: String,
        index: Int
    ): Pair<Int, Int>? {
        val default = 1 to index
        if (words[index] != unit) {
            val left = words[index].replace(unit, "")
            try {
                return left.toInt() to index
            } catch (e: Exception) {

            }
        }

        val prev = words.getOrNull(index - 1) ?: return default
        return try {
            prev.toInt() to index - 1
        } catch (e: Exception) {
            default
        }
    }


}
