package com.nagl.smartgeoapp.database.entity

import java.io.Serializable

/**
 * класс данных эллипсоид
 */
data class Ellipsoid(
    var ellipsoidName: String = "WGS84", //название эллипсоида
    var bHalfShaft: Double = 6378137.0, //главная полуось
    var compression: Double = 298.2572235630, //сжатие
    var axisDirection: String = NORTH_EAST, //направление осей
    var isAzimuthOnSouth: Boolean = false //азимут на юг
) : Serializable {

    companion object {
        const val ELLIPSOID = "ellipsoid"

        const val NORTH_EAST = "Северо-восток"
        const val SOUTH_WEST = "Юго-запад"
    }
}