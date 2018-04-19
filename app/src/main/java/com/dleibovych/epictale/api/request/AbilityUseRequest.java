package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import org.thetale.api.enumerations.Action;
import com.dleibovych.epictale.api.response.CommonResponse;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 03.10.2014
 */
public class AbilityUseRequest extends AbstractApiRequest<CommonResponse> {

    private final Action action;

    public AbilityUseRequest(OkHttpClient client, CookieManager manager, final Action action) {
        super(client, manager, HttpMethod.POST, String.format("game/abilities/%s/api/use", action.getCode()), "1.0", true);
        this.action = action;
    }

    public void execute(final int targetId, final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        switch(action) {
            case ARENA_ACCEPT:
                getParams.put("battle", String.valueOf(targetId));
                break;

            case BUILDING_REPAIR:
                getParams.put("building", String.valueOf(targetId));
                break;
        }
        execute(getParams, null, callback);
    }

    @Override
    protected CommonResponse getResponse(final String response) throws JSONException {
        return new CommonResponse(response);
    }

    @Override
    protected void retry(final Map<String, String> getParams, final Map<String, String> postParams,
                         final CommonResponse response, final ApiResponseCallback<CommonResponse> callback) {
        new PostponedTaskRequest(getHttpClient(), getCookieManager(), response.statusUrl).execute(callback);
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
