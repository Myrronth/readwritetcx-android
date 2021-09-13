package com.tobiaskress.readwritetcxandroid.parser

import androidx.test.filters.LargeTest
import org.junit.Test

@LargeTest
internal class TcxParserTest {

    @Test
    fun testMorningRide() {
        val input = ReadWriteTcxTest.getAssets()!!.open("Morning_Ride.tcx")
        ReadWriteTcxTest.testMorningRide(input)
    }

    @Test
    fun testEyewear() {
        val input = ReadWriteTcxTest.getAssets()!!.open("30.08.2021, 08:40.tcx")
        ReadWriteTcxTest.testEyewear(input)
    }
}
