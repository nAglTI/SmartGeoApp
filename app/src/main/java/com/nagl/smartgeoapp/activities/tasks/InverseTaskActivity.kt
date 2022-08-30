package com.nagl.smartgeoapp.activities.tasks

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.activities.CoordSystemActivity
import com.nagl.smartgeoapp.activities.points.DatabaseActivity
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.database.entity.Ellipsoid
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_inverse_task.*
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

class InverseTaskActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    private var numberApi = NumberApi()
    private val coordinateApi = CoordinateApi(numberApi)

    private var sourceCoordinate: CoordinateSystem = CoordinateSystem()
    private var pointA: Point = Point()
    private var pointB: Point = Point()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inverse_task)

        initTopPanel("ОГЗ")
        init()
    }

    private fun init() {
        addSourceSystemBtn.setOnClickListener {
            val intent = Intent(this, CoordSystemActivity::class.java)
            intent.putExtra("Type", CoordinateSystem.CARTOGRAPH_TYPE)
            sourceSystem.launch(intent)
        }
        firstPointInput.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            firstPoint.launch(intent)
        }
        secondPointInput.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            secondPoint.launch(intent)
        }
        calcBtn.setOnClickListener {
            calculateInverse()
        }
    }

    private fun calculateInverse() {
        if (sourceCoordinate != CoordinateSystem() && pointA != Point() && pointB != Point()) {
            val coordA = coordinateApi.convertToCoordinateInFormat(
                pointA.coordinate,
                Point.NEH,
                sourceCoordinate
            )
            val coordB = coordinateApi.convertToCoordinateInFormat(
                pointB.coordinate,
                Point.NEH,
                sourceCoordinate
            )

            val dn = coordB.firstCoordinate - coordA.firstCoordinate
            val de = coordB.secondCoordinate - coordA.secondCoordinate
            val dh = coordB.thirdCoordinate - coordA.thirdCoordinate

            var azimuth = Math.toDegrees(atan(de / dn))
            if (dn < 0) {
                azimuth += 180
            }
            if (de < 0 && dn >= 0) {
                azimuth += 360
            }

            val horizontalPosition = sqrt(dn.pow(2) + de.pow(2))
            val incline = dh / horizontalPosition
            val tiltAngle = Math.toDegrees(atan(incline))
            val inclinedDistance = sqrt(dh.pow(2) + horizontalPosition.pow(2))

            aziText2.text = numberApi.roundSomeSymbols(azimuth, 3).toString()
            angleText2.text = numberApi.roundSomeSymbols(tiltAngle, 3).toString()
            horPosText2.text = numberApi.roundSomeSymbols(horizontalPosition, 3).toString()
            incDistText2.text = numberApi.roundSomeSymbols(inclinedDistance, 3).toString()
            northText2.text = numberApi.roundSomeSymbols(dn, 3).toString()
            eastText2.text = numberApi.roundSomeSymbols(de, 3).toString()
            excessText2.text = numberApi.roundSomeSymbols(dh, 3).toString()
            inclineText2.text = numberApi.roundSomeSymbols(incline, 3).toString()

            Toast.makeText(this, "Вычисления выполнены успешно!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Выберите систему координат и две точки!", Toast.LENGTH_LONG).show()
        }
    }

    private val sourceSystem = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            sourceCoordinate =
                data.getSerializableExtra(CoordinateSystem.COORDINATE_SYSTEM_EXTRA) as CoordinateSystem
            addSourceSystemBtn.text = sourceCoordinate.name
        }
    }

    private val firstPoint = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            pointA =
                data.getSerializableExtra(Point.EXTRA) as Point
            firstPointInput.text = pointA.name
        }
    }

    private val secondPoint = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            pointB =
                data.getSerializableExtra(Point.EXTRA) as Point
            secondPointInput.text = pointB.name
        }
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Обратная геодезическая задача")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_inverse_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }

}