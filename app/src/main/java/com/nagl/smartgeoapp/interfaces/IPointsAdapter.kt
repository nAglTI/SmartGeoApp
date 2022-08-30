package com.nagl.smartgeoapp.interfaces

import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.Point

interface IPointsAdapter {
    fun convertCoordinate(point: Point, coordinateFormat: String): Coordinate
}