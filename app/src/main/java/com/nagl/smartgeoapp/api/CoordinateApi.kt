package com.nagl.smartgeoapp.api

import com.nagl.smartgeoapp.database.entity.*
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.BLH_WGS
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.BLH_REF
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.NEH
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.XYZ_WGS
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.XYZ_REF
import kotlin.math.*

class CoordinateApi(
    private val numberRoundingApi: NumberApi,
//    private val convertFeetApi: ConvertFeetApi,
//    private val currentCoordinateSystemApi: CacheCurrentCoordinateSystemApi,
) {

    private fun convertXyzToCoordinateInFormat(
        coordinate: Coordinate,
        coordinateFormat: String,
        coordSystem: CoordinateSystem,
    ): Coordinate {
        when (coordinateFormat) {
            XYZ_WGS -> {
                return coordinate
            }
            BLH_WGS -> {
                return convertXyzToBlh(coordinate)
            }
            XYZ_REF -> {
                return convertXyzToXyzRef(
                    coordinate,
                    coordSystem.igd
                )
            }
            BLH_REF -> {
                return convertXyzToBlhRef(
                    coordinate,
                    coordSystem.igd,
                    coordSystem.ellipsoid
                )
            }
            NEH -> {
                return convertXyzToNeh(
                    coordinate,
                    coordSystem.ellipsoid,
                    coordSystem.projection,
                    coordSystem.igd
                )
            }
        }
        return coordinate
    }

    fun convertCoordinateInFormatToXyz(
        coordinate: Coordinate,
        coordSystem: CoordinateSystem,
    ): Coordinate {
        when (coordinate.format) {
            XYZ_WGS -> {
                return coordinate
            }
            BLH_WGS -> {
                return convertBlhToXyz(coordinate)
            }
            XYZ_REF -> {
                return convertXyzRefToXyz(
                    coordinate,
                    coordSystem.igd
                )
            }
            BLH_REF -> {
                return convertBlhRefToXyz(
                    coordinate,
                    coordSystem.igd,
                    coordSystem.ellipsoid
                )
            }
            NEH -> {
                return convertNehToXyz(
                    coordinate,
                    coordSystem.ellipsoid,
                    coordSystem.projection,
                    coordSystem.igd
                )
            }
        }
        return coordinate
    }

    fun convertToCoordinateInFormat(
        coordinate: Coordinate,
        toCoordinateFormat: String,
        coordSystem: CoordinateSystem = CoordinateSystem(),
    ): Coordinate {
        if (coordinate.format == toCoordinateFormat) {
            return coordinate
        }

        when (coordinate.format) {
            XYZ_WGS -> {
                return convertXyzToCoordinateInFormat(coordinate, toCoordinateFormat, coordSystem)
            }

            BLH_WGS -> {
                when (toCoordinateFormat) {
                    XYZ_WGS -> {
                        return convertBlhToXyz(coordinate)
                    }

                    XYZ_REF -> {
                        return convertBlhToXyzRef(coordinate, coordSystem.igd)
                    }

                    BLH_REF -> {
                        return convertBlhToBlhRef(
                            coordinate,
                            coordSystem.igd,
                            coordSystem.ellipsoid
                        )
                    }

                    NEH -> {
                        return convertBlhToNeh(
                            coordinate,
                            coordSystem.igd,
                            coordSystem.projection,
                            coordSystem.ellipsoid
                        )
                    }
                }
            }

            XYZ_REF -> {
                when (toCoordinateFormat) {
                    XYZ_WGS -> {
                        return convertXyzRefToXyz(
                            coordinate,
                            coordSystem.igd,
                        )
                    }

                    BLH_WGS -> {
                        return convertXyzRefToBlh(
                            coordinate,
                            coordSystem.igd
                        )
                    }

                    BLH_REF -> {
                        return convertXyzRefToBlhRef(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.igd
                        )
                    }

                    NEH -> {
                        return convertXyzRefToNeh(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection,
                            coordSystem.igd
                        )
                    }
                }
            }
            BLH_REF -> {
                when (toCoordinateFormat) {
                    XYZ_WGS -> {
                        return convertBlhRefToXyz(
                            coordinate,
                            coordSystem.igd,
                            coordSystem.ellipsoid
                        )
                    }

                    BLH_WGS -> {
                        return convertBlhRefToBLH(
                            coordinate,
                            coordSystem.igd,
                            coordSystem.ellipsoid
                        )
                    }

                    XYZ_REF -> {
                        return convertBlhRefToXyzRef(
                            coordinate,
                            coordSystem.ellipsoid
                        )
                    }

                    NEH -> {
                        return convertBlhRefToNeh(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection
                        )
                    }
                }
            }
            NEH -> {
                when (toCoordinateFormat) {
                    XYZ_WGS -> {
                        return convertNehToXyz(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection,
                            coordSystem.igd
                        )
                    }

                    BLH_WGS -> {
                        return convertNehToBlh(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection,
                            coordSystem.igd
                        )
                    }

                    XYZ_REF -> {
                        return convertNehToXyzRef(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection,
                            coordSystem.igd
                        )
                    }

                    BLH_REF -> {
                        return convertNehToBlhRef(
                            coordinate,
                            coordSystem.ellipsoid,
                            coordSystem.projection
                        )
                    }
                }
            }
        }
        return coordinate
    }

    fun convertBlhToXyz(coordinate: Coordinate, ellipsoid: Ellipsoid = Ellipsoid()): Coordinate {
        val b = Math.toRadians(coordinate.firstCoordinate)
        val l = Math.toRadians(coordinate.secondCoordinate)
        val h = coordinate.thirdCoordinate

        val a = ellipsoid.bHalfShaft
        val f = 1 / ellipsoid.compression
        val e = sqrt(2 * f - f.pow(2))
        val v = a / sqrt(1 - e.pow(2) * sin(b).pow(2))

        return Coordinate(
            (v + h) * cos(b) * cos(l),
            (v + h) * cos(b) * sin(l),
            (v * (1 - e.pow(2)) + h) * sin(b),
            XYZ_WGS
        )
    }

    private fun convertBlhToXyzRef(
        coordinate: Coordinate,
        igd: Parameters
    ): Coordinate {
        val xyzCoordinate = convertBlhToXyz(coordinate)
        return convertXyzToXyzRef(xyzCoordinate, igd)
    }

    private fun convertBlhToBlhRef(
        coordinate: Coordinate,
        igd: Parameters,
        ellipsoid: Ellipsoid
    ): Coordinate {
        val xyzCoordinate = convertBlhToXyz(coordinate)
        return convertXyzToBlhRef(xyzCoordinate, igd, ellipsoid)
    }

    private fun convertBlhToNeh(
        coordinate: Coordinate,
        igd: Parameters,
        projection: Projection,
        ellipsoid: Ellipsoid
    ): Coordinate {
        val xyzCoordinate = convertBlhToXyz(coordinate)
        return convertXyzToNeh(xyzCoordinate, ellipsoid, projection, igd)
    }

    fun convertXyzToBlh(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
    ): Coordinate {
        if (isNullCoordinate(coordinate)) {
            return Coordinate(0.0, 0.0, 0.0)
        }

        val x = coordinate.firstCoordinate
        val y = coordinate.secondCoordinate
        val z = coordinate.thirdCoordinate

        val a = ellipsoid.bHalfShaft
        val precision = 2 / a
        val e2 = getEccentricity(ellipsoid).pow(2)
        val p = sqrt(x.pow(2) + y.pow(2))
        var nu = 0.0
        var b = atan2(z, p * (1 - e2))
        var previousB = 2 * PI
        while (abs(b - previousB) > precision) {
            nu = a / sqrt(1 - e2 * sin(b).pow(2))
            previousB = b
            b = atan2(z + e2 * nu * sin(b), p)
        }
        var l = atan2(y, x)
        var h = p / cos(b) - nu

        if (b.isNaN()) {
            b = 0.0
        }
        if (l.isNaN()) {
            l = 0.0
        }
        if (h.isNaN()) {
            h = 0.0
        }

        return Coordinate(Math.toDegrees(b), Math.toDegrees(l), h, BLH_WGS)
    }

    fun convertXyzToXyzRef(
        coordinate: Coordinate,
        igd: Parameters
    ): Coordinate {

        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        var x1 = coordinate.firstCoordinate
        var y1 = coordinate.secondCoordinate
        var z1 = coordinate.thirdCoordinate

        var x2 = coordinate.firstCoordinate
        var y2 = coordinate.secondCoordinate
        var z2 = coordinate.thirdCoordinate

        val rxRadians = convertSecondsToRadians(igd.rx)
        val ryRadians = convertSecondsToRadians(igd.ry)
        val rzRadians = convertSecondsToRadians(igd.rz)
        val scaleCoefficient = 1 - igd.scale * 10.0.pow(-6)

        x2 =
            scaleCoefficient * (x1 * cos(rxRadians) - y1 * sin(rzRadians) + z1 * sin(
                ryRadians
            ))
        y2 =
            scaleCoefficient * (x1 * sin(rzRadians) + y1 * cos(ryRadians) - z1 * sin(
                rxRadians
            ))
        z2 =
            scaleCoefficient * (-x1 * sin(ryRadians) + y1 * sin(rxRadians) + z1 * cos(
                rzRadians
            ))

        x2 -= igd.dx
        y2 -= igd.dy
        z2 -= igd.dz


        return Coordinate(x2, y2, z2, XYZ_REF)
    }

    fun convertXyzToBlhRef(
        coordinate: Coordinate,
        igd: Parameters,
        ellipsoid: Ellipsoid
    ): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        val xyzRefCoordinate = convertXyzToXyzRef(coordinate, igd)

        val blhRefCoordinate = convertXyzToBlh(xyzRefCoordinate, ellipsoid)
        blhRefCoordinate.format = BLH_REF

        return blhRefCoordinate
    }

    fun convertXyzRefToXyz(
        coordinate: Coordinate,
        igd: Parameters,
    ): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        if (igd.parmsCountValue == 0) {
            return coordinate
        }

        var x1 = coordinate.firstCoordinate
        var y1 = coordinate.secondCoordinate
        var z1 = coordinate.thirdCoordinate

        var x2 = coordinate.firstCoordinate
        var y2 = coordinate.secondCoordinate
        var z2 = coordinate.thirdCoordinate

        val rxRadians = convertSecondsToRadians(igd.rx)
        val ryRadians = convertSecondsToRadians(igd.ry)
        val rzRadians = convertSecondsToRadians(igd.rz)
        val scaleCoefficient = 1 + igd.scale * 10.0.pow(-6)

        x2 = scaleCoefficient * (x1 * cos(rxRadians) + y1 * sin(rzRadians) - z1 * sin(
            ryRadians
        ))
        y2 = scaleCoefficient * (-x1 * sin(rzRadians) + y1 * cos(ryRadians) + z1 * sin(
            rxRadians
        ))
        z2 = scaleCoefficient * (x1 * sin(ryRadians) - y1 * sin(rxRadians) + z1 * cos(
            rzRadians
        ))


        x2 += igd.dx
        y2 += igd.dy
        z2 += igd.dz

        return Coordinate(x2, y2, z2, XYZ_WGS)
    }

    fun convertXyzRefToBlh(
        coordinate: Coordinate,
        igd: Parameters,
    ): Coordinate {
        val xyzCoordinate = convertXyzRefToXyz(coordinate, igd)
        return convertXyzToBlh(xyzCoordinate)
    }

    fun convertXyzRefToBlhRef(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid,
        igd: Parameters
    ): Coordinate {
        val xyzCoordinate = convertXyzRefToXyz(coordinate, igd)
        return convertXyzToBlhRef(xyzCoordinate, igd, ellipsoid)
    }

    fun convertXyzRefToNeh(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid,
        projection: Projection,
        igd: Parameters
    ): Coordinate {
        val xyzCoordinate = convertXyzRefToXyz(coordinate, igd)
        return convertXyzToNeh(xyzCoordinate, ellipsoid, projection, igd)
    }

    fun convertBlhRefToXyz(
        coordinate: Coordinate,
        igd: Parameters,
        ellipsoid: Ellipsoid
    ): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        val xyzRefCoordinate = convertBlhToXyz(coordinate, ellipsoid)

        return convertXyzRefToXyz(xyzRefCoordinate, igd)
    }

    private fun convertBlhRefToBLH(
        coordinate: Coordinate,
        igd: Parameters,
        ellipsoid: Ellipsoid = Ellipsoid()
    ): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        val xyzRefCoordinate = convertBlhToXyz(coordinate, ellipsoid)
        val xyzCoordinate = convertXyzRefToXyz(xyzRefCoordinate, igd)

        return convertXyzToBlh(xyzCoordinate)
    }

    fun convertBlhRefToXyzRef(coordinate: Coordinate, ellipsoid: Ellipsoid): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(0.0, 0.0, 0.0)
        return convertBlhToXyz(coordinate, ellipsoid)
    }

    fun convertXyzToNeh(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
        igd: Parameters = Parameters(),
    ): Coordinate {
        if (isNullCoordinate(coordinate)) return Coordinate(
            0.0,
            0.0,
            0.0
        )

        val blhCoordinate = convertXyzToBlhRef(coordinate, igd, ellipsoid)

        return convertBlhRefToNeh(blhCoordinate, ellipsoid, projection)
    }

    fun convertBlhRefToNeh(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
    ): Coordinate {

        val scale = projection.scale
        val a = ellipsoid.bHalfShaft
        val e = getEccentricity(ellipsoid)
        val e2 = e.pow(2)
        val phi0 = Math.toRadians(projection.startOfLatitude)
        val phi = Math.toRadians(coordinate.firstCoordinate)
        val lambda0 = Math.toRadians(projection.axialMeridian)
        val lambda = Math.toRadians(coordinate.secondCoordinate)

        val m = calculateM(e, a, phi)
        val m0 = calculateM(e, a, phi0)

        val p = a * (1.0 - e2) / (1.0 - e2 * sin(phi).pow(2)).pow(3.0 / 2.0)
        val v = a / sqrt(1.0 - e2 * sin(phi).pow(2))
        val psi = v / p
        val t = tan(phi)
        val omega = lambda - lambda0

        val north1 = (omega.pow(2) / 2.0) * v * sin(phi) * cos(phi)
        val north2 = (omega.pow(4) / 24.0) * v * sin(phi) * cos(phi).pow(3) *
                (4.0 * psi.pow(2) + psi - t.pow(2))
        val north3 = (omega.pow(6) / 720.0) * v * sin(phi) * cos(phi).pow(5) *
                (8.0 * psi.pow(4) * (11.0 - 24.0 * t.pow(2)) - 28.0 * psi.pow(3) * (1.0 - 6.0 * t.pow(
                    2
                ))
                        + psi.pow(2) * (1.0 - 32.0 * t.pow(2)) - psi * 2.0 * t.pow(2) + t.pow(4))
        val north4 = (omega.pow(8) / 40320.0) * v * sin(phi) * cos(phi).pow(7) *
                (1385.0 - 3111.0 * t.pow(2) + 543.0 * t.pow(4) - t.pow(6))
        val toNorth =
            projection.northShift + scale * (m - m0 + north1 + north2 + north3 + north4)

        val east1 = omega.pow(2) / 6 * cos(phi).pow(2) * (psi - t.pow(2))
        val east2 = omega.pow(4) / 120 * cos(phi).pow(4) * (4 * psi.pow(3) * (1 - 6 * t.pow(2))
                + psi.pow(2) * (1 + 8 * t.pow(2)) - psi * 2 * t.pow(2) + t.pow(4))
        val east3 =
            omega.pow(6) / 5040 * cos(phi).pow(6) * (61 - 479 * t.pow(2) + 179 * t.pow(4) - t.pow(
                6
            ))
        val toEast =
            projection.eastShift + scale * v * omega * cos(phi) * (1 + east1 + east2 + east3)

        return Coordinate(toNorth, toEast, coordinate.thirdCoordinate, NEH)
    }

    private fun calculateM(e: Double, a: Double, phi: Double): Double {
        val e2 = e.pow(2)
        val e4 = e.pow(4)
        val e6 = e.pow(6)

        val a0 = 1 - (e2 / 4.0) - (3 * e4 / 64.0) - (5.0 * e6 / 256.0)
        val a2 = (3.0 / 8.0) * (e2 + e4 / 4.0 + 15.0 * e6 / 128.0)
        val a4 = (15.0 / 256.0) * (e4 + 3.0 * e6 / 4.0)
        val a6 = 35.0 * e6 / 3072.0

        return a * (a0 * phi - a2 * sin(2 * phi) + a4 * sin(4 * phi) - a6 * sin(6 * phi))
    }

    fun convertNehToXyz(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
        igd: Parameters
    ): Coordinate {
        val blhCoordinate = convertNehToBlhRef(coordinate, ellipsoid, projection)

        return convertBlhRefToXyz(blhCoordinate, igd, ellipsoid)
    }

    fun convertNehToXyzRef(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
        igd: Parameters
    ): Coordinate {
        val blhCoordinate = convertNehToBlhRef(coordinate, ellipsoid, projection)
        val xyzCoordinate = convertBlhRefToXyz(blhCoordinate, igd, ellipsoid)
        return convertXyzToXyzRef(xyzCoordinate, igd)
    }

    fun convertNehToBlh(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
        igd: Parameters
    ): Coordinate {
        val blhCoordinate = convertNehToBlhRef(coordinate, ellipsoid, projection)
        val xyzCoordinate = convertBlhRefToXyz(blhCoordinate, igd, ellipsoid)
        return convertXyzToBlh(xyzCoordinate)
    }

    fun convertNehToBlhRef(
        coordinate: Coordinate,
        ellipsoid: Ellipsoid = Ellipsoid(),
        projection: Projection = Projection(),
    ): Coordinate {
        val e = getEccentricity(ellipsoid)
        val a = ellipsoid.bHalfShaft
        val phi0 = Math.toRadians(projection.startOfLatitude)
        val scale = projection.scale
        val m0 = calculateM(e, a, phi0)
        val m1 = m0 + (coordinate.firstCoordinate - projection.northShift) / scale

        val f = 1 / ellipsoid.compression
        val n = f / (2 - f)
        val g = a * (1 - n) * (1 - n.pow(2)) * (1 + 9 * n.pow(2) / 4 + 225 * n.pow(4) / 64)
        val sigma = m1 / g
        val phi1 = sigma + (3 * n / 2 - 27 * n.pow(3) / 32) * sin(2 * sigma) +
                (21 * n.pow(2) / 16 - 55 * n.pow(4) / 32) * sin(4 * sigma) +
                (151 * n.pow(3) / 96) * sin(6 * sigma) +
                (1097 * n.pow(4) / 512) * sin(8 * sigma)

        val e2 = e.pow(2)
        val p = a * (1 - e2) / (1 - e2 * sin(phi1).pow(2)).pow(1.5)
        val v = a / sqrt(1 - e2 * sin(phi1).pow(2))
        val psi = v / p
        val t = tan(phi1)
        val east1 = coordinate.secondCoordinate - projection.eastShift
        val x = east1 / (scale * v)

        val termCoef = t * east1 / (scale * p)
        val term1 = termCoef * x / 2
        val term2 = termCoef * x.pow(3) / 24 *
                (-4 * psi.pow(2) + 9 * psi * (1 - t.pow(2)) + 12 * t.pow(2))
        val term3 = termCoef * x.pow(5) / 720 * (8 * psi.pow(4) * (11 - 24 * t.pow(2)) -
                12 * psi.pow(3) * (21 - 71 * t.pow(2)) +
                15 * psi.pow(2) * (15 - 98 * t.pow(2) + 15 * t.pow(4)) +
                180 * psi * (5 * t.pow(2) - 3 * t.pow(4)) +
                360 * t.pow(4))
        val term4 = termCoef * x.pow(7) / 40320 * (1385 + 3633 * t.pow(2) +
                4095 * t.pow(4) + 1575 * t.pow(6))
        val phi = Math.toDegrees(phi1 - term1 + term2 - term3 + term4)

        val lambda0 = Math.toRadians(projection.axialMeridian)
        val secPhi = 1 / cos(phi1)
        val lambda1 = x * secPhi
        val lambda2 = x.pow(3) * secPhi / 6 * (psi + 2 * t.pow(2))
        val lambda3 = x.pow(5) * secPhi / 120 * (-4 * psi.pow(3) * (1 - 6 * t.pow(2)) +
                psi.pow(2) * (9 - 68 * t.pow(2)) + 72 * psi * t.pow(2) + 24 * t.pow(4))
        val lambda4 = x.pow(7) * secPhi / 5040 * (61 + 662 * t.pow(2) +
                1320 * t.pow(4) + 720 * t.pow(6))
        val lambda = Math.toDegrees(lambda0 + lambda1 - lambda2 + lambda3 - lambda4)

        return Coordinate(phi, lambda, coordinate.thirdCoordinate, BLH_REF)
    }

    fun convertToPseudoMercator(
        coordinate: Coordinate,
        coordinateSystem: CoordinateSystem,
    ): Coordinate {
        val blhCoordinate = convertToCoordinateInFormat(coordinate, BLH_WGS, coordinateSystem)

        val latitudeRadians = Math.toRadians(blhCoordinate.firstCoordinate)
        val longitudeRadians = Math.toRadians(blhCoordinate.secondCoordinate)
        val semimajorAxis = Ellipsoid().bHalfShaft
        val eccentricity = 0.0

        val n = semimajorAxis * longitudeRadians
        val e = semimajorAxis * ln(
            tan(PI / 4 + latitudeRadians / 2) *
                    ((1 - eccentricity * sin(latitudeRadians)) /
                            (1 + eccentricity * sin(latitudeRadians))).pow(eccentricity / 2)
        )

        return Coordinate(n, e, blhCoordinate.thirdCoordinate)
    }

    fun convertPseudoMercatorToXYZ(coordinate: Coordinate): Coordinate {
        val blhCoordinate = convertPseudoMercatorToBlh(coordinate)

        return convertBlhToXyz(blhCoordinate)
    }

    fun convertPseudoMercatorToBlh(coordinate: Coordinate): Coordinate {
        val n = coordinate.firstCoordinate
        val e = coordinate.secondCoordinate
        val h = coordinate.thirdCoordinate
        val semimajorAxis = Ellipsoid().bHalfShaft

        val longitude = Math.toDegrees(n / semimajorAxis)
        val latitude = Math.toDegrees(2 * (atan(E.pow(e / semimajorAxis)) - PI / 4))

        return Coordinate(
            latitude,
            longitude,
            h,
            BLH_WGS
        )
    }

    private fun getEccentricity(ellipsoid: Ellipsoid = Ellipsoid()): Double {
        val f = 1 / ellipsoid.compression
        return sqrt(2 * f - f.pow(2))
    }

    fun roundSomeSymbols(coordinate: Coordinate, symbolsCount: Int): Coordinate {
        return Coordinate(
            numberRoundingApi.roundSomeSymbols(coordinate.firstCoordinate, symbolsCount),
            numberRoundingApi.roundSomeSymbols(coordinate.secondCoordinate, symbolsCount),
            numberRoundingApi.roundSomeSymbols(coordinate.thirdCoordinate, symbolsCount)
        )
    }

    fun formatCoordinateToDegreesMinutesSeconds(coordinate: Coordinate): List<String> {
        return listOf(
            formatCoordinateToDegreesMinutesSeconds(coordinate.firstCoordinate),
            formatCoordinateToDegreesMinutesSeconds(coordinate.secondCoordinate),
            coordinate.thirdCoordinate.toString(),
        )
    }

    fun formatCoordinateToDegreesMinutesSeconds(coordinate: Double): String {
        val degrees = coordinate.toInt()
        val allSeconds = (coordinate % 1) * 3600
        val minutes = (allSeconds / 60).toInt()
        val seconds = numberRoundingApi.formatNumber(allSeconds % 60, 5)

        return "$degrees° $minutes' $seconds''"
    }

    fun convertDMSToDecimalDegrees(dms: ArrayList<Double>): Double {
        return if (dms[0] < 0) dms[0] - (dms[1] / 60) - dms[2] / 3600 else dms[0] + (dms[1] / 60) + dms[2] / 3600
    }

    private fun convertSecondsToRadians(seconds: Double) = Math.toRadians(seconds / 3600)

//    private fun formatCoordinate(
//        coordinate: Coordinate,
//        finalCoordinateFormat: String,
//    ): TextCoordinate {
//        if (finalCoordinateFormat == BLH || finalCoordinateFormat == BLH_REF) {
//            return if (CacheCurrentProject.cacheProject.geographicCoordinatesFormat == Project.DEGREES_FORMAT) {
//                val formattedCoordinate = formatCoordinateToDegreesMinutesSeconds(coordinate)
//                TextCoordinate(
//                    formattedCoordinate[0],
//                    formattedCoordinate[1],
//                    formattedCoordinate[2],
//                    formattedCoordinate[3].toDouble(),
//                    formattedCoordinate[4].toDouble()
//                )
//            } else {
//                TextCoordinate(
//                    numberRoundingApi.formatNumber(coordinate.firstCoordinate, 9),
//                    numberRoundingApi.formatNumber(coordinate.secondCoordinate, 9),
//                    numberRoundingApi.formatNumber(coordinate.thirdCoordinate, 3),
//                    coordinate.weight,
//                    coordinate.pae
//                )
//            }
//        } else {
//            return TextCoordinate(
//                numberRoundingApi.formatNumber(coordinate.firstCoordinate, 3),
//                numberRoundingApi.formatNumber(coordinate.secondCoordinate, 3),
//                numberRoundingApi.formatNumber(coordinate.thirdCoordinate, 3),
//                coordinate.weight,
//                coordinate.pae
//            )
//        }
//
//    }

    private fun isNullCoordinate(coordinate: Coordinate): Boolean {
        return coordinate.firstCoordinate == 0.0 &&
                coordinate.secondCoordinate == 0.0 &&
                coordinate.thirdCoordinate == 0.0
    }

    fun formatCoordinate(
        coordinate: Coordinate,
        finalCoordinateFormat: String,
    ): Coordinate {
        if (finalCoordinateFormat == BLH_WGS || finalCoordinateFormat == BLH_REF) {
            //return if (CacheCurrentProject.cacheProject.geographicCoordinatesFormat == Project.DEGREES_FORMAT) {
//                val formattedCoordinate = formatCoordinateToDegreesMinutesSeconds(coordinate)
//                Coordinate(
//                    formattedCoordinate[0],
//                    formattedCoordinate[1],
//                    formattedCoordinate[2],
//                    formattedCoordinate[3].toDouble(),
//                    formattedCoordinate[4].toDouble()
//                )
//            } else {
            return Coordinate(
                numberRoundingApi.formatNumber(coordinate.firstCoordinate, 9).toDouble(),
                numberRoundingApi.formatNumber(coordinate.secondCoordinate, 9).toDouble(),
                numberRoundingApi.formatNumber(coordinate.thirdCoordinate, 3).toDouble(),
            )
            //}
        } else {
            return Coordinate(
                numberRoundingApi.formatNumber(coordinate.firstCoordinate, 3).toDouble(),
                numberRoundingApi.formatNumber(coordinate.secondCoordinate, 3).toDouble(),
                numberRoundingApi.formatNumber(coordinate.thirdCoordinate, 3).toDouble(),
            )
        }

    }

}

