package com.nagl.smartgeoapp.interfaces

import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.nagl.smartgeoapp.R
import kotlinx.android.synthetic.main.top_panel.*

interface ITopPanel : IPanel, Toolbar.OnMenuItemClickListener {

    fun initTopPanel(title: String) {
        configBackIcon()
        configActionIcon()
        panelContext.topToolbar.title = title
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun configBackIcon() {
        if (panelContext.isTaskRoot) {
            panelContext.topToolbar.navigationIcon = null
        } else {
            panelContext.topToolbar.navigationIcon = panelContext.getDrawable(R.drawable.ic_back)
            panelContext.topToolbar.setNavigationOnClickListener {
                onBackPressedTopPanel()
            }
        }
    }

    fun onBackPressedTopPanel() {
        panelContext.onBackPressed()
        panelContext.finish()
        panelContext.overridePendingTransition(0, 0)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun configActionIcon() {
        panelContext.topToolbar.menu.findItem(R.id.navigation_action).icon =
            panelContext.getDrawable(R.drawable.ic_help_fill)
        panelContext.topToolbar.setOnMenuItemClickListener(this)

    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_action -> {
                actionClick()
                true
            }
            else -> {
                false
            }
        }
    }

    fun actionClick() {}
}