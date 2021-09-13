package com.tobiaskress.readwritetcxandroid.parser

import android.util.Xml
import com.tobiaskress.readwritetcxandroid.parser.types.TrainingCenterDatabase
import java.io.StringWriter

/**
 * [TcxSerializer] currently supports only the following TCX tags listed in
 * [Training Center Database v2](https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd) schema:
 *
 * - Activities
 *     - Activity
 *         - Sport
 *         - Id
 *         - Lap
 *         - Notes
 */
class TcxSerializer {

    private val namespace: String? = null

    fun serialize(tcx: TrainingCenterDatabase): String {
        val xmlSerializer = Xml.newSerializer()
        val stringWriter = StringWriter()

        xmlSerializer.setOutput(stringWriter)

        xmlSerializer.startDocument("UTF-8", null)
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

        tcx.serialize(xmlSerializer, ROOT_ELEMENT, namespace)

        xmlSerializer.endDocument()

        return stringWriter.toString()
    }

}
