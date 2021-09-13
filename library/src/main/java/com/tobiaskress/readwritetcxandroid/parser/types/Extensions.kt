package com.tobiaskress.readwritetcxandroid.parser.types

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer

data class Extensions(
    val trackpointExtension: ActivityTrackpointExtension? = null,
    val lapExtension: ActivityLapExtension? = null
) {

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        namespace: String?
    ) {
        serialize(xmlSerializer, elementName, null, namespace)
    }

    internal fun serialize(
        xmlSerializer: XmlSerializer,
        elementName: String,
        prefix: String?,
        namespace: String?
    ) {
        val prefixedElementName = if (prefix != null) "$prefix:$elementName" else elementName

        xmlSerializer.startTag(namespace, prefixedElementName)

        trackpointExtension?.serialize(
            xmlSerializer,
            ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION,
            TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE,
            namespace
        )

        lapExtension?.serialize(
            xmlSerializer,
            ELEMENT_ACTIVITY_LAP_EXTENSION,
            TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE,
            namespace
        )

        xmlSerializer.endTag(namespace, prefixedElementName)
    }

    companion object {

        private const val ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION = "TPX"
        private const val ELEMENT_ACTIVITY_LAP_EXTENSION = "LX"

        internal fun parse(
            parser: XmlPullParser,
            elementName: String,
            namespace: String?,
            skip: (parser: XmlPullParser) -> Unit,
            loopMustContinue: (next: Int) -> Boolean
        ): Extensions {
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
        ): Extensions {
            val prefixedElementName = if (prefix != null) "$prefix:$elementName" else elementName

            parser.require(XmlPullParser.START_TAG, namespace, prefixedElementName)

            var trackpointExtension: ActivityTrackpointExtension? = null
            var lapExtension: ActivityLapExtension? = null

            while (loopMustContinue(parser.next())) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                when (parser.name) {
                    "${TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE}:$ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION" -> {
                        trackpointExtension = ActivityTrackpointExtension.parse(
                            parser,
                            ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION,
                            TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE,
                            namespace,
                            skip,
                            loopMustContinue
                        )
                    }
                    ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION -> {
                        trackpointExtension = ActivityTrackpointExtension.parse(
                            parser,
                            ELEMENT_ACTIVITY_TRACKPOINT_EXTENSION,
                            namespace,
                            skip,
                            loopMustContinue
                        )
                    }

                    "${TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE}:$ELEMENT_ACTIVITY_LAP_EXTENSION" -> {
                        lapExtension = ActivityLapExtension.parse(
                            parser,
                            ELEMENT_ACTIVITY_LAP_EXTENSION,
                            TrainingCenterDatabase.ACTIVITY_EXTENSION_NAMESPACE,
                            namespace,
                            skip,
                            loopMustContinue
                        )
                    }
                    ELEMENT_ACTIVITY_LAP_EXTENSION -> {
                        lapExtension = ActivityLapExtension.parse(
                            parser,
                            ELEMENT_ACTIVITY_LAP_EXTENSION,
                            namespace,
                            skip,
                            loopMustContinue
                        )
                    }

                    else -> skip(parser)
                }
            }

            parser.require(XmlPullParser.END_TAG, namespace, prefixedElementName)

            return Extensions(
                trackpointExtension = trackpointExtension,
                lapExtension = lapExtension
            )
        }
    }
}
