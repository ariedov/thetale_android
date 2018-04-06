package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.cache.RequestCacheManager;
import com.dleibovych.epictale.api.response.CommonResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class LogoutRequest extends AbstractApiRequest<CommonResponse> {

    public LogoutRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.POST, "accounts/auth/api/logout", "1.0", true);
    }

    public void execute(final ApiResponseCallback<CommonResponse> callback) {
        execute(null, null, callback);
    }

    protected CommonResponse getResponse(final String response) throws JSONException {
        RequestCacheManager.invalidate();
        return new CommonResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
