package com.nagl.smartgeoapp.activities.tasks

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
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
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_notch_task.*
import kotlin.math.tan

class NotchTaskActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    private var sourceCoordinate: CoordinateSystem = CoordinateSystem()
    private var pointA: Point = Point()
    private var pointB: Point = Point()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notch_task)

        initTopPanel("Прямая угловая засечка")
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
        calcNotchBtn.setOnClickListener {
            getResult()
        }
        addResPointBtn.setOnClickListener {
            val intent = Intent(this, CreatePointActivity::class.java)
            intent.putExtra("AddPointTask", true)
            val point = coordinateApi.convertToCoordinateInFormat(
                Coordinate(
                    format = Point.NEH,
                    firstCoordinate = nText2.text.toString().toDouble(),
                    secondCoordinate = eText2.text.toString().toDouble(),
                    thirdCoordinate = hText2.text.toString().toDouble()
                ),
                Point.XYZ_WGS,
                sourceCoordinate
            )
            intent.putExtra(Coordinate.EXTRA, point)
            createResPoint.launch(intent)
        }
    }

    private fun getResult() {
        if (sourceCoordinate != CoordinateSystem() &&
            pointA != Point() && pointB != Point() &&
            aAngleEdit2.text.toString().toDoubleOrNull() != null &&
            bAngleEdit2.text.toString().toDoubleOrNull() != null
        ) {
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
            val aAngle = aAngleEdit2.text.toString().toDouble()
            val bAngle = bAngleEdit2.text.toString().toDouble()

            val xn =
                (coordA.firstCoordinate * (1 / tan(bAngle)) + coordB.firstCoordinate *
                        (1 / tan(aAngle)) + coordB.secondCoordinate - coordA.secondCoordinate) /
                        ((1 / tan(aAngle)) + (1 / tan(bAngle)))

            val yn =
                (coordA.secondCoordinate * (1 / tan(bAngle)) + coordB.secondCoordinate *
                        (1 / tan(aAngle)) - coordB.firstCoordinate + coordA.firstCoordinate
                        ) /
                        ((1 / tan(aAngle)) + (1 / tan(bAngle)))

            nText2.text = xn.toString()
            eText2.text = yn.toString()
            hText2.text = "0.0"
            addResPointBtn.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                this,
                "Выберете систему координат, точки и введите исходные данные!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val createResPoint = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

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
            .setTitle("Прямая угловая засечка")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_notch_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}