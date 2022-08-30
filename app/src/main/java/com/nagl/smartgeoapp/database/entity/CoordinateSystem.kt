package com.nagl.smartgeoapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * класс данных системы координат
 */
@Entity
data class CoordinateSystem(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, //id системы координат
    var isLibrary: Boolean = false, //флаг находится ли она в библиотеке
    @ColumnInfo(defaultValue = CARTOGRAPH_TYPE)
    var type: String = CARTOGRAPH_TYPE,
    var country: String = "", //страна
    var name: String = "ск " + SimpleDateFormat("dd.M.yyyy_HH:mm").format(Date()), //название системы координат
    @Embedded(prefix = "ellipsoid")
    var ellipsoid: Ellipsoid = Ellipsoid(), //эллипсоид
    @Embedded(prefix = "projection")
    var projection: Projection = Projection(), //проекция
    @Embedded(prefix = "igd")
    var igd: Parameters = Parameters(), //ИГД
) : Serializable {

    companion object {
        const val COORDINATE_SYSTEM_EXTRA = "coordSystem"
        const val COORDINATE_SYSTEM = "Система координат"
        const val GEODES_TYPE = "geodes"
        const val CARTOGRAPH_TYPE = "cart"
    }
}