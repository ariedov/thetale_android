package com.wrewolf.thetaleclient.api.cache.prerequisite;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wrewolf.thetaleclient.api.request.GameInfoRequest;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 01.01.2015
 */
public class GameInfoPrerequisiteRequest extends PrerequisiteRequest<GameInfoResponse> {

    public GameInfoPrerequisiteRequest(Runnable task, ErrorCallback<GameInfoResponse> errorCallback, Fragment fragment) {
        super(task, errorCallback, fragment);
    }

    @Override
    protected boolean isPreExecuted() {
        return (PreferencesManager.getAccountId() != 0) && (!TextUtils.isEmpty(PreferencesManager.getMapVersion()));
    }

    @Override
    protected void preExecuteAndRun() {
        new GameInfoRequest(false).execute(getApiCallback(), false);
    }

}
