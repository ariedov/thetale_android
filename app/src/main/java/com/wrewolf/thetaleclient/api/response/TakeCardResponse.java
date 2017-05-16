package com.wrewolf.thetaleclient.api.response;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.model.CardInfo;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 29.03.2015
 */
public class TakeCardResponse extends AbstractApiResponse {

    public String message;
    public CardInfo card;

    public TakeCardResponse(final String response) throws JSONException {
        super(response);
    }

    @Override
    protected void parseData(final JSONObject data) throws JSONException {
        message = data.optString("message");
        card = ObjectUtils.getModelFromJson(CardInfo.class, data.getJSONObject("card"));
    }

}
