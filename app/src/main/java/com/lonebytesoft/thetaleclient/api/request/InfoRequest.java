package com.lonebytesoft.thetaleclient.api.request;

import com.lonebytesoft.thetaleclient.api.AbstractApiRequest;
import com.lonebytesoft.thetaleclient.api.ApiResponseCallback;
import com.lonebytesoft.thetaleclient.api.HttpMethod;
import com.lonebytesoft.thetaleclient.api.response.InfoResponse;

import org.json.JSONException;

/**
 * @author Hamster
 * @since 30.09.2014
 */
public class InfoRequest extends AbstractApiRequest<InfoResponse> {

    public InfoRequest() {
        super(HttpMethod.GET, "api/info", "1.0", true);
    }

    public void execute(final ApiResponseCallback<InfoResponse> callback) {
        execute(null, null, callback);
    }

    protected InfoResponse getResponse(final String response) throws JSONException {
        return new InfoResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 5000;
    }

}