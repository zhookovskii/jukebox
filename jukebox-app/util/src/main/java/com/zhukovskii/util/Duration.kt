package com.zhukovskii.util

typealias Seconds = Int

typealias Milliseconds = Int

fun Seconds.toMilliseconds() = this * 1000

fun Milliseconds.toSeconds() = this / 1000

fun Seconds.toSongDurationText(): String {
    val minutes = "${this / 60}".justifyLeft(2, "0")
    val seconds = "${this % 60}".justifyLeft(2, "0")
    return "$minutes:$seconds"
}