package com.nagl.smartgeoapp.activities.points

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.FormatObject
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.IAdapterSpinner
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_create_point.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import java.util.*

class CreatePointActivity : AppCompatActivity(), ITopPanel, IAdapterSpinner {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_point)

        initTopPanel(getString(R.string.str_create_point_activity_title))
        init()

    }

    private fun init() {
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.format_types)),
            formatTypeSpinner,
            this
        )

        when (FormatObject.coordinateFormat) {
            Point.XYZ_WGS -> {
                formatTypeSpinner.setSelection(0)
            }
            else -> {
                formatTypeSpinner.setSelection(1)
            }
        }

        if (intent.getBooleanExtra("AddPointTask", false)) {
            val coordinate = intent.getSerializableExtra(Coordinate.EXTRA) as Coordinate
            firstCoordSelectedPointInput.setText(coordinate.firstCoordinate.toString())
            secondCoordSelectedPointInput.setText(coordinate.secondCoordinate.toString())
            thirdCoordSelectedPointInput.setText(coordinate.thirdCoordinate.toString())

            formatTypeSpinner.setSelection(0)
        }

        formatTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View?,
                    selectedItemPosition: Int,
                    selectedId: Long
                ) {
                    if (selectedItemPosition == 0) {
                        FormatObject.coordinateFormat = Point.XYZ_WGS
                        firstCoordSelectedPointInput.hint = "X"
                        secondCoordSelectedPointInput.hint = "Y"
                        thirdCoordSelectedPointInput.hint = "Z"
                    } else {
                        FormatObject.coordinateFormat = Point.BLH_WGS
                        firstCoordSelectedPointInput.hint = "B"
                        secondCoordSelectedPointInput.hint = "L"
                        thirdCoordSelectedPointInput.hint = "H"
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        savePointDatabaseBtn.setOnClickListener {
            if (pointNameSelectedPointInput.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.str_create_point_activity_name_alert_text),
                    Toast.LENGTH_LONG
                ).show()
            } else if (
                firstCoordSelectedPointInput.text.toString().toDoubleOrNull() == null ||
                secondCoordSelectedPointInput.text.toString().toDoubleOrNull() == null ||
                thirdCoordSelectedPointInput.text.toString().toDoubleOrNull() == null
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.str_create_point_activity_coords_alert_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val point = Point(
                    name = pointNameSelectedPointInput.text.toString().trim(),
                    coordinate = Coordinate(
                        firstCoordSelectedPointInput.text.toString().toDouble(),
                        secondCoordSelectedPointInput.text.toString().toDouble(),
                        thirdCoordSelectedPointInput.text.toString().toDouble(),
                        format = FormatObject.coordinateFormat
                    ),
                    date = Date().time
                )
                savePoint(point)
                showSaveSuccess(point)
            }
        }
    }

    private fun savePoint(point: Point) = runBlocking {
        launch {
            LocalDB.getDatabase(panelContext).getPointsDao().insert(point)
        }
    }

    private fun showSaveSuccess(point: Point) {
        Toast.makeText(
            this,
            getString(R.string.str_create_point_activity_create_alert_text),
            Toast.LENGTH_LONG
        ).show()
        if (intent.getBooleanExtra("AddPointTask", false)) {
            setResult(RESULT_OK)
            onBackPressed()
        } else {
            val intent = Intent().apply {
                putExtra(Point.EXTRA, point as Serializable)
            }
            setResult(RESULT_OK, intent)
            onBackPressed()
        }

    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Создание точки")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_create_point_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}