package com.nagl.smartgeoapp.activities

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nagl.smartgeoapp.R
import com.nagl.smartgeoapp.api.CoordinateApi
import com.nagl.smartgeoapp.api.NumberApi
import com.nagl.smartgeoapp.database.LocalDB
import com.nagl.smartgeoapp.database.entity.Point
import com.nagl.smartgeoapp.interfaces.ITopPanel
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

class MapActivity : AppCompatActivity(), ITopPanel {

    override var panelContext: Activity = this

    private val coordinateApi = CoordinateApi(NumberApi())
    private val graphicsOverlay = GraphicsOverlay()

    private var pointsList: ArrayList<Point> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4227555441,none,KGE60RFLTFJS2T8AG101")
        initTopPanel(getString(R.string.str_map_activity_title))
        initMap()


    }

    private fun initMap() {
        mapView.graphicsOverlays.add(graphicsOverlay)

        val baseMapType = Basemap.Type.OPEN_STREET_MAP

        val map = ArcGISMap(
            baseMapType,
            56.75,
            37.16,
            8
        )

        map.maxScale = 1.0

        mapView.map = map
        mapView.isAttributionTextVisible = false

        mapView.addMapScaleChangedListener {
            mapScaleShootingText.text = getString(R.string.map_scale, mapView.mapScale.roundToInt().toString())
        }

        getPoints()
        val wgs84 = SpatialReferences.getWgs84()
        pointsList.forEach {
            val blhCoordinate = coordinateApi.convertToCoordinateInFormat(
                it.coordinate,
                Point.BLH_WGS
            )

            val arcgisPoint = com.esri.arcgisruntime.geometry.Point(
                blhCoordinate.secondCoordinate,
                blhCoordinate.firstCoordinate,
                wgs84
            )

            val pointSymbol = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.MAGENTA, 10f)
            val pointGraphic = Graphic(arcgisPoint, pointSymbol)
            graphicsOverlay.graphics.add(pointGraphic)

            val textSymbol = TextSymbol(
                14f,
                it.name,
                Color.BLACK,
                TextSymbol.HorizontalAlignment.CENTER,
                TextSymbol.VerticalAlignment.BOTTOM
            )
            graphicsOverlay.graphics.add(Graphic(arcgisPoint, textSymbol))
        }
    }

    private fun getPoints() = runBlocking {
        pointsList.clear()
        pointsList.addAll(LocalDB.getDatabase(panelContext).getPointsDao().get())
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

    override fun actionClick() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Карта")
            .setMessage(
                @Suppress("DEPRECATION")
                Html.fromHtml(getString(R.string.str_map_help))
            )
            .setNegativeButton("Ok") { _, _ -> }
            .show()
    }
}