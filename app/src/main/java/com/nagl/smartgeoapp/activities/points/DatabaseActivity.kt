package com.nagl.smartgeoapp.activities.points

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.adapters.PointsAdapter
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.FormatObject
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.Coordinate
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.database.entity.Point.CREATOR.SIZE_OF_POINT_HEADER
import com.nagl.smartgeoapp.interfaces.*
import kotlinx.android.synthetic.main.activity_database.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DatabaseActivity : AppCompatActivity(), ITopPanel, ISelectableTableHeader, IAdapterSpinner,
    ChangeableCallback, View.OnClickListener, IPointsAdapter {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())

    override lateinit var selectAllCheckBox: CheckBox

    private lateinit var pointsAdapter: PointsAdapter
    private var pointsList: ArrayList<Point> = arrayListOf()

    private lateinit var NAME: String
    private var firstCoordinateChar = "X"
    private var secondCoordinateChar = "Y"
    private var thirdCoordinateChar = "Z"
    private lateinit var DATE: String
    private var headerSize: ArrayList<Float> = arrayListOf()
    private var headerTextViews: ArrayList<TextView> = arrayListOf()
    private var namesOfHeader: ArrayList<String> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)
        initTopPanel(getString(R.string.str_database_activity_title))

        NAME = getString(R.string.str_database_activity_name_cell_text)
        DATE = getString(R.string.str_database_activity_date_cell_text)

        headerSize = SIZE_OF_POINT_HEADER
        namesOfHeader = arrayListOf(
            NAME,
            firstCoordinateChar,
            secondCoordinateChar,
            thirdCoordinateChar,
            DATE
        )
        init()
        getPoints()
    }

    override fun onResume() {
        super.onResume()
        when (FormatObject.coordinateFormat) {
            Point.XYZ_WGS -> {
                formatSpinner.setSelection(0)
            }
            else -> {
                formatSpinner.setSelection(1)
            }
        }
    }

    private fun init() {
        selectAllCheckBox = headerDatabaseCheckBox

        setAdapter(
            arrayListOf(*resources.getStringArray(R.array.format_types)),
            formatSpinner,
            this
        )

        when (FormatObject.coordinateFormat) {
            Point.XYZ_WGS -> {
                formatSpinner.setSelection(0)
            }
            else -> {
                formatSpinner.setSelection(1)
            }
        }

        formatSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View?,
                    selectedItemPosition: Int,
                    selectedId: Long
                ) {
                    if (selectedItemPosition == 0) {
                        FormatObject.coordinateFormat = Point.XYZ_WGS
                        setFormatHeader(Point.FORMAT_CHAR_SETS.getValue(Point.XYZ_WGS))
                        pointsAdapter.notifyDataSetChanged()
                    } else {
                        FormatObject.coordinateFormat = Point.BLH_WGS
                        setFormatHeader(Point.FORMAT_CHAR_SETS.getValue(Point.BLH_WGS))
                        pointsAdapter.notifyDataSetChanged()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        pointsDatabaseRecyclerView.layoutManager = LinearLayoutManager(this)

        headerTextViews = createTableHeader(namesOfHeader, headerSize, this, this)
        for (header in headerTextViews) {
            headerDatabaseLayout.addView(header)
        }

        pointsAdapter = PointsAdapter(
            pointsList,
            SIZE_OF_POINT_HEADER,
            this,
            intent,
            application,
            this,
            this
        )
        pointsDatabaseRecyclerView.adapter = pointsAdapter
        setAllSelectedCheckBoxListener(pointsAdapter)

        addPointDatabaseBtn.setOnClickListener {
            val intent = Intent(this, CreatePointActivity::class.java)
            createPointStartForResult.launch(intent)
        }

        deletePointDatabaseBtn.setOnClickListener {
            acceptDelete()
        }

        cancelPointDatabaseBtn.setOnClickListener {
            pointsAdapter.changeMod(false)
            showNormalMode()
        }
    }

    private fun acceptDelete() {
        val points = pointsAdapter.getSelected()

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.str_point_activity_delete_alert_title)
            .setMessage(
                getString(R.string.str_database_activity_delete_points_message, points.size)
            )
            .setPositiveButton(R.string.yes) { _, _ ->
                deletePointsList(points)
                pointsList.removeAll(points)
                pointsAdapter.changeMod(false)
                showNormalMode()
                showPointsListDeleted()
                pointsAdapter.notifyDataSetChanged()
            }.setNegativeButton(R.string.no) { _, _ -> }
            .show()
    }

    private fun deletePointsList(points: List<Point>) = GlobalScope.launch {
        points.forEach {
            LocalDB.getDatabase(panelContext).getPointsDao().delete(it)
        }
    }

    private fun showPointsListDeleted() {
        Toast.makeText(
            this,
            getString(R.string.str_database_activity_delete_alert_text),
            Toast.LENGTH_LONG
        ).show()
    }

    private val createPointStartForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!
            pointsList.add(data.getSerializableExtra(Point.EXTRA) as Point)
            pointsAdapter.notifyDataSetChanged()
        }
    }

    fun selectedPointStartForResult(intent: Intent) {
        selectedPointStartForResult.launch(intent)
    }

    private val selectedPointStartForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent = result.data!!

            if (data.getParcelableExtra<Point>(Point.UPDATED_EXTRA) != null) {
                val extraPoint = data.getParcelableExtra<Point>(Point.UPDATED_EXTRA)!!
                pointsList.forEachIndexed { index, it ->
                    if (it.id == extraPoint.id) {
                        pointsList[index] = extraPoint
                        return@forEachIndexed
                    }
                }
            } else if (data.getParcelableExtra<Point>(Point.DELETED_EXTRA) != null) {
                val extraPoint = data.getParcelableExtra<Point>(Point.DELETED_EXTRA)!!
                pointsList.forEach {
                    if (it.id == extraPoint.id) {
                        pointsList.remove(it)
                        return@forEach
                    }
                }
            }
            pointsAdapter.notifyDataSetChanged()
        }
    }


    private fun getPoints() = runBlocking {
        launch {
            pointsList.addAll(LocalDB.getDatabase(panelContext).getPointsDao().get())
        }
        pointsAdapter.notifyDataSetChanged()
    }

    override fun showSelectableMode() {
        headerDatabaseCheckBox.visibility = View.VISIBLE
        addPointDatabaseBtn.visibility = View.GONE
        deletePointDatabaseBtn.visibility = View.VISIBLE
        cancelPointDatabaseBtn.visibility = View.VISIBLE
    }

    override fun showNormalMode() {
        headerDatabaseCheckBox.visibility = View.GONE
        addPointDatabaseBtn.visibility = View.VISIBLE
        deletePointDatabaseBtn.visibility = View.GONE
        cancelPointDatabaseBtn.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.addPointDatabaseBtn -> {

            }
        }
        setDefaultTextHeaders(namesOfHeader, headerTextViews, (view as TextView).text.toString())
    }

    private fun setFormatHeader(formatChars: List<String>) {
        headerTextViews[1].text = formatChars[0]
        headerTextViews[2].text = formatChars[1]
        headerTextViews[3].text = formatChars[2]

        firstCoordinateChar = formatChars[0]
        secondCoordinateChar = formatChars[1]
        thirdCoordinateChar = formatChars[2]

        namesOfHeader[1] = formatChars[0]
        namesOfHeader[2] = formatChars[1]
        namesOfHeader[3] = formatChars[2]
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("База точек")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_database_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }

    override fun convertCoordinate(point: Point, coordinateFormat: String): Coordinate =
        coordinateApi.formatCoordinate(coordinateApi.convertToCoordinateInFormat(point.coordinate, coordinateFormat), coordinateFormat)
}