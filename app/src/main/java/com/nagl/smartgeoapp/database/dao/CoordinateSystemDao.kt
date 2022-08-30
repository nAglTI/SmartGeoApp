package com.nagl.smartgeoapp.database.dao

import androidx.room.*
import com.nagl.smartgeoapp.database.entity.CoordinateSystem

@Dao
interface CoordinateSystemDao : BaseDao<CoordinateSystem> {

    @Query("SELECT * FROM CoordinateSystem")
    suspend fun get(): List<CoordinateSystem>

    @Transaction
    @Query("SELECT * FROM CoordinateSystem WHERE id = :id")
    suspend fun getById(id: Long): CoordinateSystem?

    @Query("SELECT COUNT(*) + 1 FROM CoordinateSystem WHERE name LIKE :nameSubstring || ' (%)'")
    suspend fun getCountByNameSubstring(nameSubstring: String): Int
}