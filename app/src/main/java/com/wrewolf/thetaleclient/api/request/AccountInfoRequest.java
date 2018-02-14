package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.response.AccountInfoResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 20.02.2015
 */
public class AccountInfoRequest extends AbstractApiRequest<AccountInfoResponse> {

    public AccountInfoRequest(OkHttpClient client, CookieManager manager, final int accountId) {
        super(client, manager, HttpMethod.GET, String.format("accounts/%d/api/show", accountId), "1.0", true);
    }

    public void execute(final ApiResponseCallback<AccountInfoResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected AccountInfoResponse getResponse(String response) throws JSONException {
        return new AccountInfoResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 7200; // 2 hours
    }

}
