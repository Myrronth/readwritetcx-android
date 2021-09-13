package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsDouble
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class Cadence(
    val low: Double,
    val high: Double
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)

        xmlSerializer.startTag(namespace, ELEMENT_LOW)
        xmlSerializer.text(low.toBigDecimal().toPlainString())
        xmlSerializer.endTag(namespace, ELEMENT_LOW)

        xmlSerializer.startTag(namespace, ELEMENT_HIGH)
        xmlSerializer.text(high.toBigDecimal().toPlainString())
        xmlSerializer.endTag(namespace, ELEMENT_HIGH)

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ELEMENT_LOW = "Low"
        private const val ELEMENT_HIGH = "High"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Cadence {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var low: Double? = null
            var high: Double? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_LOW -> {
                        low = TcxParser.readTextAsDouble(parser, ELEMENT_LOW, namespace)
                    }
                    ELEMENT_HIGH -> {
                        high = TcxParser.readTextAsDouble(parser, ELEMENT_HIGH, namespace)
                    }
                    else -> skip(parser)
                }
            }

            val nullValues: MutableList<String> = mutableListOf()
            if (low == null) nullValues.add(ELEMENT_LOW)
            if (high == null) nullValues.add(ELEMENT_HIGH)

            if (nullValues.size > 0) {
                throw NullPointerException(
                    "Elements ${
                        nullValues.joinToString(", ", prefix = "'", postfix = "'")
                    } have to be set for '$elementName'."
                )
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return Cadence(
                low = low!!,
                high = high!!
            )
        }
    }
}
