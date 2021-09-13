package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.exceptions.NumberOutOfBoundsException

private const val MIN = -90.0
private const val MAX = 90.0

/**
 * The latitude of the point. Decimal degrees, WGS84 datum.
 *
 * @throws NumberOutOfBoundsException When the latitude is less than -90.0 or greater than 90.0 degrees.
 */
class Latitude @Throws(NumberOutOfBoundsException::class) constructor(decimalDegrees: Double) {
    var decimalDegrees: Double = decimalDegrees
        private set(value) {
            if (value < MIN) {
                throw NumberOutOfBoundsException("latitude has to be at least $MIN decimal degrees.")
            }

            if (value > MAX) {
                throw NumberOutOfBoundsException("latitude has to be at most $MAX decimal degrees.")
            }

            field = value
        }

    //region Mimic data class

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Latitude) return false
        if (decimalDegrees != other.decimalDegrees) return false

        return true
    }

    override fun hashCode(): Int {
        return decimalDegrees.hashCode()
    }

    override fun toString(): String = """
    |Latitude [
    |  latitude: $decimalDegrees
    |]
    """.trimMargin()

    //endregion
}
