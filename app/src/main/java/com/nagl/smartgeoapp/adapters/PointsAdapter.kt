package com.nagl.smartgeoapp.adapters

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.adapters.elements.PointElement
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.ChangeableCallback
import com.nagl.smartgeoapp.interfaces.IPointsAdapter
import com.nagl.smartgeoapp.interfaces.ISelectableTableHeader
import javax.inject.Inject

class PointsAdapter(
    private val pointsList: List<Point>,
    private val cellSizes: ArrayList<Float>,
    private val iPointsAdapter: IPointsAdapter,
    intent: Intent,
    application: Application,
    changeableActivityCallback: ChangeableCallback
) : ChangeableAdapter<Point>(changeableActivityCallback) {

    constructor(
        pointsList: List<Point>,
        cellSizes: ArrayList<Float>,
        iPointsAdapter: IPointsAdapter,
        intent: Intent,
        application: Application,
        changeableActivityCallback: ChangeableCallback,
        selectableTableHeader: ISelectableTableHeader
    ) : this(
        pointsList,
        cellSizes,
        iPointsAdapter,
        intent,
        application,
        changeableActivityCallback
    ) {
        this.selectableTableHeader = selectableTableHeader
    }

    init {
        selectableList = pointsList
//        DaggerAppComponent
//            .builder()
//            .appModule(AppModule(application, intent))
//            .apiModule(ApiModule(application))
//            .build().inject(this)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PointsViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.point_element
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        view.setOnLongClickListener {
            changeMod(true)
            true
        }

        return this.PointsViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ISelectableViewHolder, i: Int) {
        val pointElement = (holder as PointsViewHolder).bind(pointsList[i], isSelectableMod)
        setSelectCheckBoxListener(pointElement, pointsList[i])
        selectableElements.add(pointElement)
    }

    fun getSelectedAsParcelable(): ArrayList<Parcelable> {
        return getSelected().map { it as Parcelable } as ArrayList<Parcelable>
    }

    override fun getItemCount(): Int {
        return pointsList.size
    }

    inner class PointsViewHolder(private val view: View, private val context: Context) :
        SelectableAdapter.ISelectableViewHolder(view) {

        fun bind(point: Point, isSelectableMode: Boolean): PointElement {
            val pointElement =
                PointElement(context, cellSizes, iPointsAdapter)

            pointElement.createElement(point, view, isSelectableMode)
            return pointElement
        }
    }
}