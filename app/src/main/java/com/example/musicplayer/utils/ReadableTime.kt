package com.example.musicplayer.utils

fun readableTime(dur: Long): String {
    val hrs = dur / (1000 * 60 * 60)
    val min = (dur % (1000 * 60 * 60)) / (1000 * 60)
    val sec = (((dur % (1000 * 60 * 60)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000
    return if (hrs < 1) {
        ("$min:$sec")
    } else {
        ("$hrs:$min:$sec")
    }
}
