package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.PlaceResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 05.05.2015
 */
public class PlaceRequest extends AbstractApiRequest<PlaceResponse> {

    public PlaceRequest(OkHttpClient client, CookieManager manager, final int placeId) {
        super(client, manager, HttpMethod.GET, String.format("game/map/places/%d/api/show", placeId), "1.0", true);
    }

    public void execute(final ApiResponseCallback<PlaceResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected PlaceResponse getResponse(String response) throws JSONException {
        return new PlaceResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 2 * 60 * 60 * 1000; // 2 hours
    }

}
