package com.dleibovych.epictale.game.map

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

import com.dleibovych.epictale.fragment.MapTileCouncilFragment
import com.dleibovych.epictale.fragment.MapTileDescriptionFragment
import com.dleibovych.epictale.fragment.MapTileParamsFragment
import com.dleibovych.epictale.fragment.MapTileTerrainFragment
import com.dleibovych.epictale.fragment.WrapperFragment
import com.github.chrisbanes.photoview.PhotoView
import com.dleibovych.epictale.DataViewMode
import com.dleibovych.epictale.R
import com.dleibovych.epictale.game.MainActivity
import com.dleibovych.epictale.api.CommonResponseCallback
import com.dleibovych.epictale.api.HttpMethod
import com.dleibovych.epictale.api.dictionary.MapCellType
import com.dleibovych.epictale.api.dictionary.MapStyle
import com.dleibovych.epictale.api.request.MapCellRequest
import com.dleibovych.epictale.api.request.MapTerrainRequest
import com.dleibovych.epictale.api.response.MapCellResponse
import com.dleibovych.epictale.api.response.MapTerrainResponse
import com.dleibovych.epictale.fragment.dialog.TabbedDialog
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.DialogUtils
import com.dleibovych.epictale.util.ObjectUtils
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.RequestUtils
import com.dleibovych.epictale.util.UiUtils
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.CookieManager
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale

import javax.inject.Inject

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.thetale.api.models.Hero
import org.thetale.api.models.HeroPosition
import org.thetale.api.models.Place
import org.thetale.api.models.Region

class MapFragment : WrapperFragment(), MapView {

    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var manager: CookieManager

    @Inject
    lateinit var presenter: MapPresenter
    @Inject
    lateinit var drawer: MapDrawer

    private var rootView: View? = null

    private var mapView: PhotoView? = null
    private var menuOptions: MenuItem? = null
    private var menuMapModification: MenuItem? = null
    private var findPlayerContainer: View? = null

    private var mapZoom: Float = 0.toFloat()
    private var mapShiftX: Float = 0.toFloat()
    private var mapShiftY: Float = 0.toFloat()
    private var isMapInitialPosition = true
    private var shouldMoveToHero = false
    private var shouldShowMenuOptions = true

    private var places: MutableList<Place>? = null
    private var mapModification: MapModification? = null

    private val mapShift: PointF
        get() {
            val currentRect = mapView!!.displayRect
            val currentDrawableWidth = currentRect.right - currentRect.left
            val currentDrawableHeight = currentRect.bottom - currentRect.top
            val viewWidth = mapView!!.width.toFloat()
            val viewHeight = mapView!!.height.toFloat()

            val centeredRectLeft = (viewWidth - currentDrawableWidth) / 2.0f
            val centeredRectTop = (viewHeight - currentDrawableHeight) / 2.0f

            return PointF(currentRect.left - centeredRectLeft, currentRect.top - centeredRectTop)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mapModification = MapModification.None
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as GameComponentProvider)
                .provideGameComponent()
                ?.inject(this)

        presenter.view = this

        rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView!!.findViewById(R.id.map_content)

        if (savedInstanceState != null) {
            mapZoom = savedInstanceState.getFloat(KEY_MAP_ZOOM, 1.0f)
            mapShiftX = savedInstanceState.getFloat(KEY_MAP_SHIFT_X, 0.0f)
            mapShiftY = savedInstanceState.getFloat(KEY_MAP_SHIFT_Y, 0.0f)
            mapModification = MapModification.None
        } else {
            mapZoom = 1.0f
            mapShiftX = 0.0f
            mapShiftY = 0.0f
            mapModification = MapModification.None
            shouldMoveToHero = true
        }

        findPlayerContainer = rootView!!.findViewById(R.id.map_find_player)
        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)

        return wrapView(layoutInflater, rootView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putFloat(KEY_MAP_ZOOM, getMapZoom())

        val mapShift = mapShift
        outState.putFloat(KEY_MAP_SHIFT_X, mapShift.x)
        outState.putFloat(KEY_MAP_SHIFT_Y, mapShift.y)
    }

    private fun updateMenuItemTitle(id: Int, title: String) {
        val menuItem = UiUtils.getMenuItem(activity, id)
        if (menuItem != null) {
            menuItem.title = title
        }
    }

    private fun updateMenuMapModificationVisibility() {
        client.newCall(HttpMethod.GET.getHttpRequest(
                MapTerrainRequest.URL_BASE, null, null).request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (menuMapModification != null) {
                    menuMapModification!!.isVisible = true
                }
            }
        })
    }

    override fun drawMap(region: Region, hero: Hero) {
        launch(UI) {
            val sprite = drawer.getMapSprite(getContext()!!, MapStyle.STANDARD)

            val map = drawer.getMapBitmap(region)
            val canvas = Canvas(map)

            val actionMapModification = UiUtils.getMenuItem(activity, R.id.action_map_modification)
            if (actionMapModification != null) {
                if (MapDrawer.currentSizeDenominator == 1) {
                    updateMenuMapModificationVisibility()
                } else {
                    //                                            if(!UiUtils.getMainActivity(MapFragment.this).isPaused()) {
                    //                                                DialogUtils.showMessageDialog(getChildFragmentManager(),
                    //                                                        getString(R.string.common_dialog_attention_title),
                    //                                                        getString(R.string.map_decreased_quality));
                    //                                            }
                }
            }

            if (mapModification === MapModification.None) {
                drawer.drawBaseLayer(canvas, region, sprite)
                drawer.drawPlaceNamesLayer(canvas, region)
                drawer.drawHeroLayer(canvas, hero, sprite)
                setMap(map, region, hero.position)
            } else {
                MapTerrainRequest().execute(RequestUtils.wrapCallback(object : CommonResponseCallback<MapTerrainResponse, String> {
                    override fun processResponse(mapTerrainResponse: MapTerrainResponse) {
                        when (mapModification) {
                            MapModification.Wind -> drawer.drawModificationLayer(canvas, region, mapTerrainResponse, mapModification!!)

                            MapModification.Influence -> {
                                drawer.drawBaseLayer(canvas, region, sprite)
                                drawer.drawModificationLayer(canvas, region, mapTerrainResponse, mapModification!!)
                                drawer.drawPlaceNamesLayer(canvas, region)
                                drawer.drawHeroLayer(canvas, hero, sprite)
                            }
                        }
                        setMap(map, region, hero.position)
                    }

                    override fun processError(error: String) {
                        setError(getString(R.string.map_error))
                        mapModification = MapModification.None
                    }
                }, this@MapFragment))
            }
        }
    }

    override fun showError(t: Throwable) {
        setError(getString(R.string.map_error))
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        menuOptions = UiUtils.getMenuItem(activity, R.id.action_map_actions)
        if (menuOptions != null && !shouldShowMenuOptions) {
            menuOptions!!.isVisible = false
        }

        menuMapModification = UiUtils.getMenuItem(activity, R.id.action_map_modification)
        updateMenuMapModificationVisibility()

        updateMenuItemTitle(R.id.action_map_style, getString(R.string.map_style, PreferencesManager.getMapStyle().getName()))
        updateMenuItemTitle(R.id.action_map_modification, getString(R.string.map_modification, mapModification!!.name))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_map_style -> {
                DialogUtils.showChoiceDialog(fragmentManager, getString(R.string.map_style_caption),
                        ObjectUtils.getNamesForEnum(MapStyle::class.java)) { position ->
                    PreferencesManager.setMapStyle(MapStyle.values()[position])
                    refresh(true)
                }
                return true
            }

            R.id.action_map_find_place -> {
                val count = places!!.size
                val choices = arrayOfNulls<String>(count)
                for (i in 0 until count) {
                    choices[i] = places!![i].name
                }

//                DialogUtils.showChoiceDialog(fragmentManager, getString(R.string.map_find_place), choices) { position ->
//                    val placeInfo = places!![position]
//                    moveToTile(placeInfo.x, placeInfo.y, mapView!!.maximumScale)
//                }
                return true
            }

            R.id.action_map_find_hero -> {
//                moveToTile(Math.round(heroPosition!!.x).toInt(), Math.round(heroPosition!!.y).toInt(),
//                        mapView!!.maximumScale)
                return true
            }

            R.id.action_map_modification -> {
//                DialogUtils.showChoiceDialog(childFragmentManager, getString(R.string.map_modification_caption),
//                        ObjectUtils.getNamesForEnum(MapModification::class.java), { position ->
//                    mapModification = MapModification.values()[position]
//                    refresh(true)
//                },
//                        R.layout.dialog_content_map_modification, R.id.dialog_map_modification_list)
                return true
            }

            R.id.action_map_save -> {
                val progressDialog = ProgressDialog.show(activity,
                        getString(R.string.map_save), getString(R.string.map_save_progress), true, false)
                Thread(Runnable {
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    path.mkdirs()

                    val filenameBase = String.format("the-tale_map_%s",
                            SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date()))
                    var file: File
                    var counter = 0
                    do {
                        val filename = if (counter == 0)
                            "$filenameBase.png"
                        else
                            String.format("%s_%d.png", filenameBase, counter)
                        file = File(path, filename)
                        counter++
                    } while (file.exists())

                    var output: OutputStream? = null
                    try {
                        output = FileOutputStream(file)
                        (mapView!!.drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.PNG, 90, output)
                    } catch (e: FileNotFoundException) {
                        showMapSaveError(e.localizedMessage)
                    }

                    if (output != null) {
                        var success = false
                        try {
                            output.close()
                            success = true
                        } catch (e: IOException) {
                            showMapSaveError(e.localizedMessage)
                        }

                        //                            if(success && !UiUtils.getMainActivity(MapFragment.this).isPaused()) {
                        //                                DialogUtils.showConfirmationDialog(getChildFragmentManager(),
                        //                                        getString(R.string.map_save), getString(R.string.map_save_message, fileMap.getPath()),
                        //                                        null, null,
                        //                                        getString(R.string.map_save_open), () -> {
                        //                                            final Intent intent = new Intent();
                        //                                            intent.setAction(Intent.ACTION_VIEW);
                        //                                            intent.setDataAndType(Uri.fromFile(fileMap), "image/png");
                        //                                            startActivity(intent);
                        //                                        }, null);
                        //                            }
                    }

                    activity!!.runOnUiThread { progressDialog.dismiss() }
                }).start()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showMapSaveError(error: String) {
        //        if(!UiUtils.getMainActivity(this).isPaused()) {
        //            DialogUtils.showMessageDialog(getChildFragmentManager(),
        //                    getString(R.string.common_dialog_attention_title),
        //                    TextUtils.isEmpty(error) ? getString(R.string.map_save_error_short) : getString(R.string.map_save_error, error));
        //        }
    }

    override fun refresh(isGlobal: Boolean) {
        super.refresh(isGlobal)
        shouldShowMenuOptions = false

        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)

        if (menuOptions != null) {
            menuOptions!!.isVisible = false
        }

        if (!isMapInitialPosition) {
            mapZoom = getMapZoom()

            val mapShift = mapShift
            mapShiftX = mapShift.x
            mapShiftY = mapShift.y
        }

        mapView!!.setImageBitmap(null)

        val mapStyle = PreferencesManager.getMapStyle()
        updateMenuItemTitle(R.id.action_map_style, getString(R.string.map_style, mapStyle.getName()))

        updateMenuItemTitle(R.id.action_map_modification, getString(R.string.map_modification, mapModification!!.name))

        presenter.loadMap()
    }

    override fun onOnscreen() {
        super.onOnscreen()

        if (findPlayerContainer != null) {
            UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)
        }
    }

    private fun moveToTile(tileX: Int, tileY: Int, scale: Float) {
        mapView!!.scale = scale
        val newCenterX = (tileX + 0.5f) * MapDrawer.MAP_TILE_SIZE / MapDrawer.currentSizeDenominator * scale
        val newCenterY = (tileY + 0.5f) * MapDrawer.MAP_TILE_SIZE / MapDrawer.currentSizeDenominator * scale
        val newRectLeft = mapView!!.width / 2.0f - newCenterX
        val newRectTop = mapView!!.height / 2.0f - newCenterY
        val currentRect = mapView!!.displayRect
        mapView!!.attacher.onDrag(newRectLeft - currentRect.left, newRectTop - currentRect.top)
    }

    private fun getMapZoom(): Float {
        var mapZoom = mapView!!.scale
        if (mapZoom > ZOOM_MAX) {
            mapZoom = ZOOM_MAX
        }

        return mapZoom
    }

    private fun setMap(map: Bitmap, region: Region, heroPosition: HeroPosition) {
        mapView!!.setImageBitmap(map)
        mapView!!.attacher.update()
        if (!isMapInitialPosition) {
            mapView!!.scale = mapZoom
            mapView!!.attacher.onDrag(mapShiftX, mapShiftY)
        }

        val width = mapView!!.drawable.intrinsicWidth
        val height = mapView!!.drawable.intrinsicHeight

        mapView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val viewWidth = mapView!!.width
                val viewHeight = mapView!!.height
                if (viewWidth != 0 && viewHeight != 0) {
                    val currentSizeDenominator = MapDrawer.currentSizeDenominator
                    val minimumScale: Float
                    if (viewWidth < viewHeight) {
                        minimumScale = viewWidth.toFloat() / width
                    } else {
                        minimumScale = viewHeight.toFloat() / height
                    }

                    if (isMapInitialPosition) {
                        isMapInitialPosition = false
                        mapView!!.maximumScale = ZOOM_MAX * currentSizeDenominator
                        mapView!!.mediumScale = (ZOOM_MAX * currentSizeDenominator + minimumScale) / 2.0f
                        mapView!!.minimumScale = minimumScale
                        val placeInfo = region.places[PreferencesManager.getMapCenterPlaceId()]
                        if (placeInfo == null) {
                            if (shouldMoveToHero) {
                                shouldMoveToHero = false
                                moveToTile(Math.round(heroPosition.x).toInt(), Math.round(heroPosition.y).toInt(),
                                        mapView!!.mediumScale)
                            } else {
                                mapView!!.scale = mapView!!.mediumScale
                                mapView!!.attacher.onDrag(mapShiftX, mapShiftY)
                            }
                        } else {
                            PreferencesManager.setMapCenterPlaceId(-1)
                            moveToTile(placeInfo.pos.x, placeInfo.pos.y, mapView!!.maximumScale)
                        }
                    }

                    UiUtils.removeGlobalLayoutListener(mapView!!, this)
                }
            }
        })

        mapView!!.setOnPhotoTapListener { view, x, y ->
            val tileX = Math.floor((x * width.toFloat() * MapDrawer.currentSizeDenominator.toFloat() / MapDrawer.MAP_TILE_SIZE).toDouble()).toInt()
            val tileY = Math.floor((y * height.toFloat() * MapDrawer.currentSizeDenominator.toFloat() / MapDrawer.MAP_TILE_SIZE).toDouble()).toInt()

            DialogUtils.showTabbedDialog(childFragmentManager, getString(R.string.drawer_title_map), null)

            MapCellRequest().execute(tileX, tileY, RequestUtils.wrapCallback(object : CommonResponseCallback<MapCellResponse, String> {
                override fun processResponse(response: MapCellResponse) {
                    // request may be completed before fragment is instantiated, we'll wait for it
                    val handler = Handler()
                    handler.post(object : Runnable {
                        override fun run() {
                            val dialog = childFragmentManager.findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG) as TabbedDialog
                            if (dialog == null) {
                                handler.post(this)
                            } else {
                                dialog.setCaption(response.title
                                        ?: getString(R.string.map_tile_caption, tileX, tileY))
                                dialog.setTabsAdapter(TileTabsAdapter(response))
                                dialog.setMode(DataViewMode.DATA)
                            }
                        }
                    })
                }

                override fun processError(error: String) {
                    val handler = Handler()
                    handler.post(object : Runnable {
                        override fun run() {
                            val dialog = childFragmentManager.findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG) as TabbedDialog
                            dialog?.dismiss() ?: handler.post(this)
                        }
                    })
                    setError(getString(R.string.map_error))
                }
            }, this@MapFragment))
        }

        places = ArrayList(region.places.size)
        for (placeInfo in region.places.values) {
            places!!.add(placeInfo)
        }
        places!!.sortWith(Comparator { lhs, rhs -> lhs.name.compareTo(rhs.name) })

        shouldShowMenuOptions = true
        if (menuOptions != null) {
            menuOptions!!.isVisible = true
        }

        setMode(DataViewMode.DATA)
    }

    private inner class TileTabsAdapter(private val cellInfo: MapCellResponse) : TabbedDialog.TabbedDialogTabsAdapter() {

        override fun getPageTitle(position: Int): CharSequence? {
            return getString(getTileTab(position).title)
        }

        override fun getItem(i: Int): Fragment {
            return getTileTab(i).getFragment(cellInfo)
        }

        override fun getCount(): Int {
            return TileTab.getTabs(cellInfo.type).size
        }

        private fun getTileTab(position: Int): TileTab {
            return TileTab.getTabs(cellInfo.type)[position]
        }

    }

    private enum class TileTab private constructor(val title: Int, private val cellTypes: Array<MapCellType>) {

        PARAMETERS(R.string.map_tile_tab_params, arrayOf<MapCellType>(MapCellType.PLACE, MapCellType.BUILDING)) {
            override fun getFragment(cellInfo: MapCellResponse): Fragment {
                return MapTileParamsFragment.newInstance(cellInfo)
            }
        },
        COUNCIL(R.string.map_tile_tab_council, arrayOf<MapCellType>(MapCellType.PLACE)) {
            override fun getFragment(cellInfo: MapCellResponse): Fragment {
                return MapTileCouncilFragment.newInstance(cellInfo)
            }
        },
        DESCRIPTION(R.string.map_tile_tab_description, arrayOf<MapCellType>(MapCellType.PLACE)) {
            override fun getFragment(cellInfo: MapCellResponse): Fragment {
                return MapTileDescriptionFragment.newInstance(cellInfo)
            }
        },
        TERRAIN(R.string.map_tile_tab_terrain, arrayOf<MapCellType>(MapCellType.PLACE, MapCellType.BUILDING, MapCellType.TERRAIN)) {
            override fun getFragment(cellInfo: MapCellResponse): Fragment {
                return MapTileTerrainFragment.newInstance(cellInfo)
            }
        };

        abstract fun getFragment(cellInfo: MapCellResponse): Fragment

        companion object {

            private val tabs: MutableMap<MapCellType, MutableList<TileTab>>

            init {
                tabs = HashMap(MapCellType.values().size)
                for (tileTab in values()) {
                    for (cellType in tileTab.cellTypes) {
                        var tileTabs = tabs[cellType]
                        if (tileTabs == null) {
                            tileTabs = ArrayList()
                            tileTabs.add(tileTab)
                            tabs[cellType] = tileTabs
                        } else {
                            tileTabs.add(tileTab)
                        }
                    }
                }
            }

            fun getTabs(cellType: MapCellType): List<TileTab> {
                return tabs[cellType]!!
            }
        }

    }

    companion object {

        private const val KEY_MAP_ZOOM = "KEY_MAP_ZOOM"
        private const val KEY_MAP_SHIFT_X = "KEY_MAP_SHIFT_X"
        private const val KEY_MAP_SHIFT_Y = "KEY_MAP_SHIFT_Y"
        private const val KEY_MAP_MODIFICATION = "KEY_MAP_MODIFICATION"

        private const val ZOOM_MAX = 3f
    }

}
