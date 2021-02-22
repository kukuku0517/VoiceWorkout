package com.example.util


fun Any.tag(): String {
    return this.javaClass.simpleName
}

fun <T> List<T>.replaceIf(other: T, unit: (T) -> Boolean): List<T> {
    val ml = this.toMutableList()
    val it = this.iterator()
    var idx = 0
    while (it.hasNext()) {
        val temp = it.next()
        if (unit.invoke(temp)) {
            ml.remove(temp)
            ml.add(idx, other)
            break
        }
        idx++
    }

    return ml.toList()
}