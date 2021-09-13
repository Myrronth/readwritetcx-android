package com.tobiaskress.readwritetcxandroid.parser.helper

import android.net.Uri
import com.tobiaskress.readwritetcxandroid.parser.TcxParser
import org.xmlpull.v1.XmlPullParser
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private fun TcxParser.Companion.readText(parser: XmlPullParser): String {
    var result = ""

    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }

    return result
}

internal fun TcxParser.Companion.readText(
    parser: XmlPullParser,
    elementName: String,
    namespace: String?
): String {
    parser.require(XmlPullParser.START_TAG, namespace, elementName)
    val text = TcxParser.readText(parser)
    parser.require(XmlPullParser.END_TAG, namespace, elementName)

    return text
}

internal fun TcxParser.Companion.readTextAsUByte(
    parser: XmlPullParser,
    elementName: String,
    namespace: String?
): UByte {
    return readTextAsUByte(parser, elementName, null, namespace)
}

internal fun TcxParser.Companion.readTextAsUByte(
    parser: XmlPullParser,
    elementName: String,
    prefix: String?,
    namespace: String?
): UByte {
    val prefixedElementName = if (prefix != null) "$prefix:$elementName" else elementName

    parser.require(XmlPullParser.START_TAG, namespace, prefixedElementName)
    val uByte = TcxParser.readText(parser).toUByte()
    parser.require(XmlPullParser.END_TAG, namespace, prefixedElementName)

    return uByte
}

internal fun TcxParser.Companion.readTextAsUShort(
    parser: XmlPullParser,
    elementName: String,
    prefix: String?,
    namespace: String?
): UShort {
    val prefixedElementName = if (prefix != null) "$prefix:$elementName" else elementName

    parser.require(XmlPullParser.START_TAG, namespace, prefixedElementName)
    val uShort = TcxParser.readText(parser).toUShort()
    parser.require(XmlPullParser.END_TAG, namespace, prefixedElementName)

    return uShort
}

internal fun TcxParser.Companion.readTextAsUInt(
    parser: XmlPullParser,
    elementName: String,
    namespace: String?
): UInt {
    parser.require(XmlPullParser.START_TAG, namespace, elementName)
    val uInt = TcxParser.readText(parser).toUInt()
    parser.require(XmlPullParser.END_TAG, namespace, elementName)

    return uInt
}

internal fun TcxParser.Companion.readTextAsDouble(
    parser: XmlPullParser,
    elementName: String,
    namespace: String?
): Double {
    return readTextAsDouble(parser, elementName, null, namespace)
}

internal fun TcxParser.Companion.readTextAsDouble(
    parser: XmlPullParser,
    elementName: String,
    prefix: String?,
    namespace: String?
): Double {
    val prefixedElementName = if (prefix != null) "$prefix:$elementName" else elementName

    parser.require(XmlPullParser.START_TAG, namespace, prefixedElementName)
    val double = TcxParser.readText(parser).toDouble()
    parser.require(XmlPullParser.END_TAG, namespace, prefixedElementName)

    return double
}

internal fun TcxParser.Companion.readTextAsOffsetTime(
    parser: XmlPullParser,
    elementName: String,
    namespace: String?
): OffsetDateTime {
    parser.require(XmlPullParser.START_TAG, namespace, elementName)
    val offsetDateTime = OffsetDateTime.parse(TcxParser.readText(parser))
    parser.require(XmlPullParser.END_TAG, namespace, elementName)

    return offsetDateTime
}
