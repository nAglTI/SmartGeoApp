package com.nagl.smartgeoapp.adapters

import com.nagl.smartgeoapp.database.entity.Selectable
import com.nagl.smartgeoapp.interfaces.ChangeableCallback


abstract class ChangeableAdapter<T : Selectable>(
    private val changeableActivityCallback: ChangeableCallback
) : SelectableAdapter<T>() {

    protected var isSelectableMod = false

    fun changeMod(isSelectable: Boolean) {
        if (isSelectableMod == isSelectable) {
            return
        }
        isSelectableMod = isSelectable

        for (element in selectableElements) {
            element.changeMod(isSelectable)
        }

        if (isSelectable) {
            changeableActivityCallback.showSelectableMode()
        } else {
            changeableActivityCallback.showNormalMode()
        }

        for (obj in selectableList) {
            obj.isSelected = false
        }
        selectableTableHeader?.uncheckAllSelectedCheckBox()
    }
}