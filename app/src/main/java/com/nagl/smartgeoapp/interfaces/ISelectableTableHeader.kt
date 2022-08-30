package com.nagl.smartgeoapp.interfaces

import android.widget.CheckBox
import com.nagl.smartgeoapp.adapters.SelectableAdapter
import com.nagl.smartgeoapp.database.entity.Selectable

interface ISelectableTableHeader : ITableHeader {

    var selectAllCheckBox: CheckBox

    fun setAllSelectedCheckBoxListener(selectableAdapter: SelectableAdapter<out Selectable>) {
        selectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectableAdapter.selectAll()
            } else {
                selectableAdapter.clearAllSelections()
            }
        }
    }

    fun checkAllSelectedCheckBox() {
        if (!selectAllCheckBox.isChecked) {
            selectAllCheckBox.isChecked = true
        }
    }

    fun uncheckAllSelectedCheckBox() {
        if (selectAllCheckBox.isChecked) {
            selectAllCheckBox.isChecked = false
        }
    }
}