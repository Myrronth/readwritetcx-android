package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import com.tobiaskress.readwritetcxandroid.parser.helper.readText
import com.tobiaskress.readwritetcxandroid.parser.helper.readTextAsOffsetTime
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Activity(
    val sport: Sport,
    val id: OffsetDateTime,
    val laps: List<ActivityLap> = listOf(),
    val notes: String? = null
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)
        xmlSerializer.attribute(namespace, ATTRIBUTE_SPORT, sport.identifier)

        xmlSerializer.startTag(namespace, ELEMENT_ID)
        xmlSerializer.text(id.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        xmlSerializer.endTag(namespace, ELEMENT_ID)

        laps.forEach {
            it.serialize(xmlSerializer, ELEMENT_LAP, namespace)
        }

        notes?.let {
            xmlSerializer.startTag(namespace, ELEMENT_NOTES)
            xmlSerializer.text(it)
            xmlSerializer.endTag(namespace, ELEMENT_NOTES)
        }

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ATTRIBUTE_SPORT = "Sport"
        private const val ELEMENT_ID = "Id"
        private const val ELEMENT_LAP = "Lap"
        private const val ELEMENT_NOTES = "Notes"

        @Suppress("ThrowsCount")
        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Activity {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            val sport = try {
                Sport.values().first {
                    it.identifier == parser.getAttributeValue(namespace, ATTRIBUTE_SPORT)
                }
            } catch (_: NoSuchElementException) {
                throw NoSuchElementException("Value of attribute $ATTRIBUTE_SPORT has to be one of ${Sport.values()}.")
            }

            var id: OffsetDateTime? = null
            val laps: MutableList<ActivityLap> = mutableListOf()
            var notes: String? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_ID -> {
                        id = TcxParser.readTextAsOffsetTime(parser, ELEMENT_ID, namespace)
                    }
                    ELEMENT_LAP -> {
                        laps.add(ActivityLap.parse(parser, ELEMENT_LAP, namespace, skip, loopMustContinue))
                    }
                    ELEMENT_NOTES -> {
                        notes = TcxParser.readText(parser, ELEMENT_NOTES, namespace)
                    }
                    else -> skip(parser)
                }
            }

            if (id == null) {
                throw NullPointerException("Element $ELEMENT_ID has to be set for '$elementName'.")
            }

            if (laps.size < 1) {
                throw NullPointerException("There has to be at least one element $ELEMENT_LAP.")
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return Activity(
                sport = sport,
                id = id,
                laps = laps.toList(),
                notes = notes
            )
        }
    }
}
