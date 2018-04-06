package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.dictionary.ThirdPartyAuthState;
import com.dleibovych.epictale.api.response.ThirdPartyAuthStateResponse;
import com.dleibovych.epictale.util.PreferencesManager;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 29.10.2014
 */
public class ThirdPartyAuthStateRequest extends AbstractApiRequest<ThirdPartyAuthStateResponse> {

    public ThirdPartyAuthStateRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.GET, "accounts/third-party/tokens/api/authorisation-state", "1.0", true);
    }

    public void execute(final ApiResponseCallback<ThirdPartyAuthStateResponse> callback) {
        execute(null, null, callback);
    }

    @Override
    protected ThirdPartyAuthStateResponse getResponse(final String response) throws JSONException {
        final ThirdPartyAuthStateResponse thirdPartyAuthStateResponse = new ThirdPartyAuthStateResponse(response);

        if(thirdPartyAuthStateResponse.authState == ThirdPartyAuthState.SUCCESS) {
            PreferencesManager.setAccountId(thirdPartyAuthStateResponse.accountId);
            PreferencesManager.setAccountName(thirdPartyAuthStateResponse.accountName);
        } else {
            PreferencesManager.setAccountId(0);
            PreferencesManager.setAccountName(null);
        }

        return thirdPartyAuthStateResponse;
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
