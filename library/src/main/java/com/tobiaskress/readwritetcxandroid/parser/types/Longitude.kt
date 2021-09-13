package com.tobiaskress.readwritetcxandroid.parser.types

import com.tobiaskress.readwritetcxandroid.parser.exceptions.NumberOutOfBoundsException

private const val MIN = -180.0
private const val MAX = 180.0

/**
 * The longitude of the point. Decimal degrees, WGS84 datum.
 *
 * @throws NumberOutOfBoundsException When the longitude is less than -180.0 or greater than 180.0 degrees.
 */
class Longitude @Throws(NumberOutOfBoundsException::class) constructor(decimalDegrees: Double) {
    var decimalDegrees: Double = decimalDegrees
        private set(value) {
            if (value < MIN) {
                throw NumberOutOfBoundsException("longitude must be at least $MIN decimal degrees.")
            }

            if (value > MAX) {
                throw NumberOutOfBoundsException("longitude must be at most $MAX decimal degrees.")
            }

            field = value
        }

    //region Mimic data class

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Longitude) return false
        if (decimalDegrees != other.decimalDegrees) return false

        return true
    }

    override fun hashCode(): Int {
        return decimalDegrees.hashCode()
    }

    override fun toString(): String = """
    |Longitude [
    |  longitude: $decimalDegrees
    |]
    """.trimMargin()

    //endregion
}
