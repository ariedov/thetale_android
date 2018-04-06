package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.PlacesResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 04.05.2015
 */
public class PlacesRequest extends AbstractApiRequest<PlacesResponse> {

    public PlacesRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.GET, "game/map/places/api/list", "1.0", true);
    }

    public void execute(final ApiResponseCallback<PlacesResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected PlacesResponse getResponse(String response) throws JSONException {
        return new PlacesResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 2 * 60 * 60 * 1000; // 2 hours
    }

}
