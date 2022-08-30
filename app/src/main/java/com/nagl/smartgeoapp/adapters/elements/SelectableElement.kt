package com.nagl.smartgeoapp.adapters.elements

import android.view.View
import android.widget.CheckBox

abstract class SelectableElement {

    lateinit var selectCheckBox: CheckBox
    protected var isSelectableMod: Boolean = false

    fun changeMod(isSelectable: Boolean) {
        if (isSelectable == isSelectableMod) {
            return
        }

        isSelectableMod = isSelectable

        if (isSelectable) {
            selectCheckBox.visibility = View.VISIBLE
        } else {
            selectCheckBox.isChecked = false
            selectCheckBox.visibility = View.GONE
        }
    }


}