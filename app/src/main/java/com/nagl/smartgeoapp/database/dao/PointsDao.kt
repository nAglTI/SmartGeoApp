package com.nagl.smartgeoapp.database.dao

import androidx.room.*
import com.nagl.smartgeoapp.database.entity.Point

@Dao
interface PointsDao : BaseDao<Point> {

    @Query("SELECT * FROM Point")
    suspend fun get(): List<Point>
}