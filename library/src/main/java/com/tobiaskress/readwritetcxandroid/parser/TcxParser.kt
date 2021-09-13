@file:Suppress("UnusedPrivateMember", "Unused")

package com.tobiaskress.readwritetcxandroid.parser

import android.util.Xml
import com.tobiaskress.readwritetcxandroid.parser.types.TrainingCenterDatabase
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * [TcxParser] currently supports only the following TCX tags listed in
 * [Training Center Database v2](https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd) schema:
 *
 * - Activities
 *     - Activity
 *         - Sport
 *         - Id
 *         - Lap
 *         - Notes
 */
class TcxParser {

    private val namespace: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): TrainingCenterDatabase {
        inputStream.use { input ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            parser.setInput(input, null)
            parser.nextTag()

            return TrainingCenterDatabase.parse(parser, ROOT_ELEMENT, namespace, { skip(it) }, { loopMustContinue(it) })
        }
    }

    private fun loopMustContinue(next: Int): Boolean {
        return next != XmlPullParser.END_TAG && next != XmlPullParser.END_DOCUMENT
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException("Expected ${XmlPullParser.START_TAG} but got ${parser.eventType}.")
        }

        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    companion object
}
