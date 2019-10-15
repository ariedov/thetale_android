package com.dleibovych.epictale.game.map

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.github.chrisbanes.photoview.PhotoView
import com.dleibovych.epictale.R
import org.thetale.api.enumerations.MapCellType
import org.thetale.api.enumerations.MapStyle
import com.dleibovych.epictale.api.response.MapCellResponse
import com.dleibovych.epictale.fragment.dialog.TabbedDialog
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.DialogUtils
import com.dleibovych.epictale.util.ObjectUtils
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.UiUtils
import kotlinx.android.synthetic.main.fragment_map.*
import org.thetale.api.models.*

import java.util.ArrayList
import java.util.HashMap

import javax.inject.Inject

class MapFragment : Fragment(), MapView {

    @Inject
    lateinit var presenter: MapPresenter

    private var rootView: View? = null

    private var mapView: PhotoView? = null
    private var menuOptions: MenuItem? = null
    private var menuMapModification: MenuItem? = null

    private var mapZoom: Float = 0.toFloat()
    private var mapShiftX: Float = 0.toFloat()
    private var mapShiftY: Float = 0.toFloat()
    private var shouldShowMenuOptions = true

    private var places: MutableList<Place>? = null

    private val mapModification: MapModification = MapModification.None
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
        } else {
            mapZoom = 1.0f
            mapShiftX = 0.0f
            mapShiftY = 0.0f
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.onRetryClick(View.OnClickListener { presenter.retry() })
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun onStop() {
        super.onStop()

        presenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
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

    override fun showLoading() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE
        mapView!!.visibility = View.GONE
    }

    override fun drawMap(bitmap: Bitmap, region: Region, hero: HeroPosition) {
        mapView!!.visibility = View.VISIBLE
        error.visibility = View.GONE
        progress.visibility = View.GONE

        setMap(bitmap, region, hero)
    }

    override fun showError(t: Throwable) {
        error.visibility = View.VISIBLE
        progress.visibility = View.GONE
        mapView!!.visibility = View.GONE

        error.setErrorText(getString(R.string.map_error))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menuOptions = UiUtils.getMenuItem(activity, R.id.action_map_actions)
        if (menuOptions != null && !shouldShowMenuOptions) {
            menuOptions!!.isVisible = false
        }

        menuMapModification = UiUtils.getMenuItem(activity, R.id.action_map_modification)

        updateMenuItemTitle(R.id.action_map_style, getString(R.string.map_style, PreferencesManager.getMapStyle().styleName))
        updateMenuItemTitle(R.id.action_map_modification, getString(R.string.map_modification, mapModification!!.name))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_map_style -> {
                DialogUtils.showChoiceDialog(fragmentManager, getString(R.string.map_style_caption),
                        ObjectUtils.getNamesForEnum(MapStyle::class.java)) { position ->
                    PreferencesManager.setMapStyle(MapStyle.values()[position])
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
//                val progressDialog = ProgressDialog.show(activity,
//                        getString(R.string.map_save), getString(R.string.map_save_progress), true, false)
//                Thread(Runnable {
//                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                    path.mkdirs()
//
//                    val filenameBase = String.format("the-tale_map_%s",
//                            SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date()))
//                    var file: File
//                    var counter = 0
//                    do {
//                        val filename = if (counter == 0)
//                            "$filenameBase.png"
//                        else
//                            String.format("%s_%d.png", filenameBase, counter)
//                        file = File(path, filename)
//                        counter++
//                    } while (file.exists())
//
//                    var output: OutputStream? = null
//                    try {
//                        output = FileOutputStream(file)
//                        (mapView!!.drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.PNG, 90, output)
//                    } catch (e: FileNotFoundException) {
//                        showMapSaveError(e.localizedMessage)
//                    }
//
//                    if (output != null) {
//                        var success = false
//                        try {
//                            output.close()
//                            success = true
//                        } catch (e: IOException) {
//                            showMapSaveError(e.localizedMessage)
//                        }
//
//                        //                            if(success && !UiUtils.getMainActivity(MapFragment.this).isPaused()) {
//                        //                                DialogUtils.showConfirmationDialog(getChildFragmentManager(),
//                        //                                        getString(R.string.map_save), getString(R.string.map_save_message, fileMap.getPath()),
//                        //                                        null, null,
//                        //                                        getString(R.string.map_save_open), () -> {
//                        //                                            final Intent intent = new Intent();
//                        //                                            intent.setAction(Intent.ACTION_VIEW);
//                        //                                            intent.setDataAndType(Uri.fromFile(fileMap), "image/png");
//                        //                                            startActivity(intent);
//                        //                                        }, null);
//                        //                            }
//                    }
//
//                    activity!!.runOnUiThread { progressDialog.dismiss() }
//                }).start()
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

        val width = mapView!!.drawable.intrinsicWidth
        val height = mapView!!.drawable.intrinsicHeight

        mapView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val viewWidth = mapView!!.width
                val viewHeight = mapView!!.height
                if (viewWidth != 0 && viewHeight != 0) {
                    val currentSizeDenominator = MapDrawer.currentSizeDenominator
                    val minimumScale: Float = if (viewWidth < viewHeight) {
                        viewWidth.toFloat() / width
                    } else {
                        viewHeight.toFloat() / height
                    }

                    mapView!!.maximumScale = ZOOM_MAX * currentSizeDenominator
                    mapView!!.mediumScale = (ZOOM_MAX * currentSizeDenominator + minimumScale) / 2.0f
                    mapView!!.minimumScale = minimumScale
                    val placeInfo = region.places[PreferencesManager.getMapCenterPlaceId()]
                    if (placeInfo == null) {
                        moveToTile(Math.round(heroPosition.x).toInt(), Math.round(heroPosition.y).toInt(),
                                mapView!!.mediumScale)
                    } else {
                        PreferencesManager.setMapCenterPlaceId(-1)
                        moveToTile(placeInfo.pos.x, placeInfo.pos.y, mapView!!.maximumScale)
                    }

                    UiUtils.removeGlobalLayoutListener(mapView!!, this)
                }
            }
        })

        mapView!!.setOnPhotoTapListener { view, x, y ->
            //            val tileX = Math.floor((x * width.toFloat() * MapDrawer.currentSizeDenominator.toFloat() / MapDrawer.MAP_TILE_SIZE).toDouble()).toInt()
//            val tileY = Math.floor((y * height.toFloat() * MapDrawer.currentSizeDenominator.toFloat() / MapDrawer.MAP_TILE_SIZE).toDouble()).toInt()

//            DialogUtils.showTabbedDialog(childFragmentManager, getString(R.string.drawer_title_map), null)

//            MapCellRequest().execute(tileX, tileY, RequestUtils.wrapCallback(object : CommonResponseCallback<MapCellResponse, String> {
//                override fun processResponse(response: MapCellResponse) {
//                    // request may be completed before fragment is instantiated, we'll wait for it
//                    val handler = Handler()
//                    handler.post(object : Runnable {
//                        override fun run() {
//                            val dialog = childFragmentManager.findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG) as TabbedDialog
//                            if (dialog == null) {
//                                handler.post(this)
//                            } else {
//                                dialog.setCaption(response.title
//                                        ?: getString(R.string.map_tile_caption, tileX, tileY))
//                                dialog.setTabsAdapter(TileTabsAdapter(response))
//                                dialog.setMode(DataViewMode.DATA)
//                            }
//                        }
//                    })
//                }
//
//                override fun processError(error: String) {
//                    val handler = Handler()
//                    handler.post(object : Runnable {
//                        override fun run() {
//                            val dialog = childFragmentManager.findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG) as TabbedDialog
//                            dialog?.dismiss() ?: handler.post(this)
//                        }
//                    })
////                    setError(getString(R.string.map_error))
//                }
//            }, this@MapFragment))
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
    }

    private inner class TileTabsAdapter(private val cellInfo: MapCellResponse) : TabbedDialog.TabbedDialogTabsAdapter() {

        override fun getPageTitle(position: Int): CharSequence? {
            return getString(getTileTab(position).title)
        }

        override fun getItem(i: Int): androidx.fragment.app.Fragment {
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
            override fun getFragment(cellInfo: MapCellResponse): androidx.fragment.app.Fragment {
                return MapTileParamsFragment.newInstance(cellInfo)
            }
        },
        COUNCIL(R.string.map_tile_tab_council, arrayOf<MapCellType>(MapCellType.PLACE)) {
            override fun getFragment(cellInfo: MapCellResponse): androidx.fragment.app.Fragment {
                return MapTileCouncilFragment.newInstance(cellInfo)
            }
        },
        DESCRIPTION(R.string.map_tile_tab_description, arrayOf<MapCellType>(MapCellType.PLACE)) {
            override fun getFragment(cellInfo: MapCellResponse): androidx.fragment.app.Fragment {
                return MapTileDescriptionFragment.newInstance(cellInfo)
            }
        },
        TERRAIN(R.string.map_tile_tab_terrain, arrayOf<MapCellType>(MapCellType.PLACE, MapCellType.BUILDING, MapCellType.TERRAIN)) {
            override fun getFragment(cellInfo: MapCellResponse): androidx.fragment.app.Fragment {
                return MapTileTerrainFragment.newInstance(cellInfo)
            }
        };

        abstract fun getFragment(cellInfo: MapCellResponse): androidx.fragment.app.Fragment

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
