package com.nagl.smartgeoapp.database.entity

import java.io.Serializable

/**
 * класс данных координат
 */
data class Coordinate(
    var firstCoordinate: Double = 0.0, // первая координата
    var secondCoordinate: Double = 0.0, // вторая координата
    var thirdCoordinate: Double = 0.0, // третья координата
    var format: String = Point.XYZ_WGS, // формат координаты
) : Serializable {
    companion object {
        const val EXTRA = "coordinate extra"
    }
}
