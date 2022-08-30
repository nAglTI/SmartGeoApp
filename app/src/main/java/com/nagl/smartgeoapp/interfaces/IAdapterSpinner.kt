package com.nagl.smartgeoapp.interfaces

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.nagl.smartgeoapp.R

interface IAdapterSpinner {

    fun setAdapter(unit: List<String>, spinner: Spinner, context: Context): ArrayAdapter<*> {
        return setAdapter(
            unit,
            spinner,
            context,
            R.layout.spinner_item,
            R.layout.spinner_item
        )
    }

    fun setAdapter(
        unit: List<String>,
        spinner: Spinner,
        context: Context,
        itemLayoutId: Int,
        dropdownItemLayoutId: Int
    ): ArrayAdapter<*> {
        val adapter = ArrayAdapter(context, itemLayoutId, unit)
        adapter.setDropDownViewResource(dropdownItemLayoutId)
        spinner.adapter = adapter
        return adapter
    }

    fun setAdapter(unit: List<String>, spinner: AutoCompleteTextView, context: Context) {
        val adapter = ArrayAdapter(context, R.layout.spinner_item, unit)
        spinner.setAdapter(adapter)
    }

    fun setAdapter(
        unit: List<String>,
        spinner: AutoCompleteTextView,
        context: Context,
        itemLayoutId: Int,
        dropdownItemLayoutId: Int
    ): ArrayAdapter<*> {
        val adapter = ArrayAdapter(context, itemLayoutId, unit)
        spinner.setAdapter(adapter)
        return adapter
    }

}