package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsOffsetTime
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUByte
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.security.cert.Extension
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Trackpoint(
    val time: OffsetDateTime,
    val position: Position? = null,
    val altitude: Double? = null,
    val distance: Double? = null,
    val heartRate: UByte? = null,
    val cadence: Cadence? = null,
    val sensorState: SensorState? = null,
    val extensions: Extensions? = null,
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)

        xmlSerializer.startTag(namespace, ELEMENT_TIME)
        xmlSerializer.text(time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        xmlSerializer.endTag(namespace, ELEMENT_TIME)

        position?.serialize(xmlSerializer, ELEMENT_POSITION, namespace)

        altitude?.let {
            xmlSerializer.startTag(namespace, ELEMENT_ALTITUDE)
            xmlSerializer.text(it.toBigDecimal().toString())
            xmlSerializer.endTag(namespace, ELEMENT_ALTITUDE)
        }

        distance?.let {
            xmlSerializer.startTag(namespace, ELEMENT_DISTANCE)
            xmlSerializer.text(it.toBigDecimal().toString())
            xmlSerializer.endTag(namespace, ELEMENT_DISTANCE)
        }

        heartRate?.let {
            xmlSerializer.startTag(namespace, ELEMENT_HEART_RATE)
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, ELEMENT_HEART_RATE)
        }

        cadence?.serialize(xmlSerializer, ELEMENT_CADENCE, namespace)

        sensorState?.let {
            xmlSerializer.startTag(namespace, ELEMENT_SENSOR_STATE)
            xmlSerializer.text(it.identifier)
            xmlSerializer.endTag(namespace, ELEMENT_SENSOR_STATE)
        }

        extensions?.serialize(xmlSerializer, ELEMENT_EXTENSIONS, namespace)

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ELEMENT_TIME = "Time"
        private const val ELEMENT_POSITION = "Position"
        private const val ELEMENT_ALTITUDE = "AltitudeMeters"
        private const val ELEMENT_DISTANCE = "DistanceMeters"
        private const val ELEMENT_HEART_RATE = "HeartRateBpm"
        private const val ELEMENT_CADENCE = "Cadence"
        private const val ELEMENT_SENSOR_STATE = "SensorState"
        private const val ELEMENT_EXTENSIONS = "Extensions"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Trackpoint {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var time: OffsetDateTime? = null
            var position: Position? = null
            var altitude: Double? = null
            var distance: Double? = null
            var heartRate: UByte? = null
            var cadence: Cadence? = null
            var sensorState: SensorState? = null
            var extensions: Extensions? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_TIME -> {
                        time = TcxParser.readTextAsOffsetTime(parser, ELEMENT_TIME, namespace)
                    }
                    ELEMENT_POSITION -> {
                        position = Position.parse(parser, ELEMENT_POSITION, namespace, skip, loopMustContinue)
                    }
                    ELEMENT_ALTITUDE -> {
                        altitude = TcxParser.readTextAsDouble(parser, ELEMENT_ALTITUDE, namespace)
                    }
                    ELEMENT_DISTANCE -> {
                        distance = TcxParser.readTextAsDouble(parser, ELEMENT_DISTANCE, namespace)
                    }
                    ELEMENT_HEART_RATE -> {
                        heartRate = TcxParser.readTextAsUByte(parser, ELEMENT_HEART_RATE, namespace)
                    }
                    ELEMENT_CADENCE -> {
                        cadence = Cadence.parse(parser, ELEMENT_CADENCE, namespace, skip, loopMustContinue)
                    }
                    ELEMENT_SENSOR_STATE -> {
                        sensorState = try {
                            SensorState.values().first {
                                it.identifier == parser.getAttributeValue(namespace, ELEMENT_SENSOR_STATE)
                            }
                        } catch (_: NoSuchElementException) {
                            throw NoSuchElementException(
                                "Value of element $ELEMENT_SENSOR_STATE has to be one of ${SensorState.values()}."
                            )
                        }
                    }
                    ELEMENT_EXTENSIONS -> {
                        extensions = Extensions.parse(parser, ELEMENT_EXTENSIONS, namespace, skip, loopMustContinue)
                    }
                    else -> skip(parser)
                }
            }

            if (time == null) {
                throw NullPointerException("Element ELEMENT_TIME has to be set for '$elementName'.")
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return Trackpoint(
                time = time,
                position = position,
                altitude = altitude,
                distance = distance,
                heartRate = heartRate,
                cadence = cadence,
                sensorState = sensorState,
                extensions = extensions
            )
        }
    }
}
