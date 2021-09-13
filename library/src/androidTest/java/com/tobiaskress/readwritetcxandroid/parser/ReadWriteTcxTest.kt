package com.tobiaskress.readwritetcxandroid.parser

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tobiaskress.readwritetcxandroid.parser.types.Intensity
import com.tobiaskress.readwritetcxandroid.parser.types.Sport
import com.tobiaskress.readwritetcxandroid.parser.types.TriggerMethod
import org.junit.Assert
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.time.OffsetDateTime
import java.time.ZoneOffset

object ReadWriteTcxTest {

    @Throws(IOException::class, XmlPullParserException::class)
    fun testMorningRide(input: InputStream) {
        val tcx = TcxParser().parse(input)

        Assert.assertNotNull(tcx)

        // Check Activity

        val activities = tcx.activities?.activities
        Assert.assertNotNull(activities)
        Assert.assertEquals(1, activities!!.size)

        val activity = activities.first()

        Assert.assertEquals(Sport.BIKING, activity.sport)
        Assert.assertEquals(OffsetDateTime.of(
            2020,
            11,
            23,
            6,
            23,
            17,
            0,
            ZoneOffset.UTC
        ), activity.id)
        Assert.assertNull(activity.notes)

        // Lap
        val lap = activity.laps.first()

        Assert.assertEquals(OffsetDateTime.of(
            2020,
            11,
            23,
            6,
            23,
            17,
            0,
            ZoneOffset.UTC
        ), lap.startTime)
        Assert.assertEquals(44.0, lap.totalTime, 0.0)
        Assert.assertEquals(32.22, lap.distance, 0.005)
        Assert.assertEquals(6840.0, lap.maximumSpeed!!, 0.005)
        Assert.assertEquals(0u, lap.calories)
        Assert.assertEquals(Intensity.ACTIVE, lap.intensity)
        Assert.assertEquals(TriggerMethod.MANUAL, lap.triggerMethod)

        // Track and trackpoints
        val trackpoints = lap.tracks.first().points

        val lastTrackpoint = trackpoints.last()
        Assert.assertEquals(OffsetDateTime.of(
            2020,
            11,
            23,
            6,
            24,
            2,
            0,
            ZoneOffset.UTC
        ), lastTrackpoint.time)
        Assert.assertEquals(49.465, lastTrackpoint.position!!.latitude.decimalDegrees, 0.005)
        Assert.assertEquals(11.083, lastTrackpoint.position!!.longitude.decimalDegrees, 0.005)
        Assert.assertEquals(323.8, lastTrackpoint.altitude!!, 0.005)
        Assert.assertEquals(32.2, lastTrackpoint.distance!!, 0.005)
        Assert.assertEquals(0.6, lastTrackpoint.extensions!!.trackpointExtension!!.speed!!, 0.005)

        // Extension
        Assert.assertNull(lap.extensions)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun testEyewear(input: InputStream) {
        val tcx = TcxParser().parse(input)

        Assert.assertNotNull(tcx)

        // Check Activity

        val activities = tcx.activities?.activities
        Assert.assertNotNull(activities)
        Assert.assertEquals(1, activities!!.size)

        val activity = activities.first()

        Assert.assertEquals(Sport.BIKING, activity.sport)
        Assert.assertEquals(OffsetDateTime.of(
            2021,
            8,
            30,
            6,
            40,
            57,
            0,
            ZoneOffset.UTC
        ), activity.id)
        Assert.assertEquals(1, activity.laps.size)
        Assert.assertEquals("", activity.notes)

        // Lap
        val lap = activity.laps.first()

        Assert.assertEquals(OffsetDateTime.of(
            2021,
            8,
            30,
            6,
            40,
            57,
            0,
            ZoneOffset.UTC
        ), lap.startTime)
        Assert.assertEquals(4.0, lap.totalTime, 0.0)
        Assert.assertEquals(29.296, lap.distance, 0.005)
        Assert.assertEquals(1.793, lap.maximumSpeed!!, 0.005)
        Assert.assertEquals(0u, lap.calories)
        Assert.assertEquals(Intensity.ACTIVE, lap.intensity)
        Assert.assertEquals(TriggerMethod.MANUAL, lap.triggerMethod)

        // Track and trackpoints
        val trackpoints = lap.tracks.first().points
        Assert.assertEquals(3, trackpoints.size)

        val lastTrackpoint = trackpoints.last()
        Assert.assertEquals(OffsetDateTime.of(
            2021,
            8,
            30,
            6,
            41,
            5,
            0,
            ZoneOffset.UTC
        ), lastTrackpoint.time)
        Assert.assertEquals(49.465, lastTrackpoint.position!!.latitude.decimalDegrees, 0.005)
        Assert.assertEquals(11.083, lastTrackpoint.position!!.longitude.decimalDegrees, 0.005)
        Assert.assertEquals(319.437, lastTrackpoint.altitude!!, 0.005)
        Assert.assertEquals(13.892, lastTrackpoint.distance!!, 0.005)
        Assert.assertEquals(1.773, lastTrackpoint.extensions!!.trackpointExtension!!.speed!!, 0.005)

        // Extension
        Assert.assertEquals(1.648, lap.extensions!!.lapExtension!!.averageSpeed!!, 0.005)
    }

    internal fun getAssets(): AssetManager? {
        return InstrumentationRegistry.getInstrumentation().context.assets
    }
}
