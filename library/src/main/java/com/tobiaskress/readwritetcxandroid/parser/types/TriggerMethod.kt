package com.tobiaskress.readwritetcxandroid.parser.types

@Suppress("Unused")
enum class TriggerMethod(val identifier: String) {
    MANUAL("Manual"),
    DISTANCE("Distance"),
    LOCATION("Location"),
    TIME("Time"),
    HEARTRATE("Heartrate")
}
