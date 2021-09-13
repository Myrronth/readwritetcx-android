package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class Position(
    val latitude: Latitude,
    val longitude: Longitude
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)

        xmlSerializer.startTag(namespace, ELEMENT_LATITUDE)
        xmlSerializer.text(latitude.decimalDegrees.toBigDecimal().toPlainString())
        xmlSerializer.endTag(namespace, ELEMENT_LATITUDE)

        xmlSerializer.startTag(namespace, ELEMENT_LONGITUDE)
        xmlSerializer.text(longitude.decimalDegrees.toBigDecimal().toPlainString())
        xmlSerializer.endTag(namespace, ELEMENT_LONGITUDE)

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ELEMENT_LATITUDE = "LatitudeDegrees"
        private const val ELEMENT_LONGITUDE = "LongitudeDegrees"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Position {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var latitude: Latitude? = null
            var longitude: Longitude? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_LATITUDE -> {
                        latitude = Latitude(TcxParser.readTextAsDouble(parser, ELEMENT_LATITUDE, namespace))
                    }
                    ELEMENT_LONGITUDE -> {
                        longitude = Longitude(TcxParser.readTextAsDouble(parser, ELEMENT_LONGITUDE, namespace))
                    }
                    else -> skip(parser)
                }
            }

            val nullValues: MutableList<String> = mutableListOf()
            if (latitude == null) nullValues.add(ELEMENT_LATITUDE)
            if (longitude == null) nullValues.add(ELEMENT_LONGITUDE)

            if (nullValues.size > 0) {
                throw NullPointerException(
                    "Elements ${
                        nullValues.joinToString(", ", prefix = "'", postfix = "'")
                    } have to be set for '$elementName'."
                )
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return Position(
                latitude = latitude!!,
                longitude = longitude!!
            )
        }
    }
}
