package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUByte
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUShort
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class ActivityLapExtension(
    val averageSpeed: Double? = null,
    val maxBikeCadence: UByte? = null,
    val averageRunCadence: UByte? = null,
    val maxRunCadence: UByte? = null,
    val steps: UShort? = null,
    val averageWatts: UShort? = null,
    val maxWatts: UShort? = null,
    val extensions: Extensions? = null,
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        prefix: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, "$prefix:$elementName")

        averageSpeed?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_AVERAGE_SPEED")
            xmlSerializer.text(it.toBigDecimal().toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_AVERAGE_SPEED")
        }

        maxBikeCadence?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_MAX_BIKE_CADENCE")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_MAX_BIKE_CADENCE")
        }

        averageRunCadence?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_AVERAGE_RUN_CADENCE")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_AVERAGE_RUN_CADENCE")
        }

        maxRunCadence?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_MAX_RUN_CADENCE")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_MAX_RUN_CADENCE")
        }

        steps?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_STEPS")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_STEPS")
        }

        averageWatts?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_AVERAGE_WATTS")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_AVERAGE_WATTS")
        }

        maxWatts?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_MAX_WATTS")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_MAX_WATTS")
        }

        extensions?.serialize(xmlSerializer, ELEMENT_EXTENSIONS, prefix, namespace)

        xmlSerializer.endTag(namespace, "$prefix:$elementName")
    }

    companion object {

        private const val ELEMENT_AVERAGE_SPEED = "AvgSpeed"
        private const val ELEMENT_MAX_BIKE_CADENCE = "MaxBikeCadence"
        private const val ELEMENT_AVERAGE_RUN_CADENCE = "AvgRunCadence"
        private const val ELEMENT_MAX_RUN_CADENCE = "MaxRunCandence"
        private const val ELEMENT_STEPS = "Steps"
        private const val ELEMENT_AVERAGE_WATTS = "AvgWatts"
        private const val ELEMENT_MAX_WATTS = "MaxWatts"
        private const val ELEMENT_EXTENSIONS = "Extensions"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityLapExtension {
            return parse(parser, elementName, null, namespace, skip, loopMustContinue)
        }

        @Suppress("LongMethod", "LongParameterList")
        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            prefix: String?,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityLapExtension {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var averageSpeed: Double? = null
            var maxBikeCadence: UByte? = null
            var averageRunCadence: UByte? = null
            var maxRunCadence: UByte? = null
            var steps: UShort? = null
            var averageWatts: UShort? = null
            var maxWatts: UShort? = null
            var extensions: Extensions? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "$prefix:$ELEMENT_AVERAGE_SPEED",
                    ELEMENT_AVERAGE_SPEED -> {
                        averageSpeed = TcxParser.readTextAsDouble(parser, ELEMENT_AVERAGE_SPEED, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_MAX_BIKE_CADENCE",
                    ELEMENT_MAX_BIKE_CADENCE -> {
                        maxBikeCadence = TcxParser.readTextAsUByte(parser, ELEMENT_MAX_BIKE_CADENCE, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_AVERAGE_RUN_CADENCE",
                    ELEMENT_AVERAGE_RUN_CADENCE -> {
                        averageRunCadence = TcxParser.readTextAsUByte(
                            parser,
                            ELEMENT_AVERAGE_RUN_CADENCE,
                            prefix,
                            namespace
                        )
                    }
                    "$prefix:$ELEMENT_MAX_RUN_CADENCE",
                    ELEMENT_MAX_RUN_CADENCE -> {
                        maxRunCadence = TcxParser.readTextAsUByte(parser, ELEMENT_MAX_RUN_CADENCE, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_STEPS",
                    ELEMENT_STEPS -> {
                        steps = TcxParser.readTextAsUShort(parser, ELEMENT_STEPS, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_AVERAGE_WATTS",
                    ELEMENT_AVERAGE_WATTS -> {
                        averageWatts = TcxParser.readTextAsUShort(parser, ELEMENT_AVERAGE_WATTS, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_MAX_WATTS",
                    ELEMENT_MAX_WATTS -> {
                        maxWatts = TcxParser.readTextAsUShort(parser, ELEMENT_MAX_WATTS, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_EXTENSIONS",
                    ELEMENT_EXTENSIONS -> {
                        extensions = Extensions.parse(
                            parser,
                            ELEMENT_EXTENSIONS,
                            prefix,
                            namespace,
                            skip,
                            loopMustContinue
                        )
                    }
                    else -> skip(parser)
                }
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return ActivityLapExtension(
                averageSpeed = averageSpeed,
                maxBikeCadence = maxBikeCadence,
                averageRunCadence = averageRunCadence,
                maxRunCadence = maxRunCadence,
                steps = steps,
                averageWatts = averageWatts,
                maxWatts = maxWatts,
                extensions = extensions
            )
        }
    }
}
