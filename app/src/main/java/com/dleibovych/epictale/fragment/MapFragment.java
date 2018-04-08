package com.dleibovych.epictale.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.dleibovych.epictale.DataViewMode;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleClientApplication;
import com.dleibovych.epictale.game.GameActivity;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.CommonResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.cache.prerequisite.InfoPrerequisiteRequest;
import com.dleibovych.epictale.api.cache.prerequisite.PrerequisiteRequest;
import com.dleibovych.epictale.api.dictionary.MapCellType;
import com.dleibovych.epictale.api.dictionary.MapStyle;
import com.dleibovych.epictale.api.model.MapPlaceInfo;
import com.dleibovych.epictale.api.model.PositionInfo;
import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.request.MapCellRequest;
import com.dleibovych.epictale.api.request.MapRequest;
import com.dleibovych.epictale.api.request.MapTerrainRequest;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.api.response.InfoResponse;
import com.dleibovych.epictale.api.response.MapCellResponse;
import com.dleibovych.epictale.api.response.MapResponse;
import com.dleibovych.epictale.api.response.MapTerrainResponse;
import com.dleibovych.epictale.fragment.dialog.TabbedDialog;
import com.dleibovych.epictale.util.DialogUtils;
import com.dleibovych.epictale.util.ObjectUtils;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.RequestUtils;
import com.dleibovych.epictale.util.UiUtils;
import com.dleibovych.epictale.util.map.MapModification;
import com.dleibovych.epictale.util.map.MapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author Hamster
 * @since 12.10.2014
 */
public class MapFragment extends WrapperFragment {

    private static final String KEY_MAP_ZOOM = "KEY_MAP_ZOOM";
    private static final String KEY_MAP_SHIFT_X = "KEY_MAP_SHIFT_X";
    private static final String KEY_MAP_SHIFT_Y = "KEY_MAP_SHIFT_Y";
    private static final String KEY_MAP_MODIFICATION = "KEY_MAP_MODIFICATION";

    @Inject OkHttpClient client;
    @Inject CookieManager manager;

    private static final float ZOOM_MAX = 3f;

    private LayoutInflater layoutInflater;

    private View rootView;

    private PhotoView mapView;
    private MenuItem menuOptions;
    private MenuItem menuMapModification;
    private View findPlayerContainer;

    private float mapZoom;
    private float mapShiftX;
    private float mapShiftY;
    private boolean isMapInitialPosition = true;
    private boolean shouldMoveToHero = false;
    private boolean shouldShowMenuOptions = true;

    private PositionInfo heroPosition;
    private List<MapPlaceInfo> places;
    private MapModification mapModification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mapModification = MapModification.NONE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TheTaleClientApplication
                .getComponentProvider()
                .getAppComponent()
                .inject(this);

        layoutInflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = rootView.findViewById(R.id.map_content);

        if(savedInstanceState != null) {
            mapZoom = savedInstanceState.getFloat(KEY_MAP_ZOOM, 1.0f);
            mapShiftX = savedInstanceState.getFloat(KEY_MAP_SHIFT_X, 0.0f);
            mapShiftY = savedInstanceState.getFloat(KEY_MAP_SHIFT_Y, 0.0f);
            mapModification = MapModification.values()[savedInstanceState.getInt(KEY_MAP_MODIFICATION, MapModification.NONE.ordinal())];
        } else {
            mapZoom = 1.0f;
            mapShiftX = 0.0f;
            mapShiftY = 0.0f;
            mapModification = MapModification.NONE;
            shouldMoveToHero = true;
        }

        findPlayerContainer = rootView.findViewById(R.id.map_find_player);
        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer, this, this, (GameActivity) getActivity());

        return wrapView(layoutInflater, rootView);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putFloat(KEY_MAP_ZOOM, getMapZoom());

        final PointF mapShift = getMapShift();
        outState.putFloat(KEY_MAP_SHIFT_X, mapShift.x);
        outState.putFloat(KEY_MAP_SHIFT_Y, mapShift.y);

        outState.putInt(KEY_MAP_MODIFICATION, mapModification.ordinal());
    }

    private void updateMenuItemTitle(final int id, final String title) {
        final MenuItem menuItem = UiUtils.getMenuItem(getActivity(), id);
        if(menuItem != null) {
            menuItem.setTitle(title);
        }
    }

    private void updateMenuMapModificationVisibility() {
      OkHttpClient client = new OkHttpClient();
      client.newCall(HttpMethod.GET.getHttpRequest(
          MapTerrainRequest.URL_BASE, null, null).getRequest()).enqueue(new Callback()
      {
        @Override
        public void onFailure(Call call, IOException e)
        {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException
        {
          if(menuMapModification != null) {
            menuMapModification.setVisible(true);
          }
        }
      });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menuOptions = UiUtils.getMenuItem(getActivity(), R.id.action_map_actions);
        if((menuOptions != null) && !shouldShowMenuOptions) {
            menuOptions.setVisible(false);
        }

        menuMapModification = UiUtils.getMenuItem(getActivity(), R.id.action_map_modification);
        updateMenuMapModificationVisibility();

        updateMenuItemTitle(R.id.action_map_style, getString(R.string.map_style, PreferencesManager.getMapStyle().getName()));
        updateMenuItemTitle(R.id.action_map_modification, getString(R.string.map_modification, mapModification.getName()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_map_style:
                DialogUtils.showChoiceDialog(getFragmentManager(), getString(R.string.map_style_caption),
                        ObjectUtils.getNamesForEnum(MapStyle.class), position -> {
                            PreferencesManager.setMapStyle(MapStyle.values()[position]);
                            refresh(true);
                        });
                return true;

            case R.id.action_map_find_place:
                final int count = places.size();
                final String[] choices = new String[count];
                for(int i = 0; i < count; i++) {
                    choices[i] = places.get(i).name;
                }

                DialogUtils.showChoiceDialog(getFragmentManager(), getString(R.string.map_find_place), choices, position -> {
                    final MapPlaceInfo placeInfo = places.get(position);
                    moveToTile(placeInfo.x, placeInfo.y, mapView.getMaximumScale());
                });
                return true;

            case R.id.action_map_find_hero:
                moveToTile((int) Math.round(heroPosition.x), (int) Math.round(heroPosition.y),
                        mapView.getMaximumScale());
                return true;

            case R.id.action_map_modification:
                DialogUtils.showChoiceDialog(getChildFragmentManager(), getString(R.string.map_modification_caption),
                        ObjectUtils.getNamesForEnum(MapModification.class), position -> {
                            mapModification = MapModification.values()[position];
                            refresh(true);
                        },
                        R.layout.dialog_content_map_modification, R.id.dialog_map_modification_list);
                return true;

            case R.id.action_map_save:
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.map_save), getString(R.string.map_save_progress), true, false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        path.mkdirs();

                        final String filenameBase = String.format("the-tale_map_%s",
                                new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date()));
                        File file;
                        int counter = 0;
                        do {
                            final String filename = counter == 0 ?
                                    filenameBase + ".png" :
                                    String.format("%s_%d.png", filenameBase, counter);
                            file = new File(path, filename);
                            counter++;
                        } while (file.exists());
                        final File fileMap = file;

                        OutputStream output = null;
                        try {
                            output = new FileOutputStream(fileMap);
                            ((BitmapDrawable) mapView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 90, output);
                        } catch(FileNotFoundException e) {
                            showMapSaveError(e.getLocalizedMessage());
                        }

                        if(output != null) {
                            boolean success = false;
                            try {
                                output.close();
                                success = true;
                            } catch (IOException e) {
                                showMapSaveError(e.getLocalizedMessage());
                            }

                            if(success && !UiUtils.getMainActivity(MapFragment.this).isPaused()) {
                                DialogUtils.showConfirmationDialog(getChildFragmentManager(),
                                        getString(R.string.map_save), getString(R.string.map_save_message, fileMap.getPath()),
                                        null, null,
                                        getString(R.string.map_save_open), () -> {
                                            final Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(fileMap), "image/png");
                                            startActivity(intent);
                                        }, null);
                            }
                        }

                        getActivity().runOnUiThread(() -> progressDialog.dismiss());
                    }
                }).start();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMapSaveError(final String error) {
        if(!UiUtils.getMainActivity(this).isPaused()) {
            DialogUtils.showMessageDialog(getChildFragmentManager(),
                    getString(R.string.common_dialog_attention_title),
                    TextUtils.isEmpty(error) ? getString(R.string.map_save_error_short) : getString(R.string.map_save_error, error));
        }
    }

    @Override
    public void refresh(final boolean isGlobal) {
        super.refresh(isGlobal);
        shouldShowMenuOptions = false;

        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer, this, this, (GameActivity) getActivity());

        if(menuOptions != null) {
            menuOptions.setVisible(false);
        }

        if(!isMapInitialPosition) {
            mapZoom = getMapZoom();

            final PointF mapShift = getMapShift();
            mapShiftX = mapShift.x;
            mapShiftY = mapShift.y;
        }

        mapView.setImageBitmap(null);
        System.gc();

        final MapStyle mapStyle = PreferencesManager.getMapStyle();
        updateMenuItemTitle(R.id.action_map_style, getString(R.string.map_style, mapStyle.getName()));

        updateMenuItemTitle(R.id.action_map_modification, getString(R.string.map_modification, mapModification.getName()));

        new InfoPrerequisiteRequest(client, manager, () -> {
            final ApiResponseCallback<GameInfoResponse> gameInfoCallback = RequestUtils.wrapCallback(new ApiResponseCallback<GameInfoResponse>() {
                @Override
                public void processResponse(final GameInfoResponse gameInfoResponse) {
                    new MapRequest(gameInfoResponse.mapVersion).execute(RequestUtils.wrapCallback(new CommonResponseCallback<MapResponse, String>() {
                        @Override
                        public void processResponse(final MapResponse mapResponse) {
                            MapUtils.getMapSprite(mapStyle, new MapUtils.MapBitmapCallback() {
                                @Override
                                public void onBitmapBuilt(final Bitmap sprite) {
                                    if(!isAdded()) {
                                        return;
                                    }

                                    heroPosition = gameInfoResponse.account.hero.position;

                                    final Bitmap map = MapUtils.getMapBitmap(mapResponse);
                                    final Canvas canvas = new Canvas(map);

                                    final MenuItem actionMapModification = UiUtils.getMenuItem(getActivity(), R.id.action_map_modification);
                                    if(actionMapModification != null) {
                                        if(MapUtils.getCurrentSizeDenominator() == 1) {
                                            updateMenuMapModificationVisibility();
                                        } else {
                                            if(!UiUtils.getMainActivity(MapFragment.this).isPaused()) {
                                                DialogUtils.showMessageDialog(getChildFragmentManager(),
                                                        getString(R.string.common_dialog_attention_title),
                                                        getString(R.string.map_decreased_quality));
                                            }
                                        }
                                    }

                                    if(mapModification == MapModification.NONE) {
                                        MapUtils.drawBaseLayer(canvas, mapResponse, sprite);
                                        MapUtils.drawPlaceNamesLayer(canvas, mapResponse);
                                        MapUtils.drawHeroLayer(canvas, gameInfoResponse.account.hero, sprite);
                                        setMap(map, mapResponse);
                                    } else {
                                        new MapTerrainRequest().execute(RequestUtils.wrapCallback(new CommonResponseCallback<MapTerrainResponse, String>() {
                                            @Override
                                            public void processResponse(final MapTerrainResponse mapTerrainResponse) {
                                                switch(mapModification) {
                                                    case WIND:
                                                        MapUtils.drawModificationLayer(canvas, mapResponse, mapTerrainResponse, mapModification);
                                                        break;

                                                    case INFLUENCE:
                                                        MapUtils.drawBaseLayer(canvas, mapResponse, sprite);
                                                        MapUtils.drawModificationLayer(canvas, mapResponse, mapTerrainResponse, mapModification);
                                                        MapUtils.drawPlaceNamesLayer(canvas, mapResponse);
                                                        MapUtils.drawHeroLayer(canvas, gameInfoResponse.account.hero, sprite);
                                                        break;
                                                }
                                                setMap(map, mapResponse);
                                            }

                                            @Override
                                            public void processError(String error) {
                                                setError(getString(R.string.map_error));
                                                mapModification = MapModification.NONE;
                                            }
                                        }, MapFragment.this));
                                    }
                                }

                                @Override
                                public void onError() {
                                    if(!isAdded()) {
                                        return;
                                    }

                                    setError(getString(R.string.map_error));
                                }
                            });
                        }

                        @Override
                        public void processError(String error) {
                            setError(getString(R.string.map_error));
                        }
                    }, MapFragment.this));
                }

                @Override
                public void processError(GameInfoResponse response) {
                    setError(getString(R.string.map_error));
                }
            }, MapFragment.this);

            final int watchingAccountId = PreferencesManager.getWatchingAccountId();
            if(watchingAccountId == 0) {
                new GameInfoRequest(client, manager, true).execute(gameInfoCallback, true);
            } else {
                new GameInfoRequest(client, manager, true).execute(watchingAccountId, gameInfoCallback, true);
            }
        }, new PrerequisiteRequest.ErrorCallback<InfoResponse>() {
            @Override
            public void processError(InfoResponse response) {
                setError(getString(R.string.map_error));
            }
        }, this).execute();
    }

    @Override
    public void onOnscreen() {
        super.onOnscreen();

        if(findPlayerContainer != null) {
            UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer, this, this, (GameActivity) getActivity());
        }
    }

    private void moveToTile(final int tileX, final int tileY, final float scale) {
        mapView.setScale(scale);
        final float newCenterX = (tileX + 0.5f) * MapUtils.MAP_TILE_SIZE / MapUtils.getCurrentSizeDenominator() * scale;
        final float newCenterY = (tileY + 0.5f) * MapUtils.MAP_TILE_SIZE / MapUtils.getCurrentSizeDenominator() * scale;
        final float newRectLeft = mapView.getWidth() / 2.0f - newCenterX;
        final float newRectTop = mapView.getHeight() / 2.0f - newCenterY;
        final RectF currentRect = mapView.getDisplayRect();
        mapView.getAttacher().onDrag(newRectLeft - currentRect.left, newRectTop - currentRect.top);
    }

    private float getMapZoom() {
        float mapZoom = mapView.getScale();
        if (mapZoom > ZOOM_MAX) {
            mapZoom = ZOOM_MAX;
        }

        return mapZoom;
    }

    private PointF getMapShift() {
        final RectF currentRect = mapView.getDisplayRect();
        final float currentDrawableWidth = currentRect.right - currentRect.left;
        final float currentDrawableHeight = currentRect.bottom - currentRect.top;
        final float viewWidth = mapView.getWidth();
        final float viewHeight = mapView.getHeight();

        final float centeredRectLeft = (viewWidth - currentDrawableWidth) / 2.0f;
        final float centeredRectTop = (viewHeight - currentDrawableHeight) / 2.0f;

        return new PointF(currentRect.left - centeredRectLeft, currentRect.top - centeredRectTop);
    }

    private void setMap(final Bitmap map, final MapResponse mapResponse) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mapView.setImageBitmap(map);
                mapView.getAttacher().update();
                if (!isMapInitialPosition) {
                    mapView.setScale(mapZoom);
                    mapView.getAttacher().onDrag(mapShiftX, mapShiftY);
                }

                final int width = mapView.getDrawable().getIntrinsicWidth();
                final int height = mapView.getDrawable().getIntrinsicHeight();

                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final int viewWidth = mapView.getWidth();
                        final int viewHeight = mapView.getHeight();
                        if ((viewWidth != 0) && (viewHeight != 0)) {
                            final int currentSizeDenominator = MapUtils.getCurrentSizeDenominator();
                            final float minimumScale;
                            if (viewWidth < viewHeight) {
                                minimumScale = (float) viewWidth / width;
                            } else {
                                minimumScale = (float) viewHeight / height;
                            }

                            if (isMapInitialPosition) {
                                isMapInitialPosition = false;
                                mapView.setMaximumScale(ZOOM_MAX * currentSizeDenominator);
                                mapView.setMediumScale((ZOOM_MAX * currentSizeDenominator + minimumScale) / 2.0f);
                                mapView.setMinimumScale(minimumScale);
                                final MapPlaceInfo placeInfo = mapResponse.places.get(PreferencesManager.getMapCenterPlaceId());
                                if(placeInfo == null) {
                                    if(shouldMoveToHero) {
                                        shouldMoveToHero = false;
                                        moveToTile((int) Math.round(heroPosition.x), (int) Math.round(heroPosition.y),
                                                mapView.getMediumScale());
                                    } else {
                                        mapView.setScale(mapView.getMediumScale());
                                        mapView.getAttacher().onDrag(mapShiftX, mapShiftY);
                                    }
                                } else {
                                    PreferencesManager.setMapCenterPlaceId(-1);
                                    moveToTile(placeInfo.x, placeInfo.y, mapView.getMaximumScale());
                                }
                            }

                            UiUtils.removeGlobalLayoutListener(mapView, this);
                        }
                    }
                });

                mapView.setOnPhotoTapListener(new OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(ImageView view, float x, float y) {
                        final int tileX = (int) Math.floor(x * width * MapUtils.getCurrentSizeDenominator() / MapUtils.MAP_TILE_SIZE);
                        final int tileY = (int) Math.floor(y * height * MapUtils.getCurrentSizeDenominator() / MapUtils.MAP_TILE_SIZE);

                        DialogUtils.showTabbedDialog(getChildFragmentManager(), getString(R.string.drawer_title_map), null);

                        new MapCellRequest().execute(tileX, tileY, RequestUtils.wrapCallback(new CommonResponseCallback<MapCellResponse, String>() {
                            @Override
                            public void processResponse(final MapCellResponse response) {
                                // request may be completed before fragment is instantiated, we'll wait for it
                                final Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        final TabbedDialog dialog = (TabbedDialog) getChildFragmentManager().findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG);
                                        if (dialog == null) {
                                            handler.post(this);
                                        } else {
                                            dialog.setCaption(response.title == null ? getString(R.string.map_tile_caption, tileX, tileY) : response.title);
                                            dialog.setTabsAdapter(new TileTabsAdapter(response));
                                            dialog.setMode(DataViewMode.DATA);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void processError(String error) {
                                final Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        final TabbedDialog dialog = (TabbedDialog) getChildFragmentManager().findFragmentByTag(DialogUtils.DIALOG_TABBED_TAG);
                                        if (dialog == null) {
                                            handler.post(this);
                                        } else {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                setError(getString(R.string.map_error));
                            }
                        }, MapFragment.this));
                    }
                });

                places = new ArrayList<>(mapResponse.places.size());
                for (final MapPlaceInfo placeInfo : mapResponse.places.values()) {
                    places.add(placeInfo);
                }
                Collections.sort(places, (lhs, rhs) -> lhs.name.compareTo(rhs.name));

                shouldShowMenuOptions = true;
                if(menuOptions != null) {
                    menuOptions.setVisible(true);
                }

                setMode(DataViewMode.DATA);
            }
        });
    }

    private class TileTabsAdapter extends TabbedDialog.TabbedDialogTabsAdapter {

        private final MapCellResponse cellInfo;

        public TileTabsAdapter(final MapCellResponse cellInfo) {
            this.cellInfo = cellInfo;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTileTab(position).getTitle();
        }

        @Override
        public Fragment getItem(int i) {
            return getTileTab(i).getFragment(cellInfo);
        }

        @Override
        public int getCount() {
            return TileTab.getTabs(cellInfo.type).size();
        }

        private TileTab getTileTab(final int position) {
            return TileTab.getTabs(cellInfo.type).get(position);
        }

    }

    private enum TileTab {

        PARAMETERS(R.string.map_tile_tab_params, new MapCellType[]{MapCellType.PLACE, MapCellType.BUILDING}) {
            @Override
            public Fragment getFragment(MapCellResponse cellInfo) {
                return MapTileParamsFragment.newInstance(cellInfo);
            }
        },
        COUNCIL(R.string.map_tile_tab_council, new MapCellType[]{MapCellType.PLACE}) {
            @Override
            public Fragment getFragment(MapCellResponse cellInfo) {
                return MapTileCouncilFragment.newInstance(cellInfo);
            }
        },
        DESCRIPTION(R.string.map_tile_tab_description, new MapCellType[]{MapCellType.PLACE}) {
            @Override
            public Fragment getFragment(MapCellResponse cellInfo) {
                return MapTileDescriptionFragment.newInstance(cellInfo);
            }
        },
        TERRAIN(R.string.map_tile_tab_terrain, new MapCellType[]{MapCellType.PLACE, MapCellType.BUILDING, MapCellType.TERRAIN}) {
            @Override
            public Fragment getFragment(MapCellResponse cellInfo) {
                return MapTileTerrainFragment.newInstance(cellInfo);
            }
        },
        ;

        private final int titleResId;
        private final MapCellType[] cellTypes;

        TileTab(final int titleResId, final MapCellType[] cellTypes) {
            this.titleResId = titleResId;
            this.cellTypes = cellTypes;
        }

        public String getTitle() {
            return TheTaleClientApplication.getContext().getString(titleResId);
        }

        public abstract Fragment getFragment(final MapCellResponse cellInfo);

        private static final Map<MapCellType, List<TileTab>> tabs;

        static {
            tabs = new HashMap<>(MapCellType.values().length);
            for(final TileTab tileTab : values()) {
                for(final MapCellType cellType : tileTab.cellTypes) {
                    List<TileTab> tileTabs = tabs.get(cellType);
                    if(tileTabs == null) {
                        tileTabs = new ArrayList<>();
                        tileTabs.add(tileTab);
                        tabs.put(cellType, tileTabs);
                    } else {
                        tileTabs.add(tileTab);
                    }
                }
            }
        }

        public static List<TileTab> getTabs(final MapCellType cellType) {
            return tabs.get(cellType);
        }

    }

}
