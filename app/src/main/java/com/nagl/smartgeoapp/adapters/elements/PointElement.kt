package com.nagl.smartgeoapp.adapters.elements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.activities.points.DatabaseActivity
import com.nagl.smartgeoapp.activities.points.SelectedPointActivity
import com.nagl.smartgeoapp.activities.tasks.ConvertTaskActivity
import com.nagl.smartgeoapp.activities.tasks.DirectTaskActivity
import com.nagl.smartgeoapp.activities.tasks.InverseTaskActivity
import com.nagl.smartgeoapp.activities.tasks.NotchTaskActivity
import com.nagl.smartgeoapp.api.FormatObject
import com.nagl.smartgeoapp.api.TimeApi
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.IPointsAdapter
import java.io.Serializable
import java.util.*

class PointElement(
    private val context: Context,
    private val cellSizes: ArrayList<Float>,
    private val iPointsAdapter: IPointsAdapter,
) : SelectableElement(), View.OnClickListener, RawTableElement {

    private lateinit var nameText: TextView
    private lateinit var firstCoordinateText: TextView
    private lateinit var secondCoordinateText: TextView
    private lateinit var thirdCoordinateText: TextView
    private lateinit var dateText: TextView
    private lateinit var mainLayout: LinearLayout
    override lateinit var textViewList: ArrayList<TextView>

    private lateinit var point: Point

    fun createElement(point: Point, view: View, isSelectableMode: Boolean) {
        init(view, isSelectableMode)
        setSizes(textViewList, cellSizes, context)
        this.point = point

        nameText.text = point.name
        selectCheckBox.isChecked = point.isSelected
        dateText.text = TimeApi.getDateInFormatYMDHMS(Date(point.date))

        val coordinate = iPointsAdapter.convertCoordinate(
            point,
            FormatObject.coordinateFormat
        )
        firstCoordinateText.text = coordinate.firstCoordinate.toString()
        secondCoordinateText.text = coordinate.secondCoordinate.toString()
        thirdCoordinateText.text = coordinate.thirdCoordinate.toString()
    }

    private fun init(view: View, isSelectableMode: Boolean) {
        nameText = view.findViewById(R.id.namePointElementText)
        firstCoordinateText = view.findViewById(R.id.firstCoordinatePointElementText)
        secondCoordinateText = view.findViewById(R.id.secondCoordinatePointElementText)
        thirdCoordinateText = view.findViewById(R.id.thirdCoordinatePointElementText)
        dateText = view.findViewById(R.id.datePointElementText)
        mainLayout = view.findViewById(R.id.mainPointElementLayout)
        mainLayout.setOnClickListener(this)

        textViewList = arrayListOf(
            nameText,
            firstCoordinateText,
            secondCoordinateText,
            thirdCoordinateText,
            dateText
        )
        selectCheckBox = view.findViewById(R.id.selectPointElementCheckBox)
        if (isSelectableMode) {
            selectCheckBox.visibility = View.VISIBLE
        } else {
            selectCheckBox.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        if (isSelectableMod || point.id == 0L) {
            return
        }

        if (isStartedForResult() == InverseTaskActivity::class.java.name ||
            isStartedForResult() == ConvertTaskActivity::class.java.name ||
            isStartedForResult() == DirectTaskActivity::class.java.name ||
            isStartedForResult() == NotchTaskActivity::class.java.name) {
            returnPoint()
        } else {
            goToCurrentPoint(point)
        }
    }

    private fun goToCurrentPoint(point: Point) {
        val intent = Intent(context, SelectedPointActivity::class.java)
        intent.putExtra(Point.EXTRA, point as Serializable)
        (context as DatabaseActivity).selectedPointStartForResult(intent)
    }

    private fun returnPoint() {
        val intent = Intent().apply {
            putExtra(Point.EXTRA, point as Serializable)
        }
        (context as Activity).setResult(Activity.RESULT_OK, intent)
        context.finish()
    }

    private fun isStartedForResult() = (context as Activity).callingActivity?.className
}
