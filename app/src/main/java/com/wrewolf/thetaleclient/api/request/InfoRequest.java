package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.InfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 30.09.2014
 */
public class InfoRequest extends AbstractApiRequest<InfoResponse> {

    public InfoRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.GET, "api/info", "1.0", true);
    }

    public void execute(final ApiResponseCallback<InfoResponse> callback) {
        execute(null, null, callback);
    }

    protected InfoResponse getResponse(final String response) throws JSONException {
        final InfoResponse infoResponse = new InfoResponse(response);

        PreferencesManager.setAccountId(infoResponse.accountId);
        PreferencesManager.setAccountName(infoResponse.accountName);
        PreferencesManager.setTurnDelta(infoResponse.turnDelta);
        PreferencesManager.setStaticContentUrl(infoResponse.staticContentUrl);
        if(infoResponse.abilitiesCost != null) {
            PreferencesManager.setAbilitiesCost(infoResponse.abilitiesCost);
        }

        return infoResponse;
    }

    @Override
    protected long getStaleTime() {
        return 10000; // 10 seconds
    }

}
