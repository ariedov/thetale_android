package com.dleibovych.epictale.api.cache.prerequisite;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.PreferencesManager;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 01.01.2015
 */
public class GameInfoPrerequisiteRequest extends PrerequisiteRequest<GameInfoResponse> {

    private final OkHttpClient client;
    private final CookieManager manager;

    public GameInfoPrerequisiteRequest(OkHttpClient httpClient, CookieManager manager, Runnable task, ErrorCallback<GameInfoResponse> errorCallback, Fragment fragment) {
        super(task, errorCallback, fragment);
        this.client = httpClient;
        this.manager = manager;
    }

    @Override
    protected boolean isPreExecuted() {
        return (PreferencesManager.getAccountId() != 0) && (!TextUtils.isEmpty(PreferencesManager.getMapVersion()));
    }

    @Override
    protected void preExecuteAndRun() {
        new GameInfoRequest(client, manager, false).execute(getApiCallback(), false);
    }

}
