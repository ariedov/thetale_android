package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import org.thetale.api.enumerations.CardTargetType;
import com.dleibovych.epictale.api.response.CommonResponse;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 05.05.2015
 */
public class UseCardRequest extends AbstractApiRequest<CommonResponse> {

    public UseCardRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.POST, "game/cards/api/use", "1.0", true);
    }

    public void execute(final int cardId, final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("card", String.valueOf(cardId));
        execute(getParams, null, callback);
    }

    public void execute(final int cardId, final CardTargetType targetType, final int targetId,
                        final ApiResponseCallback<CommonResponse> callback) {
        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("card", String.valueOf(cardId));

        final Map<String, String> postParams = new HashMap<>();
        switch(targetType) {
            case PERSON:
                postParams.put("person", String.valueOf(targetId));
                break;

            case PLACE:
                postParams.put("place", String.valueOf(targetId));
                break;

            case BUILDING:
                postParams.put("building", String.valueOf(targetId));
                break;
        }

        execute(getParams, postParams, callback);
    }

    @Override
    protected CommonResponse getResponse(String response) throws JSONException {
        return null;
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
