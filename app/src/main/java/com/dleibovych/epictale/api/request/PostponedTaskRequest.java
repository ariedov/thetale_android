package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.CommonResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 03.10.2014
 */
public class PostponedTaskRequest extends AbstractApiRequest<CommonResponse> {

    public PostponedTaskRequest(OkHttpClient client, CookieManager manager, final String url) {
        super(client, manager, HttpMethod.GET, url, "", false);
    }

    public void execute(final ApiResponseCallback<CommonResponse> callback) {
        execute(null, null, callback);
    }

    protected CommonResponse getResponse(final String response) throws JSONException {
        return new CommonResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
