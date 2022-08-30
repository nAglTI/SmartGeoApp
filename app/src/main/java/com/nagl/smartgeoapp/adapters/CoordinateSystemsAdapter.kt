package com.nagl.smartgeoapp.adapters

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.activities.tasks.ConvertTaskActivity
import com.nagl.smartgeoapp.activities.tasks.DirectTaskActivity
import com.nagl.smartgeoapp.activities.tasks.InverseTaskActivity
import com.nagl.smartgeoapp.activities.tasks.NotchTaskActivity
import com.nagl.smartgeoapp.database.entity.CoordinateSystem

class CoordinateSystemsAdapter(
    private val systemsList: ArrayList<CoordinateSystem>,
    private val context: Context
) : RecyclerView.Adapter<CoordinateSystemsAdapter.CoordinateSystemsViewHolder?>() {

    inner class CoordinateSystemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mainLayout: LinearLayout = itemView.findViewById(R.id.mainSystemElementLayout)
        var name: TextView = itemView.findViewById(R.id.nameSystemElementText)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoordinateSystemsViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.coordinate_system_element
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, parent, false)
        return this.CoordinateSystemsViewHolder(view)
        //return CoordinateSystemsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.coordinate_system_element, null));
    }

    override fun onBindViewHolder(
        holderCoordinateSystems: CoordinateSystemsViewHolder,
        position: Int
    ) {
        holderCoordinateSystems.name.text = systemsList[position].name
        holderCoordinateSystems.mainLayout.setOnClickListener {
            if ((context as Activity).callingActivity?.className == ConvertTaskActivity::class.java.name ||
                (context as Activity).callingActivity?.className == InverseTaskActivity::class.java.name ||
                (context as Activity).callingActivity?.className == DirectTaskActivity::class.java.name ||
                (context as Activity).callingActivity?.className == NotchTaskActivity::class.java.name
            ) {
                val intent = Intent().apply {
                    putExtra(CoordinateSystem.COORDINATE_SYSTEM_EXTRA, systemsList[position])
                }
                context.setResult(RESULT_OK, intent)
                context.finish()
            }
            //notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return systemsList.size
    }
}