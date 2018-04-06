package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.CombineCardsResponse;
import com.dleibovych.epictale.api.response.CommonResponse;
import com.dleibovych.epictale.util.RequestUtils;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 21.03.2015
 */
public class CombineCardsRequest extends AbstractApiRequest<CombineCardsResponse> {

    private final List<Integer> cardIds;

    public CombineCardsRequest(OkHttpClient client, CookieManager manager, final List<Integer> cardIds) {
        super(client, manager, HttpMethod.POST, "game/cards/api/combine", "1.0", true);
        this.cardIds = cardIds;
    }

    public void execute(final ApiResponseCallback<CombineCardsResponse> callback) {
        final StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(final int cardId : cardIds) {
            if(first) {
                first = false;
            } else {
                stringBuilder.append(",");
            }
            stringBuilder.append(cardId);
        }

        final Map<String, String> getParams = new HashMap<>(1);
        getParams.put("cards", stringBuilder.toString());
        execute(getParams, null, callback);
    }

    protected CombineCardsResponse getResponse(final String response) throws JSONException {
        return new CombineCardsResponse(response);
    }

    @Override
    protected void retry(final Map<String, String> getParams, final Map<String, String> postParams,
                         final CombineCardsResponse response, final ApiResponseCallback<CombineCardsResponse> callback) {
        new PostponedTaskRequest(getHttpClient(), getCookieManager(), response.statusUrl).execute(new ApiResponseCallback<CommonResponse>() {
            @Override
            public void processResponse(CommonResponse response) {
                CombineCardsResponse combineCardsResponse;
                try {
                    combineCardsResponse = new CombineCardsResponse(response.rawResponse);
                    callback.processResponse(combineCardsResponse);
                } catch (JSONException e) {
                    try {
                        combineCardsResponse = new CombineCardsResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        combineCardsResponse = null;
                    }
                    callback.processError(combineCardsResponse);
                }
            }

            @Override
            public void processError(CommonResponse response) {
                CombineCardsResponse combineCardsResponse;
                try {
                    combineCardsResponse = new CombineCardsResponse(response.rawResponse);
                } catch (JSONException e) {
                    try {
                        combineCardsResponse = new CombineCardsResponse(RequestUtils.getGenericErrorResponse(e.getMessage()));
                    } catch (JSONException ignored) {
                        combineCardsResponse = null;
                    }
                }
                callback.processError(combineCardsResponse);
            }
        });
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
