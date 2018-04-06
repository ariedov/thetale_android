package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleClientApplication;
import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.ApiResponseStatus;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.RequestUtils;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class GameInfoRequest extends AbstractApiRequest<GameInfoResponse> {

    private final boolean needAuthorization;

    public GameInfoRequest(final OkHttpClient client, CookieManager cookieManager, final boolean needAuthorization) {
        super(client, cookieManager, HttpMethod.GET, "game/api/info", "1.3", true);
        this.needAuthorization = needAuthorization;
    }

    public void execute(final int accountId, final ApiResponseCallback<GameInfoResponse> callback,
                        final boolean useCache) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("account", String.valueOf(accountId));
        execute(getParams, null, callback, useCache);
    }

    public void execute(final ApiResponseCallback<GameInfoResponse> callback, final boolean useCache) {
        execute(null, null, callback, useCache);
    }

    protected GameInfoResponse getResponse(final String response) throws JSONException {
        final GameInfoResponse gameInfoResponse = new GameInfoResponse(response);

        if(gameInfoResponse.account == null) {
            PreferencesManager.setAccountId(0);
            PreferencesManager.setAccountName(null);
        } else if(gameInfoResponse.account.isOwnInfo) {
            PreferencesManager.setAccountId(gameInfoResponse.account.accountId);
        }
        PreferencesManager.setMapVersion(gameInfoResponse.mapVersion);

        if((gameInfoResponse.status == ApiResponseStatus.OK) && (gameInfoResponse.account == null) && needAuthorization) {
            return new GameInfoResponse(RequestUtils.getGenericErrorResponse(
                    TheTaleClientApplication.getContext().getString(R.string.game_not_authorized)));
        } else {
            return gameInfoResponse;
        }
    }

    @Override
    protected boolean isFinished(final GameInfoResponse response) {
        return super.isFinished(response) && ((response == null) ||
                ((response.account == null) || !response.account.isObsoleteInfo) &&
                ((response.enemy == null) || !response.enemy.isObsoleteInfo));
    }

    @Override
    protected long getStaleTime() {
        return 10000; // 10 seconds
    }

}
