package com.nagl.smartgeoapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.nagl.smartgeoapp.adapters.elements.SelectableElement
import com.nagl.smartgeoapp.database.entity.Selectable
import com.nagl.smartgeoapp.interfaces.ISelectableTableHeader

abstract class SelectableAdapter<T : Selectable>() :
    RecyclerView.Adapter<SelectableAdapter.ISelectableViewHolder>() {

    protected var selectableList: List<T> = arrayListOf()
    protected val selectableElements = ArrayList<SelectableElement>()
    var selectableTableHeader: ISelectableTableHeader? = null

    constructor(selectableTableHeader: ISelectableTableHeader) : this() {
        this.selectableTableHeader = selectableTableHeader
    }

    open fun setSelectCheckBoxListener(selectableElement: SelectableElement, selectable: T) {
        selectableElement.selectCheckBox.setOnClickListener {
            selectable.isSelected = !selectable.isSelected

            if (selectableList.any { !it.isSelected }) {
                selectableTableHeader?.uncheckAllSelectedCheckBox()
            } else {
                selectableTableHeader?.checkAllSelectedCheckBox()
            }
        }
    }

    open fun getSelected(): List<T> {
        return selectableList.filter { it.isSelected }
    }

    open fun selectAll() {
        for (selectable in selectableList) {
            selectable.isSelected = true
        }
        for (selectableElement in selectableElements) {
            selectableElement.selectCheckBox.isChecked = true
        }
    }

    open fun clearAllSelections() {
        if (selectableList.any { !it.isSelected }) {
            return
        }

        for (selectable in selectableList) {
            selectable.isSelected = false
        }
        for (selectableElement in selectableElements) {
            selectableElement.selectCheckBox.isChecked = false
        }
    }

    abstract class ISelectableViewHolder(view: View) : RecyclerView.ViewHolder(view)
}