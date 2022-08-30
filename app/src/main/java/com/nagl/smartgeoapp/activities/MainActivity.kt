package com.nagl.smartgeoapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.activities.points.DatabaseActivity
import com.nagl.smartgeoapp.activities.tasks.TasksActivity
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), ITopPanel {

//    launch {
//        val coordinateSystems =
//            context.resources.openRawResource(R.raw.cs).bufferedReader().use {
//                Gson().fromJson(it.readText(), Array<CoordSystem>::class.java).asList()
//            }.filter {
//                it.country == country
//            }
//        withContext(Dispatchers.Main) {
//            getCoordinateSystemsCallback.returnList(coordinateSystems)
//        }
//    }

    override var panelContext: Activity = this

    private var coordinateSystems: List<CoordinateSystem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPanel()
        setOnClickListeners()
    }

    private fun createPanel() {
        initTopPanel(getString(R.string.str_main_activity_title_text))
    }

    private fun setOnClickListeners() {
        coordSystemBtn.setOnClickListener {
            val intent = Intent(this, CoordSystemActivity::class.java)
            startActivity(intent)
        }
        pointsDatabaseBtn.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
            startActivity(intent)
        }
        mapBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        taskBtn.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }
//        settingsBtn.setOnClickListener {
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
//        }
//        aboutAppBtn.setOnClickListener {
//            val intent = Intent(this, InfoActivity::class.java)
//            startActivity(intent)
//        }
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Главное меню")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_main_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}