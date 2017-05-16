package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.cache.RequestCacheManager;
import com.wrewolf.thetaleclient.api.response.CommonResponse;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class LogoutRequest extends AbstractApiRequest<CommonResponse> {

    public LogoutRequest() {
        super(HttpMethod.POST, "accounts/auth/api/logout", "1.0", true);
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
