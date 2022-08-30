package com.nagl.smartgeoapp.database.entity

import java.io.Serializable

/**
 * файл данных ИГД
 */
data class Parameters(
    var parmsCount: String = "Без параметров", //количество параметров
    var parmsCountValue: Int = 0, //количество параметров числом
    var recountDirection: Int = 0, //направление пересчета
    var dx: Double = 0.0, //дельта x
    var dy: Double = 0.0, //дельта y
    var dz: Double = 0.0, //дельта z
    var rx: Double = 0.0, //угол поворота по x
    var ry: Double = 0.0, //угол поворота по y
    var rz: Double = 0.0, //угол поворота по z
    var scale: Double = 0.0, //масштаб
    var file: Int = 0 //файлы
) : Serializable {

    companion object {
        const val IGD = "igd"
    }
}