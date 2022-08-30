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
import com.nagl.smartgeoapp.activities.points.CreatePointActivity
import com.nagl.smartgeoapp.activities.points.DatabaseActivity
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.database.entity.Ellipsoid
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_convert_task.*
import kotlinx.android.synthetic.main.activity_create_point.*

class ConvertTaskActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    private lateinit var sourcePoint: Point
    private var sourceCoordinate: CoordinateSystem = CoordinateSystem()
    private var resultCoordinate: CoordinateSystem = CoordinateSystem()
    private var resultPoint: Point = Point()
    private var sourceFormat = ""
    private var resultFormat = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_task)

        initTopPanel(getString(R.string.str_convert_task_activity_title_text))
        init()
    }

    private fun init() {
        addSourceCoordinateBtn.setOnClickListener {
            getSourceSystem()
        }
        addResultCoordinateBtn.setOnClickListener {
            getResultSystem()
        }
        getSourcePointBtn.setOnClickListener {
            getPointFromDB()
        }
        clearSourceCoordsBtn.setOnClickListener {
            firstCoordSourcePointInput.setText("")
            secondCoordSourcePointInput.setText("")
            thirdCoordSourcePointInput.setText("")
        }
        insertResultPointBtn.setOnClickListener {
            insertResultPoint()
        }
        getResultBtn.setOnClickListener {
            getResult()
        }
    }

    private fun getSourceSystem() {
        sourceSystem.launch(Intent(this, CoordSystemActivity::class.java))
    }

    private val sourceSystem = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            sourceCoordinate =
                data.getSerializableExtra(CoordinateSystem.COORDINATE_SYSTEM_EXTRA) as CoordinateSystem
            addSourceCoordinateBtn.text = sourceCoordinate.name

            sourceFormat = if (sourceCoordinate.type == CoordinateSystem.GEODES_TYPE)
                (if (sourceCoordinate.ellipsoid == Ellipsoid()) Point.BLH_WGS
                else Point.BLH_REF)
            else Point.NEH

            when (sourceFormat) {
                Point.NEH -> {
                    firstCoordSourcePointText.text = "N"
                    secondCoordSourcePointText.text = "E"
                }
                else -> {
                    firstCoordSourcePointText.text = "B"
                    secondCoordSourcePointText.text = "L"
                }
            }

            sourcePoint = Point(
                coordinate = Coordinate(
                    format = sourceFormat
                )
            )
        }
    }

    private fun getResultSystem() {
        resultSystem.launch(Intent(this, CoordSystemActivity::class.java))
    }

    private val resultSystem = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            resultCoordinate =
                data.getSerializableExtra(CoordinateSystem.COORDINATE_SYSTEM_EXTRA) as CoordinateSystem
            addResultCoordinateBtn.text = resultCoordinate.name

            resultFormat = if (resultCoordinate.type == CoordinateSystem.GEODES_TYPE)
                (if (resultCoordinate.ellipsoid == Ellipsoid()) Point.BLH_WGS
                else Point.BLH_REF)
            else Point.NEH

            when (resultFormat) {
                Point.NEH -> {
                    firstCoordResultPointText.text = "N"
                    secondCoordResultPointText.text = "E"
                }
                else -> {
                    firstCoordResultPointText.text = "B"
                    secondCoordResultPointText.text = "L"
                }
            }

            firstCoordResultPointInput.text = ""
            secondCoordResultPointInput.text = ""
            thirdCoordResultPointInput.text = ""
        }
    }

    private fun getPointFromDB() {
        if (sourceCoordinate != CoordinateSystem())
            getSourcePoint.launch(Intent(this, DatabaseActivity::class.java))
        else
            Toast.makeText(this, "Выберите систему координат", Toast.LENGTH_LONG)
                .show() // TODO: перевод
    }

    private val getSourcePoint = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            sourcePoint = data.getSerializableExtra(Point.EXTRA) as Point

            sourceFormat = if (sourceCoordinate.type == CoordinateSystem.GEODES_TYPE)
                (if (sourceCoordinate.ellipsoid == Ellipsoid()) Point.BLH_WGS
                else Point.BLH_REF)
            else Point.NEH

            sourcePoint.coordinate = coordinateApi.convertToCoordinateInFormat(
                sourcePoint.coordinate,
                sourceFormat,
                sourceCoordinate
            )

            val coordinate = coordinateApi.formatCoordinate(
                sourcePoint.coordinate,
                sourceFormat
            )

            firstCoordSourcePointInput.setText(coordinate.firstCoordinate.toString())
            firstCoordSourcePointInput.isEnabled = false
            secondCoordSourcePointInput.setText(coordinate.secondCoordinate.toString())
            secondCoordSourcePointInput.isEnabled = false
            thirdCoordSourcePointInput.setText(coordinate.thirdCoordinate.toString())
            thirdCoordSourcePointInput.isEnabled = false
        }
    }

    private val insertPoint = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //Toast.makeText(panelContext, "Точка добавлена успешно!", Toast.LENGTH_LONG).show()
        }
    }

    private fun insertResultPoint() {
        if (
            firstCoordResultPointInput.text.toString().toDoubleOrNull() != null &&
            secondCoordResultPointInput.text.toString().toDoubleOrNull() != null &&
            thirdCoordResultPointInput.text.toString().toDoubleOrNull() != null
        ) {
            val intent = Intent(this, CreatePointActivity::class.java)
            intent.putExtra("AddPointTask", true)
            val point = coordinateApi.convertToCoordinateInFormat(
                Coordinate(
                    firstCoordinate = firstCoordResultPointInput.text.toString().toDouble(),
                    secondCoordinate = secondCoordResultPointInput.text.toString().toDouble(),
                    thirdCoordinate = thirdCoordResultPointInput.text.toString().toDouble(),
                    format = resultFormat
                ),
                Point.XYZ_WGS,
                resultCoordinate
            )
            intent.putExtra(Coordinate.EXTRA, point)
            insertPoint.launch(intent)
        } else {
            Toast.makeText(this, "Вы не произвели вычисления!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getResult() {
        if (
            firstCoordSourcePointInput.text.toString().toDoubleOrNull() != null &&
            secondCoordSourcePointInput.text.toString().toDoubleOrNull() != null &&
            thirdCoordSourcePointInput.text.toString().toDoubleOrNull() != null &&
            sourceCoordinate != CoordinateSystem() && resultCoordinate != CoordinateSystem()
        ) {
            if (sourcePoint.name.isEmpty()) {
                sourcePoint.coordinate.firstCoordinate =
                    firstCoordSourcePointInput.text.toString().toDouble()
                sourcePoint.coordinate.secondCoordinate =
                    secondCoordSourcePointInput.text.toString().toDouble()
                sourcePoint.coordinate.thirdCoordinate =
                    thirdCoordSourcePointInput.text.toString().toDouble()
            }

            val wgsCoords = coordinateApi.convertToCoordinateInFormat(
                sourcePoint.coordinate,
                Point.XYZ_WGS,
                sourceCoordinate
            )

            val resultCoordinates = coordinateApi.convertToCoordinateInFormat(
                wgsCoords,
                resultFormat,
                resultCoordinate
            )

            resultPoint.coordinate = resultCoordinates

            val formatCoordinate = coordinateApi.formatCoordinate(
                resultCoordinates,
                resultFormat
            )

            firstCoordResultPointInput.text = formatCoordinate.firstCoordinate.toString()
            secondCoordResultPointInput.text = formatCoordinate.secondCoordinate.toString()
            thirdCoordResultPointInput.text = formatCoordinate.thirdCoordinate.toString()
        } else {
            Toast.makeText(
                this,
                "Выберите системы координат и заполните поля исходной точки!", // TODO: перевод
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Конвертация координат")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_point_conversion_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}