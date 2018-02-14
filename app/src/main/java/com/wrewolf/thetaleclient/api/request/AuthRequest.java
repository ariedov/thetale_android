package com.wrewolf.thetaleclient.api.request;

import com.wrewolf.thetaleclient.api.AbstractApiRequest;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.HttpMethod;
import com.wrewolf.thetaleclient.api.cache.RequestCacheManager;
import com.wrewolf.thetaleclient.api.response.AuthResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class AuthRequest extends AbstractApiRequest<AuthResponse> {

    public AuthRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.POST, "accounts/auth/api/login", "1.0", true);
    }

    public void execute(final String email, final String password, final boolean remember,
                        final ApiResponseCallback<AuthResponse> callback) {
        final Map<String, String> postParams = new HashMap<>(3);
        postParams.put("email", email);
        postParams.put("password", password);
        postParams.put("remember", String.valueOf(remember));
        execute(null, postParams, callback);
    }

    protected AuthResponse getResponse(final String response) throws JSONException {
        RequestCacheManager.invalidate();

        final AuthResponse authResponse = new AuthResponse(response);

        if((authResponse.errorsLogin == null) && (authResponse.errorsPassword == null)) {
            PreferencesManager.setAccountId(authResponse.accountId);
            PreferencesManager.setAccountName(authResponse.accountName);
        } else {
            PreferencesManager.setAccountId(0);
            PreferencesManager.setAccountName(null);
        }

        return authResponse;
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
