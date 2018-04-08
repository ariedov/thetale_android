package com.dleibovych.epictale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dleibovych.epictale.DataViewMode;
import com.dleibovych.epictale.DrawerItem;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleClientApplication;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.cache.RequestCacheManager;
import com.dleibovych.epictale.api.cache.prerequisite.InfoPrerequisiteRequest;
import com.dleibovych.epictale.api.cache.prerequisite.PrerequisiteRequest;
import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.request.LogoutRequest;
import com.dleibovych.epictale.api.response.CommonResponse;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.api.response.InfoResponse;
import com.dleibovych.epictale.fragment.GameFragment;
import com.dleibovych.epictale.fragment.NavigationDrawerFragment;
import com.dleibovych.epictale.fragment.Refreshable;
import com.dleibovych.epictale.fragment.WrapperFragment;
import com.dleibovych.epictale.login.LoginActivity;
import com.dleibovych.epictale.util.DialogUtils;
import com.dleibovych.epictale.util.HistoryStack;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.TextToSpeechUtils;
import com.dleibovych.epictale.util.UiUtils;
import com.dleibovych.epictale.util.WebsiteUtils;
import com.dleibovych.epictale.util.onscreen.OnscreenPart;

import java.net.CookieManager;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX";
    public static final String KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT";

    private static final String KEY_DRAWER_TAB_INDEX = "KEY_DRAWER_TAB_INDEX";

    @Inject OkHttpClient client;
    @Inject CookieManager manager;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private DrawerItem currentItem;
    private HistoryStack<DrawerItem> history;
    private boolean isPaused;

    private Menu menu;

    private TextView accountNameTextView;
    private TextView timeTextView;
    private View drawerItemInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TheTaleClientApplication
                .getComponentProvider()
                .getAppComponent()
                .inject(this);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // set up the drawer
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                findViewById(R.id.drawer_layout));

        accountNameTextView = findViewById(R.id.drawer_account_name);
        timeTextView = findViewById(R.id.drawer_time);
        drawerItemInfoView = findViewById(DrawerItem.PROFILE.getViewResId());

        history = new HistoryStack<>(DrawerItem.values().length);
        int tabIndex = DrawerItem.GAME.ordinal();
        if(savedInstanceState != null) {
            tabIndex = savedInstanceState.getInt(KEY_DRAWER_TAB_INDEX, tabIndex);
        }
        onNavigationDrawerItemSelected(DrawerItem.values()[tabIndex]);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(PreferencesManager.isReadAloudConfirmed()) {
            TextToSpeechUtils.init(TheTaleClientApplication.getContext(), null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;

        if(PreferencesManager.shouldExit()) {
            PreferencesManager.setShouldExit(false);
            finish();
        }

        final Intent intent = getIntent();
        int tabIndex = -1;
        if(intent != null) {
            if(intent.hasExtra(KEY_GAME_TAB_INDEX)) {
                onNavigationDrawerItemSelected(DrawerItem.GAME);
                tabIndex = intent.getIntExtra(KEY_GAME_TAB_INDEX, GameFragment.GamePage.GAME_INFO.ordinal());
                intent.removeExtra(KEY_GAME_TAB_INDEX);
            }

            if(intent.getBooleanExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT, false)) {
                PreferencesManager.setWatchingAccount(0, null);
                intent.removeExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT);
            }
        }

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
        if(tabIndex != -1) {
            final GameFragment.GamePage gamePage = GameFragment.GamePage.values()[tabIndex];
            if(fragment instanceof GameFragment) {
                ((GameFragment) fragment).setCurrentPage(gamePage);
            } else {
                PreferencesManager.setDesiredGamePage(gamePage);
            }
        }
        UiUtils.callOnscreenStateChange(fragment, true);

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, true);
    }

    @Override
    protected void onPause() {
        isPaused = true;

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, false);
        TextToSpeechUtils.pause();
        RequestCacheManager.invalidate();
        UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        isPaused = true;
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_DRAWER_TAB_INDEX, currentItem.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TextToSpeechUtils.destroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(DrawerItem item) {
        if(item != currentItem) {
            switch(item) {
                case PROFILE:
                    DialogUtils.showChoiceDialog(getSupportFragmentManager(), getString(R.string.drawer_title_site),
                            new String[]{
                                    getString(R.string.drawer_dialog_profile_item_keeper),
                                    getString(R.string.drawer_dialog_profile_item_hero)
                            },
                            position -> new InfoPrerequisiteRequest(client, manager, () -> {
                                final int accountId = PreferencesManager.getAccountId();
                                if(accountId == 0) {
                                    if(!isPaused()) {
                                        DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                    }
                                } else {
                                    switch(position) {
                                        case 0:
                                            startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_KEEPER, accountId)));
                                            break;

                                        case 1:
                                            startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_HERO, accountId)));
                                            break;

                                        default:
                                            if(!isPaused()) {
                                                DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                            }
                                            break;
                                    }
                                }
                            }, new PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                                @Override
                                public void processError(InfoResponse response) {
                                    if(!isPaused()) {
                                        DialogUtils.showCommonErrorDialog(getSupportFragmentManager(), MainActivity.this);
                                    }
                                }
                            }, null).execute());
                    break;

                case SITE:
                    startActivity(UiUtils.getOpenLinkIntent(WebsiteUtils.URL_GAME));
                    break;

                case LOGOUT:
                    PreferencesManager.setSession("");

                    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
                    if(fragment instanceof WrapperFragment) {
                        ((WrapperFragment) fragment).setMode(DataViewMode.LOADING);
                    }

                    new LogoutRequest(client, manager).execute(new ApiResponseCallback<CommonResponse>() {
                        @Override
                        public void processResponse(CommonResponse response) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void processError(CommonResponse response) {
                            if(fragment instanceof WrapperFragment) {
                                ((WrapperFragment) fragment).setError(response.errorMessage);
                            }
                        }
                    });
                    break;

                case ABOUT:
                    DialogUtils.showAboutDialog(getSupportFragmentManager());
                    break;

                default:
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment oldFragment = fragmentManager.findFragmentByTag(item.getFragmentTag());
                    if(oldFragment == null) {
                        oldFragment = item.getFragment();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, oldFragment, item.getFragmentTag())
                                .commit();
                    } else if(oldFragment.isDetached()) {
                        fragmentManager.beginTransaction()
                                .attach(oldFragment)
                                .commit();
                    }

                    if(currentItem != null) {
                        UiUtils.callOnscreenStateChange(getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag()), false);
                    }
                    UiUtils.callOnscreenStateChange(oldFragment, true);

                    currentItem = item;
                    mTitle = getString(currentItem.getTitleResId());
                    history.set(currentItem);
                    supportInvalidateOptionsMenu();

                    break;
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen() && (currentItem.getMenuResId() != 0)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            this.menu = menu;
            getMenuInflater().inflate(currentItem.getMenuResId(), menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void refresh() {
        onRefreshStarted();
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
        if(fragment instanceof Refreshable) {
            ((Refreshable) fragment).refresh(true);
        }
    }

    public void refreshGameAdjacentFragments() {
        if(currentItem == DrawerItem.GAME) {
            final Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentItem.getFragmentTag());
            if(fragment instanceof GameFragment) {
                ((GameFragment) fragment).refreshAdjacentFragments();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRefreshStarted() {
        if(menu != null) {
            final MenuItem itemRefresh = menu.findItem(R.id.action_refresh);
            if(itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, getLayoutInflater().inflate(R.layout.menu_progress, null));
            }
        }
    }

    public void onRefreshFinished() {
        if(menu != null) {
            final MenuItem itemRefresh = menu.findItem(R.id.action_refresh);
            if(itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, null);
            }
        }
    }

    public void onDataRefresh() {
        new InfoPrerequisiteRequest(client, manager, () -> {
            drawerItemInfoView.setVisibility(View.VISIBLE);
            UiUtils.setText(accountNameTextView, PreferencesManager.getAccountName());
            new GameInfoRequest(client, manager, false).execute(new ApiResponseCallback<GameInfoResponse>() {
                @Override
                public void processResponse(GameInfoResponse response) {
                    UiUtils.setText(timeTextView, String.format("%s %s", response.turnInfo.verboseDate, response.turnInfo.verboseTime));
                }

                @Override
                public void processError(GameInfoResponse response) {
                    UiUtils.setText(timeTextView, null);
                }
            }, true);
        }, new PrerequisiteRequest.ErrorCallback<InfoResponse>() {
            @Override
            public void processError(InfoResponse response) {
                drawerItemInfoView.setVisibility(View.GONE);
                UiUtils.setText(accountNameTextView, null);
                UiUtils.setText(timeTextView, null);
            }
        }, null).execute();
    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            final DrawerItem drawerItem = history.pop();
            if(drawerItem == null) {
                PreferencesManager.setShouldExit(true);
                finish();
            } else {
                onNavigationDrawerItemSelected(drawerItem);
            }
        }
    }

    public Menu getMenu() {
        return menu;
    }

    public boolean isPaused() {
        return isPaused;
    }

}
