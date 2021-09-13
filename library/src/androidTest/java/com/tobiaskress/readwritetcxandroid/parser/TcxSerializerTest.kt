package com.tobiaskress.readwritetcxandroid.parser

import org.junit.Test
import java.io.ByteArrayInputStream

class TcxSerializerTest {

    @Test
    fun testMorningRide() {
        val input = ReadWriteTcxTest.getAssets()!!.open("Morning_Ride.tcx")
        val tcx = TcxParser().parse(input)
        val serializedTcx = TcxSerializer().serialize(tcx)
        val serializedInput = ByteArrayInputStream(serializedTcx.toByteArray(Charsets.UTF_8))
        ReadWriteTcxTest.testMorningRide(serializedInput)
    }

    @Test
    fun testEyewear() {
        val input = ReadWriteTcxTest.getAssets()!!.open("30.08.2021, 08:40.tcx")
        val tcx = TcxParser().parse(input)
        val serializedTcx = TcxSerializer().serialize(tcx)
        val serializedInput = ByteArrayInputStream(serializedTcx.toByteArray(Charsets.UTF_8))
        ReadWriteTcxTest.testEyewear(serializedInput)
    }
}
