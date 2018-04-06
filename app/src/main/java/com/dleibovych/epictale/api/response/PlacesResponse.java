package com.dleibovych.epictale.api.response;

import com.dleibovych.epictale.api.AbstractApiResponse;
import com.dleibovych.epictale.api.model.PlaceInfo;
import com.dleibovych.epictale.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Hamster
 * @since 04.05.2015
 */
public class PlacesResponse extends AbstractApiResponse {

    public List<PlaceInfo> places;

    public PlacesResponse(final String response) throws JSONException {
        super(response);
    }

    @Override
    protected void parseData(JSONObject data) throws JSONException {
        final JSONObject placesJson = data.getJSONObject("places");
        places = new ArrayList<>(placesJson.length());
        for(final Iterator<String> placesJsonIterator = placesJson.keys(); placesJsonIterator.hasNext();) {
            final String key = placesJsonIterator.next();
            places.add(ObjectUtils.getModelFromJson(PlaceInfo.class, placesJson.getJSONObject(key)));
        }
    }

}
