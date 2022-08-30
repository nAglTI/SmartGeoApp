package com.nagl.smartgeoapp.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Point(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, // id точки в БД
    var name: String = "", // название точки
    @Embedded(prefix = "coordinate_")
    var coordinate: Coordinate = Coordinate(), // координаты точки
    var date: Long = 0, // дата создания точки

) : Selectable(), Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readSerializable() as Coordinate,
        parcel.readLong(),
    )

    /**
     * метод записи parcel
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeSerializable(coordinate)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Point> {
        override fun createFromParcel(parcel: Parcel): Point {
            return Point(parcel)
        }

        override fun newArray(size: Int): Array<Point?> {
            return arrayOfNulls(size)
        }

        const val EXTRA = "point extra"
        const val UPDATED_EXTRA = "point updated"
        const val DELETED_EXTRA = "point deleted"

        const val XYZ_WGS = "XYZ WGS"
        const val XYZ_REF = "XYZ REF"
        const val BLH_WGS = "BLH WGS"
        const val BLH_REF = "BLH REF"
        const val NEH = "NEH MCK"

        // Coordinate format chars
        val FORMAT_CHAR_SETS = mapOf(
            XYZ_WGS to listOf("X", "Y", "Z"),
            XYZ_REF to listOf("X", "Y", "Z"),
            BLH_WGS to listOf("B", "L", "H"),
            BLH_REF to listOf("B", "L", "H"),
            NEH to listOf("N", "E", "H")
        )

        val SIZE_OF_POINT_HEADER = arrayListOf(160f, 120f, 120f, 120f, 160f)

        const val NAME = "Имя"
        const val FORMAT = "Формат координат точки"
        const val DATE = "Дата"
    }
}