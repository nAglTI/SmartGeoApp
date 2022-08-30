package com.nagl.smartgeoapp.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.adapters.CoordinateSystemsAdapter
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.database.entity.Ellipsoid
import com.nagl.smartgeoapp.database.entity.Parameters
import com.nagl.smartgeoapp.interfaces.IAdapterSpinner
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_coord_system.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoordSystemActivity : AppCompatActivity(), ITopPanel, IAdapterSpinner {

    override var panelContext: Activity = this

    private var cartSystemsList: ArrayList<CoordinateSystem> = arrayListOf()
    private var geodesSystemsList: ArrayList<CoordinateSystem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coord_system)

        createPanel()
        init()
        setAdapters()
        setSystemsArrays()

        if (intent.getStringExtra("Type") == CoordinateSystem.CARTOGRAPH_TYPE) {
            geodesSystemsRecyclerView.visibility = View.GONE
            cartSystemsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun init() {
        geodesSystemsRecyclerView.layoutManager = LinearLayoutManager(this)
        cartSystemsRecyclerView.layoutManager = LinearLayoutManager(this)

        if (intent.getStringExtra("Type") == CoordinateSystem.CARTOGRAPH_TYPE) {
            setAdapter(
                arrayListOf(resources.getStringArray(R.array.system_types)[1]),
                typeSpinner,
                this
            )
        } else {
            setAdapter(
                arrayListOf(*resources.getStringArray(R.array.system_types)),
                typeSpinner,
                this
            )
        }

        typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    itemSelected: View?,
                    selectedItemPosition: Int,
                    selectedId: Long
                ) {
                    if (selectedItemPosition == 0) {
                        geodesSystemsRecyclerView.visibility = View.VISIBLE
                        cartSystemsRecyclerView.visibility = View.GONE
                    } else {
                        geodesSystemsRecyclerView.visibility = View.GONE
                        cartSystemsRecyclerView.visibility = View.VISIBLE
                    }
                    if (typeSpinner.selectedItem == resources.getStringArray(R.array.system_types)[1]) {
                        geodesSystemsRecyclerView.visibility = View.GONE
                        cartSystemsRecyclerView.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        searchInput.doOnTextChanged { text, _, _, _ ->
            if (typeSpinner.selectedItemPosition == 0) {
                getGeodSystems()
                val tempList = geodesSystemsList.filter { it.name.contains(text!!, true) }
                geodesSystemsList.clear()
                geodesSystemsList.addAll(tempList)
                geodesSystemsRecyclerView.adapter!!.notifyDataSetChanged()
            } else {
                getCartSystems()
                val tempList = cartSystemsList.filter { it.name.contains(text!!, true) }
                cartSystemsList.clear()
                cartSystemsList.addAll(tempList)
                cartSystemsRecyclerView.adapter!!.notifyDataSetChanged()
            }
            if (typeSpinner.selectedItem == resources.getStringArray(R.array.system_types)[1]) {
                getCartSystems()
                val tempList = cartSystemsList.filter { it.name.contains(text!!, true) }
                cartSystemsList.clear()
                cartSystemsList.addAll(tempList)
                cartSystemsRecyclerView.adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun createPanel() {
        initTopPanel(getString(R.string.str_coord_system_activity_title))
    }

    private fun setAdapters() {
        geodesSystemsRecyclerView.adapter = CoordinateSystemsAdapter(
            geodesSystemsList,
            this
        )

        cartSystemsRecyclerView.adapter = CoordinateSystemsAdapter(
            cartSystemsList,
            this
        )
    }

    private fun setSystemsArrays() {
        getGeodSystems()
        getCartSystems()
    }

    private fun getCartSystems() = runBlocking {
        launch {
            cartSystemsList.clear()
            cartSystemsList.addAll(LocalDB.getDatabase(panelContext).getCoordinateSystemDao().get())
        }
        cartSystemsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun getGeodSystems() {
        geodesSystemsList.clear()
        geodesSystemsList.add(
            CoordinateSystem(
                type = CoordinateSystem.GEODES_TYPE,
                name = "WGS 84 (World Geodetic System)",
                ellipsoid = Ellipsoid(),
                igd = Parameters()
            )
        )
        geodesSystemsList.add(
            CoordinateSystem(
                type = CoordinateSystem.GEODES_TYPE,
                name = "ГСК-2011",
                ellipsoid = Ellipsoid(
                    ellipsoidName = "ГСК-2011",
                    bHalfShaft = 6378136.5,
                    compression = 298.2564151
                ),
                igd = Parameters(
                    dx = 0.013,
                    dy = -0.092,
                    dz = -0.03,
                    rx = 0.001738,
                    ry = -0.003559,
                    rz = 0.004263,
                    scale = 0.0074
                )
            )
        )
        geodesSystemsList.add(
            CoordinateSystem(
                type = CoordinateSystem.GEODES_TYPE,
                name = "Пулково 42",
                ellipsoid = Ellipsoid(
                    ellipsoidName = "Красовского 1940 года",
                    bHalfShaft = 6378245.0,
                    compression = 298.3
                ),
                igd = Parameters(
                    dx = 23.57,
                    dy = -140.95,
                    dz = -79.8,
                    rx = 0.0,
                    ry = -0.35,
                    rz = -0.79,
                    scale = -0.22
                )
            )
        )
        geodesSystemsList.add(
            CoordinateSystem(
                type = CoordinateSystem.GEODES_TYPE,
                name = "ПЗ-90.11 (Параметры Земли 1990 года)",
                ellipsoid = Ellipsoid(
                    ellipsoidName = "ПЗ-90",
                    bHalfShaft = 6378136.0,
                    compression = 298.25784
                ),
                igd = Parameters(
                    dx = 0.013,
                    dy = -0.106,
                    dz = -0.022,
                    rx = 0.0023,
                    ry = -0.00354,
                    rz = 0.00421,
                    scale = 0.008
                )
            )
        )
        geodesSystemsList.add(
            CoordinateSystem(
                type = CoordinateSystem.GEODES_TYPE,
                name = "Пулково 95",
                ellipsoid = Ellipsoid(
                    ellipsoidName = "Красовского 1940 года",
                    bHalfShaft = 6378245.0,
                    compression = 298.3
                ),
                igd = Parameters(
                    dx = 24.47,
                    dy = -130.89,
                    dz = -81.56,
                    rx = 0.0,
                    ry = 0.0,
                    rz = -0.13,
                    scale = -0.22
                )
            )
        )
        geodesSystemsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Системы координат")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_coord_systems_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}