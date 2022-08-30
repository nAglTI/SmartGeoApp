package com.nagl.smartgeoapp.adapters.elements

import android.content.Context
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.TextView

interface RawTableElement {

    var textViewList: ArrayList<TextView>

    fun setSizes(
        textViewList: ArrayList<TextView>,
        cellSizes: ArrayList<Float>,
        context: Context
    ) {
        for ((i, textView) in textViewList.withIndex()) {
            val widthDp = getDp(cellSizes[i], context)
            val params = LinearLayout.LayoutParams(widthDp, LinearLayout.LayoutParams.WRAP_CONTENT)
            textView.layoutParams = params
        }
    }

    private fun getDp(size: Float, context: Context): Int {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val pixels = metrics.density * size
        return (pixels + 0.5f).toInt()
    }
}