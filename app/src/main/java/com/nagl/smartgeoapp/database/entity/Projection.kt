package com.nagl.smartgeoapp.database.entity

import java.io.Serializable

/**
 * класс данных проекция
 */
data class Projection( // todo для разных проекций придется менять базу, или запихивать дофига параметров и ненужные скрывать
    var type: String = "", //тип проекции
    var axialMeridian: Double = 117.0, //осевой меридиан
    var startOfLatitude: Double = 0.0, //начало отсчета широт
    var scale: Double = 1.0, //масштаб
    var eastShift: Double = 500000.0, //сдвиг на восток
    var northShift: Double = 0.0, //сдвиг на север
    var zone: Int = 1, //зона
    var hemisphere: Int = 0, //полусфера
    var azimuth: String = "000:00:00.0000000", //азимут
    var firstParallel: String = "000:00:00.0000000N", //первая парралель
    var secondParallel: String = "000:00:00.0000000N", //вторая парралель
    var firstMeridian: String = "000:00:00.0000000E", //первый меридиан
    var secondMeridian: String = "000:00:00.0000000E", //второй меридиан
    var dirTurningCorner: String = "000:00:00.0000000", //направление поворотного угла
    var usingDirTurning: Int = 0, //использование поворотного угла
    var initialAzimuth: Int = 0, //начальный азимут
    var initialPosition: Int = 0, //начальное положение
    var averageLatitude: String = "000:00:00.0000000N", //средняя широта
    var averageProjectHeight: Double = 0.0 //средняя высота проекта
) : Serializable {

    companion object {
        const val PROJECTION = "projection"
    }
}