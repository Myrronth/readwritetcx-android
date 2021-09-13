package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUByte
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsUShort
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class ActivityTrackpointExtension(
    val cadenceSensor: CadenceSensor? = null,
    val speed: Double? = null,
    val runCadence: UByte? = null,
    val watts: UShort? = null,
    val extensions: Extensions? = null,
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        prefix: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, "$prefix:$elementName")

        cadenceSensor?.let {
            xmlSerializer.attribute(namespace, ATTRIBUTE_CADENCE_SENSOR, it.identifier)
        }

        speed?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_SPEED")
            xmlSerializer.text(it.toBigDecimal().toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_SPEED")
        }

        runCadence?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_RUN_CADENCE")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_RUN_CADENCE")
        }

        watts?.let {
            xmlSerializer.startTag(namespace, "$prefix:$ELEMENT_WATTS")
            xmlSerializer.text(it.toString())
            xmlSerializer.endTag(namespace, "$prefix:$ELEMENT_WATTS")
        }

        extensions?.serialize(xmlSerializer, ELEMENT_EXTENSIONS, prefix, namespace)

        xmlSerializer.endTag(namespace, "$prefix:$elementName")
    }

    companion object {

        private const val ATTRIBUTE_CADENCE_SENSOR = "CadenceSensor"
        private const val ELEMENT_SPEED = "Speed"
        private const val ELEMENT_RUN_CADENCE = "RunCadence"
        private const val ELEMENT_WATTS = "Watts"
        private const val ELEMENT_EXTENSIONS = "Extensions"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityTrackpointExtension {
            return parse(parser, elementName, null, namespace, skip, loopMustContinue)
        }

        @Suppress("LongParameterList")
        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            prefix: String?,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityTrackpointExtension {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var cadenceSensor: CadenceSensor? = null
            var speed: Double? = null
            var runCadence: UByte? = null
            var watts: UShort? = null
            var extensions: Extensions? = null

            parser.getAttributeValue(namespace, ATTRIBUTE_CADENCE_SENSOR)?.let { identifier ->
                cadenceSensor = try {
                    CadenceSensor.values().first {
                        it.identifier == identifier
                    }
                } catch (_: NoSuchElementException) {
                    throw NoSuchElementException(
                        "Value of attribute $ATTRIBUTE_CADENCE_SENSOR has to be one of ${CadenceSensor.values()}."
                    )
                }
            }

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "$prefix:$ELEMENT_SPEED",
                    ELEMENT_SPEED -> {
                        speed = TcxParser.readTextAsDouble(parser, ELEMENT_SPEED, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_RUN_CADENCE",
                    ELEMENT_RUN_CADENCE -> {
                        runCadence = TcxParser.readTextAsUByte(parser, ELEMENT_RUN_CADENCE, prefix, namespace)
                    }
                    "$prefix:$ELEMENT_WATTS",
                    ELEMENT_WATTS -> {
                        watts = TcxParser.readTextAsUShort(parser, ELEMENT_WATTS, prefix, namespace)
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

            return ActivityTrackpointExtension(
                cadenceSensor = cadenceSensor,
                speed = speed,
                runCadence = runCadence,
                watts = watts,
                extensions = extensions
            )
        }
    }
}
