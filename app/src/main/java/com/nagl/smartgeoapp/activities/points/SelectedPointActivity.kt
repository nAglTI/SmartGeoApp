package com.nagl.smartgeoapp.activities.points

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.FormatObject
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.api.TimeApi
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.IAdapterSpinner
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_selected_point.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class SelectedPointActivity : AppCompatActivity(), ITopPanel, IAdapterSpinner {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    private lateinit var extraPoint: Point
    private lateinit var currentPointFormat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_point)

        extraPoint = intent.getSerializableExtra(Point.EXTRA) as Point
        currentPointFormat = extraPoint.coordinate.format
        initTopPanel(getString(R.string.str_point_activity_title, extraPoint.name))
        init()
        setViewFields()
    }

    private fun setViewFields() {
        when (currentPointFormat) {
            Point.XYZ_WGS -> {
                formatTypeSpinner.setSelection(0)
            }
            else -> {
                formatTypeSpinner.setSelection(1)
            }
        }

        pointNameSelectedPointInput.setText(extraPoint.name)
        firstCoordSelectedPointInput.setText(extraPoint.coordinate.firstCoordinate.toString())
        secondCoordSelectedPointInput.setText(extraPoint.coordinate.secondCoordinate.toString())
        thirdCoordSelectedPointInput.setText(extraPoint.coordinate.thirdCoordinate.toString())
        pointDateSelectedPointText.text = getString(
            R.string.str_point_activity_date_text,
            TimeApi.getDateInFormatYMDHMS(Date(extraPoint.date))
        )
    }

    private fun init() {
        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.format_types)),
            formatTypeSpinner,
            this
        )

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
                        if (
                            firstCoordSelectedPointInput.text.toString().toDoubleOrNull() != null &&
                            secondCoordSelectedPointInput.text.toString()
                                .toDoubleOrNull() != null &&
                            thirdCoordSelectedPointInput.text.toString().toDoubleOrNull() != null &&
                            currentPointFormat != Point.XYZ_WGS
                        ) {
                            val coordinate = coordinateApi.formatCoordinate(
                                coordinateApi.convertToCoordinateInFormat(
                                    Coordinate(
                                        firstCoordSelectedPointInput.text.toString().toDouble(),
                                        secondCoordSelectedPointInput.text.toString().toDouble(),
                                        thirdCoordSelectedPointInput.text.toString().toDouble(),
                                        Point.BLH_WGS
                                    ),
                                    Point.XYZ_WGS
                                ),
                                Point.XYZ_WGS
                            )
                            firstCoordSelectedPointInput.setText(coordinate.firstCoordinate.toString())
                            secondCoordSelectedPointInput.setText(coordinate.secondCoordinate.toString())
                            thirdCoordSelectedPointInput.setText(coordinate.thirdCoordinate.toString())
                            currentPointFormat = Point.XYZ_WGS
                        }
                    } else {
                        FormatObject.coordinateFormat = Point.BLH_WGS
                        firstCoordSelectedPointInput.hint = "B"
                        secondCoordSelectedPointInput.hint = "L"
                        thirdCoordSelectedPointInput.hint = "H"
                        if (
                            firstCoordSelectedPointInput.text.toString().toDoubleOrNull() != null &&
                            secondCoordSelectedPointInput.text.toString()
                                .toDoubleOrNull() != null &&
                            thirdCoordSelectedPointInput.text.toString().toDoubleOrNull() != null &&
                            currentPointFormat != Point.BLH_WGS
                        ) {
                            val coordinate = coordinateApi.formatCoordinate(
                                coordinateApi.convertToCoordinateInFormat(
                                    Coordinate(
                                        firstCoordSelectedPointInput.text.toString().toDouble(),
                                        secondCoordSelectedPointInput.text.toString().toDouble(),
                                        thirdCoordSelectedPointInput.text.toString().toDouble(),
                                        Point.XYZ_WGS
                                    ),
                                    Point.BLH_WGS
                                ),
                                Point.BLH_WGS
                            )
                            firstCoordSelectedPointInput.setText(coordinate.firstCoordinate.toString())
                            secondCoordSelectedPointInput.setText(coordinate.secondCoordinate.toString())
                            thirdCoordSelectedPointInput.setText(coordinate.thirdCoordinate.toString())
                            currentPointFormat = Point.BLH_WGS
                        }
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
                val point = extraPoint
                point.name = pointNameSelectedPointInput.text.toString().trim()
                point.coordinate = Coordinate(
                    firstCoordSelectedPointInput.text.toString().toDouble(),
                    secondCoordSelectedPointInput.text.toString().toDouble(),
                    thirdCoordSelectedPointInput.text.toString().toDouble(),
                    format = FormatObject.coordinateFormat
                )
                point.date = Date().time
                updatePoint(point)
                showUpdateSuccess(point)
            }
        }

        deletePointDatabaseBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.str_point_activity_delete_alert_title)
                .setMessage(
                    R.string.str_point_activity_delete_alert_message
                )
                .setPositiveButton(R.string.yes) { _, _ ->
                    deletePoint(intent.getSerializableExtra(Point.EXTRA) as Point)
                    showDeleteSuccess(extraPoint)
                }.setNegativeButton(R.string.no) { _, _ ->
                }.show()
        }
    }

    private fun deletePoint(point: Point) = runBlocking {
        launch {
            LocalDB.getDatabase(panelContext).getPointsDao().delete(point)
        }
    }

    private fun updatePoint(point: Point) = runBlocking {
        launch {
            LocalDB.getDatabase(panelContext).getPointsDao().update(point)
        }
    }

    private fun showUpdateSuccess(point: Point) {
        Toast.makeText(
            this,
            getString(R.string.str_create_point_activity_update_alert_text),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent().apply {
            putExtra(Point.UPDATED_EXTRA, point as Parcelable)
        }
        setResult(RESULT_OK, intent)
        onBackPressed()
    }

    private fun showDeleteSuccess(point: Point) {
        Toast.makeText(
            this,
            getString(R.string.str_create_point_activity_delete_alert_text),
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent().apply {
            putExtra(Point.DELETED_EXTRA, point as Parcelable)
        }
        setResult(RESULT_OK, intent)
        onBackPressed()
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Редактирование точки")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_selected_point_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}