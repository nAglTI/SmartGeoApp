package com.nagl.smartgeoapp.activities.tasks

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_tasks.*

class TasksActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        initTopPanel(getString(R.string.str_tasks_activity_title_text))
        init()
    }

    private fun init() {
        convertPointBtn.setOnClickListener {
            val intent = Intent(this, ConvertTaskActivity::class.java)
            startActivity(intent)
        }
        inverseTaskBtn.setOnClickListener {
            val intent = Intent(this, InverseTaskActivity::class.java)
            startActivity(intent)
        }
        directTaskBtn.setOnClickListener {
            val intent = Intent(this, DirectTaskActivity::class.java)
            startActivity(intent)
        }
        notchTaskBtn.setOnClickListener {
            val intent = Intent(this, NotchTaskActivity::class.java)
            startActivity(intent)
        }
        areaTaskBtn.setOnClickListener {
            val intent = Intent(this, AreaTaskActivity::class.java)
            startActivity(intent)
        }
        lengthTaskBtn.setOnClickListener {
            val intent = Intent(this, LengthTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Задачи")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_tasks_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}