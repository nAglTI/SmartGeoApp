package com.nagl.smartgeoapp.activities.tasks

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
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
import kotlinx.android.synthetic.main.activity_direct_task.*
import kotlinx.android.synthetic.main.activity_direct_task.addSourceSystemBtn
import java.io.Serializable
import kotlin.math.cos
import kotlin.math.sin

class DirectTaskActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    private var sourceCoordinate: CoordinateSystem = CoordinateSystem()
    private var pointA: Point = Point()
    private var resPoint = Point()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_task)

        initTopPanel("ПГЗ")
        init()
    }

    private fun init() {
        addSourceSystemBtn.setOnClickListener {
            val intent = Intent(this, CoordSystemActivity::class.java)
            intent.putExtra("Type", CoordinateSystem.CARTOGRAPH_TYPE)
            sourceSystem.launch(intent)
        }
        aPointInput.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            firstPoint.launch(intent)
        }
        calcDirectBtn.setOnClickListener {
            getResult()
        }
        addResPointBtn.setOnClickListener {
            val intent = Intent(this, CreatePointActivity::class.java)
            intent.putExtra("AddPointTask", true)
            val point = coordinateApi.convertToCoordinateInFormat(
                resPoint.coordinate,
                Point.XYZ_WGS,
                sourceCoordinate
            )
            intent.putExtra(Coordinate.EXTRA, point)
            createResPoint.launch(intent)
        }
    }

    private fun getResult() {
        if (
            horPosEdit2.text.toString().toDoubleOrNull() != null &&
            dirAngleEdit2.text.toString().toDoubleOrNull() != null &&
            sourceCoordinate != CoordinateSystem() &&
            pointA != Point()
        ) {
            val coordA = coordinateApi.convertToCoordinateInFormat(
                pointA.coordinate,
                Point.NEH,
                sourceCoordinate
            )
            val horPos = horPosEdit2.text.toString().toDouble()
            val angle = dirAngleEdit2.text.toString().toDouble()

            val dx = horPos * cos(angle)
            val dy = horPos * sin(angle)

            resPoint.coordinate.format = Point.NEH
            resPoint.coordinate.firstCoordinate = coordA.firstCoordinate + dx
            resPoint.coordinate.secondCoordinate = coordA.secondCoordinate + dy
            resPoint.coordinate.thirdCoordinate = coordA.thirdCoordinate

            nText2.text = resPoint.coordinate.firstCoordinate.toString()
            eText2.text = resPoint.coordinate.secondCoordinate.toString()
            hText2.text = resPoint.coordinate.thirdCoordinate.toString()

            addResPointBtn.visibility = View.VISIBLE
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
            aPointInput.text = pointA.name
        }
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Прямая геодезическая задача")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_direct_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}