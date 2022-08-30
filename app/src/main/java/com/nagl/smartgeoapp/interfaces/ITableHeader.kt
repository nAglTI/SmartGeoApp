package com.nagl.smartgeoapp.interfaces

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.nagl.smartgeoapp.R

interface ITableHeader {

    fun createTableHeader(
        namesOfHeader: ArrayList<String>,
        sizesOfHeader: ArrayList<Float>,
        context: Context,
        listener: View.OnClickListener
    ): ArrayList<TextView> {
        val headerCells = ArrayList<TextView>()

        for ((i, name) in namesOfHeader.withIndex()) {
            val headerCell = createHeaderCell(name, sizesOfHeader[i], context)
            headerCell.setOnClickListener(listener)
            headerCells.add(headerCell)
        }
        return headerCells
    }

    fun createTableHeader(
        namesOfHeader: ArrayList<String>,
        sizesOfHeader: ArrayList<Float>,
        context: Context
    ): ArrayList<TextView> {
        val headerCells = ArrayList<TextView>()

        for ((i, name) in namesOfHeader.withIndex()) {
            val headerCell = createHeaderCell(name, sizesOfHeader[i], context)
            headerCells.add(headerCell)
        }
        return headerCells
    }

    private fun createHeaderCell(name: String, size: Float, context: Context): TextView {
        val header = TextView(context)
        header.width = getDp(size, context)
        header.height = getDp(40f, context)
        //header.textAlignment = View.TEXT_ALIGNMENT_CENTER
        header.gravity = Gravity.CENTER
        header.setTypeface(header.typeface, Typeface.BOLD)
        header.setBackgroundResource(R.drawable.cell)
        header.setTextColor(context.getColor(R.color.white))
        header.text = name
        header.tag = 0

        return header
    }

    private fun getDp(size: Float, context: Context): Int {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val pixels = metrics.density * size
        return (pixels + 0.5f).toInt()
    }

    fun setDefaultTextHeaders(
        namesOfHeader: ArrayList<String>,
        headers: ArrayList<TextView>,
        currentHeader: String
    ) {
        for ((i, header) in headers.withIndex()) {
            header.text = namesOfHeader[i]
            if (!currentHeader.contains(header.text.toString())) {
                header.tag = 0
            }
        }
    }
}