package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.AccountInfoResponse;

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
