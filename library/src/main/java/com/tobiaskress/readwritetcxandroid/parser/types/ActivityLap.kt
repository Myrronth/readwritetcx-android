package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readText
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUByte
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUInt
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ActivityLap(
    val startTime: OffsetDateTime,
    val totalTime: Double,
    val distance: Double,
    val maximumSpeed: Double? = null,
    val calories: UInt,
    val averageHeartRate: UByte? = null,
    val maximumHeartRate: UByte? = null,
    val intensity: Intensity,
    val cadence: Cadence? = null,
    val triggerMethod: TriggerMethod,
    val tracks: List<Track> = listOf(),
    val notes: String? = null,
    val extensions: Extensions? = null,
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)
        xmlSerializer.attribute(
            namespace,
            ATTRIBUTE_START_TIME,
            startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )

        xmlSerializer.startTag(namespace, ELEMENT_TOTAL_TIME)
        xmlSerializer.text(totalTime.toBigDecimal().toString())
        xmlSerializer.endTag(namespace, ELEMENT_TOTAL_TIME)

        xmlSerializer.startTag(namespace, ELEMENT_DISTANCE)
        xmlSerializer.text(distance.toBigDecimal().toString())
        xmlSerializer.endTag(namespace, ELEMENT_DISTANCE)

        maximumSpeed?.let {
            xmlSerializer.startTag(namespace, ELEMENT_MAXIMUM_SPEED)
            xmlSerializer.text(it.toBigDecimal().toString())
            xmlSerializer.endTag(namespace, ELEMENT_MAXIMUM_SPEED)
        }

        xmlSerializer.startTag(namespace, ELEMENT_CALORIES)
        xmlSerializer.text(calories.toString())
        xmlSerializer.endTag(namespace, ELEMENT_CALORIES)

        averageHeartRate?.let {
            xmlSerializer.startTag(namespace, ELEMENT_AVERAGE_HEART_RATE)
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, ELEMENT_AVERAGE_HEART_RATE)
        }

        maximumHeartRate?.let {
            xmlSerializer.startTag(namespace, ELEMENT_MAXIMUM_HEART_RATE)
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, ELEMENT_MAXIMUM_HEART_RATE)
        }

        xmlSerializer.startTag(namespace, ELEMENT_INTENSITY)
        xmlSerializer.text(intensity.identifier)
        xmlSerializer.endTag(namespace, ELEMENT_INTENSITY)

        cadence?.serialize(xmlSerializer, ELEMENT_CADENCE, namespace)

        xmlSerializer.startTag(namespace, ELEMENT_TRIGGER_METHOD)
        xmlSerializer.text(triggerMethod.identifier)
        xmlSerializer.endTag(namespace, ELEMENT_TRIGGER_METHOD)

        tracks.forEach {
            it.serialize(xmlSerializer, ELEMENT_TRACK, namespace)
        }

        notes?.let {
            xmlSerializer.startTag(namespace, ELEMENT_NOTES)
            xmlSerializer.text(it)
            xmlSerializer.endTag(namespace, ELEMENT_NOTES)
        }

        extensions?.serialize(xmlSerializer, ELEMENT_EXTENSIONS, namespace)

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ATTRIBUTE_START_TIME = "StartTime"
        private const val ELEMENT_TOTAL_TIME = "TotalTimeSeconds"
        private const val ELEMENT_DISTANCE = "DistanceMeters"
        private const val ELEMENT_MAXIMUM_SPEED = "MaximumSpeed"
        private const val ELEMENT_CALORIES = "Calories"
        private const val ELEMENT_AVERAGE_HEART_RATE = "AverageHeartRateBpm"
        private const val ELEMENT_MAXIMUM_HEART_RATE = "MaximumHeartRateBpm"
        private const val ELEMENT_INTENSITY = "Intensity"
        private const val ELEMENT_CADENCE = "Cadence"
        private const val ELEMENT_TRIGGER_METHOD = "TriggerMethod"
        private const val ELEMENT_TRACK = "Track"
        private const val ELEMENT_NOTES = "Notes"
        private const val ELEMENT_EXTENSIONS = "Extensions"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityLap {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            val startTime = OffsetDateTime.parse(
                parser.getAttributeValue(namespace, ATTRIBUTE_START_TIME)
            )
            var totalTime: Double? = null
            var distance: Double? = null
            var maximumSpeed: Double? = null
            var calories: UInt? = null
            var averageHeartRate: UByte? = null
            var maximumHeartRate: UByte? = null
            var intensity: Intensity? = null
            var cadence: Cadence? = null
            var triggerMethod: TriggerMethod? = null
            val tracks: MutableList<Track> = mutableListOf()
            var notes: String? = null
            var extensions: Extensions? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_TOTAL_TIME -> {
                        totalTime = TcxParser.readTextAsDouble(parser, ELEMENT_TOTAL_TIME, namespace)
                    }
                    ELEMENT_DISTANCE -> {
                        distance = TcxParser.readTextAsDouble(parser, ELEMENT_DISTANCE, namespace)
                    }
                    ELEMENT_MAXIMUM_SPEED -> {
                        maximumSpeed = TcxParser.readTextAsDouble(parser, ELEMENT_MAXIMUM_SPEED, namespace)
                    }
                    ELEMENT_CALORIES -> {
                        calories = TcxParser.readTextAsUInt(parser, ELEMENT_CALORIES, namespace)
                    }
                    ELEMENT_AVERAGE_HEART_RATE -> {
                        averageHeartRate = TcxParser.readTextAsUByte(parser, ELEMENT_AVERAGE_HEART_RATE, namespace)
                    }
                    ELEMENT_MAXIMUM_HEART_RATE -> {
                        maximumHeartRate = TcxParser.readTextAsUByte(parser, ELEMENT_MAXIMUM_HEART_RATE, namespace)
                    }
                    ELEMENT_INTENSITY -> {
                        try {
                            intensity = Intensity.values().first {
                                it.identifier == TcxParser.readText(parser, ELEMENT_INTENSITY, namespace)
                            }
                        } catch (_: NoSuchElementException) {
                            throw NoSuchElementException(
                                "Value of element $ELEMENT_INTENSITY has to be one of ${Intensity.values()}."
                            )
                        }
                    }
                    ELEMENT_CADENCE -> {
                        cadence = Cadence.parse(parser, ELEMENT_CADENCE, namespace, skip, loopMustContinue)
                    }
                    ELEMENT_TRIGGER_METHOD -> {
                        try {
                            triggerMethod = TriggerMethod.values().first {
                                it.identifier == TcxParser.readText(parser, ELEMENT_TRIGGER_METHOD, namespace)
                            }
                        } catch (_: NoSuchElementException) {
                            throw NoSuchElementException(
                                "Value of element $ELEMENT_TRIGGER_METHOD has to be one of ${TriggerMethod.values()}."
                            )
                        }
                    }
                    ELEMENT_TRACK -> {
                        tracks.add(Track.parse(parser, ELEMENT_TRACK, namespace, skip, loopMustContinue))
                    }
                    ELEMENT_NOTES -> {
                        notes = TcxParser.readText(parser, ELEMENT_NOTES, namespace)
                    }
                    ELEMENT_EXTENSIONS -> {
                        extensions = Extensions.parse(parser, ELEMENT_EXTENSIONS, namespace, skip, loopMustContinue)
                    }
                    else -> skip(parser)
                }
            }

            val nullValues: MutableList<String> = mutableListOf()
            if (totalTime == null) nullValues.add(ELEMENT_TOTAL_TIME)
            if (distance == null) nullValues.add(ELEMENT_DISTANCE)
            if (calories == null) nullValues.add(ELEMENT_CALORIES)
            if (intensity == null) nullValues.add(ELEMENT_INTENSITY)
            if (triggerMethod == null) nullValues.add(ELEMENT_TRIGGER_METHOD)

            if (nullValues.size > 0) {
                throw NullPointerException(
                    "Elements ${
                        nullValues.joinToString(", ", prefix = "'", postfix = "'")
                    } have to be set for '$elementName'."
                )
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return ActivityLap(
                startTime = startTime,
                totalTime = totalTime!!,
                distance = distance!!,
                maximumSpeed = maximumSpeed,
                calories = calories!!,
                averageHeartRate = averageHeartRate,
                maximumHeartRate = maximumHeartRate,
                intensity = intensity!!,
                cadence = cadence,
                triggerMethod = triggerMethod!!,
                tracks = tracks.toList(),
                notes = notes,
                extensions = extensions
            )
        }
    }
}
