package com.tobiaskress.readwritetcxandroid.parser.types

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class TrainingCenterDatabase(
    val activities: ActivityList?
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)
        xmlSerializer.attribute(namespace, "xmlns", "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2")
        xmlSerializer.attribute(namespace, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")

        xmlSerializer.attribute(namespace,
            "xmlns:$USER_PROFILE_NAMESPACE",
            "http://www.garmin.com/xmlschemas/UserProfile/v2"
        )

        xmlSerializer.attribute(namespace,
            "xmlns:$ACTIVITY_EXTENSION_NAMESPACE",
            "http://www.garmin.com/xmlschemas/ActivityExtension/v2"
        )

        xmlSerializer.attribute(namespace,
            "xmlns:$PROFILE_NAMESPACE",
            "http://www.garmin.com/xmlschemas/ProfileExtension/v1"
        )

        xmlSerializer.attribute(namespace,
            "xmlns:$ACTIVITY_GOALS_NAMESPACE",
            "http://www.garmin.com/xmlschemas/ActivityGoals/v1"
        )

        xmlSerializer.attribute(
            namespace,
            "xsi:schemaLocation",
            "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 " +
                    "http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd"
        )

        activities?.serialize(xmlSerializer, ELEMENT_ACTIVITIES, namespace)

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        const val USER_PROFILE_NAMESPACE = "ns2"
        const val ACTIVITY_EXTENSION_NAMESPACE = "ns3"
        const val PROFILE_NAMESPACE = "ns4"
        const val ACTIVITY_GOALS_NAMESPACE = "ns5"

        private const val ELEMENT_ACTIVITIES = "Activities"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): TrainingCenterDatabase {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            var activities: ActivityList? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_ACTIVITIES -> {
                        activities = ActivityList.parse(parser, ELEMENT_ACTIVITIES, namespace, skip, loopMustContinue)
                    }
                    else -> skip(parser)
                }
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return TrainingCenterDatabase(
                activities = activities
            )
        }
    }
}
