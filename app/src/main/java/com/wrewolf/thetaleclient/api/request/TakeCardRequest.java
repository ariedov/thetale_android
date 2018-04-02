package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.CommonResponse;
import com.wrewolf.thetaleclient.api.response.TakeCardResponse;
import com.wrewolf.thetaleclient.util.RequestUtils;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 10.10.2014
 */
public class TakeCardRequest extends AbstractApiRequest<TakeCardResponse> {

    public TakeCardRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.POST, "game/cards/api/call", "1.0", true);
    }

    public void execute(final ApiResponseCallback<TakeCardResponse> callback) {
        execute(null, null, callback);
    }

    protected TakeCardResponse getResponse(final String response) throws JSONException {
        return new TakeCardResponse(response);
    }

    @Override
    protected void retry(final Map<String, String> getParams, final Map<String, String> postParams,
                         final TakeCardResponse response, final ApiResponseCallback<TakeCardResponse> callback) {
        new PostponedTaskRequest(getHttpClient(), getCookieManager(), response.statusUrl).execute(new ApiResponseCallback<CommonResponse>() {
            @Override
            public void processResponse(CommonResponse response) {
                TakeCardResponse takeCardResponse;
                try {
                    takeCardResponse = new TakeCardResponse(response.rawResponse);
                    callback.processResponse(takeCardResponse);
                } catch (JSONException e) {
                    try {
                        takeCardResponse = new TakeCardResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        takeCardResponse = null;
                    }
                    callback.processError(takeCardResponse);
                }
            }

            @Override
            public void processError(CommonResponse response) {
                TakeCardResponse takeCardResponse;
                try {
                    takeCardResponse = new TakeCardResponse(response.rawResponse);
                } catch (JSONException e) {
                    try {
                        takeCardResponse = new TakeCardResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        takeCardResponse = null;
                    }
                }
                callback.processError(takeCardResponse);
            }
        });
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
