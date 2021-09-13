package com.tobiaskress.readwritetcxandroid.parser.types

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class Track(
    val points: List<Trackpoint> = listOf()
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)

        points.forEach {
            it.serialize(xmlSerializer, ELEMENT_POINT, namespace)
        }

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ELEMENT_POINT = "Trackpoint"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Track {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            val points: MutableList<Trackpoint> = mutableListOf()

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_POINT -> {
                        points.add(Trackpoint.parse(parser, ELEMENT_POINT, namespace, skip, loopMustContinue))
                    }
                    else -> skip(parser)
                }
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return Track(
                points = points.toList()
            )
        }
    }
}
