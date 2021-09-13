package com.tobiaskress.readwritetcxandroid.parser.types

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class ActivityList(
    val activities: List<Activity> = listOf()
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        xmlSerializer.startTag(namespace, elementName)

        activities.forEach {
            it.serialize(xmlSerializer, ELEMENT_ACTIVITY, namespace)
        }

        xmlSerializer.endTag(namespace, elementName)
    }

    companion object {

        private const val ELEMENT_ACTIVITY = "Activity"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): ActivityList {
            parser.require(XmlPullParser.START_TAG, namespace, elementName)

            val activities: MutableList<Activity> = mutableListOf()

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    ELEMENT_ACTIVITY -> {
                        activities.add(Activity.parse(parser, ELEMENT_ACTIVITY, namespace, skip, loopMustContinue))
                    }
                    else -> skip(parser)
                }
            }

            parser.require(XmlPullParser.END_TAG, namespace, elementName)

            return ActivityList(
                activities = activities.toList()
            )
        }
    }
}
