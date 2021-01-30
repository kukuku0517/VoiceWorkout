package com.example.util

fun Any.tag(): String {
    return this.javaClass.simpleName
}