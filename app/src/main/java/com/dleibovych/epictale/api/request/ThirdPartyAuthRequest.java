package com.dleibovych.epictale.api.request;

import android.os.Build;

import com.dleibovych.epictale.BuildConfig;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.ThirdPartyAuthResponse;

import org.json.JSONException;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 29.10.2014
 */
public class ThirdPartyAuthRequest extends AbstractApiRequest<ThirdPartyAuthResponse> {

    public ThirdPartyAuthRequest(OkHttpClient client, CookieManager manager) {
        super(client, manager, HttpMethod.POST, "accounts/third-party/tokens/api/request-authorisation", "1.0", true);
    }

    public void execute(final ApiResponseCallback<ThirdPartyAuthResponse> callback) {
        final Map<String, String> postParams = new HashMap<>(3);
        postParams.put("application_name", TheTaleApplication.getContext().getString(R.string.app_name));
        postParams.put("application_info", String.format("%s %s, %s %s (%d)", Build.BRAND, Build.MODEL,
                TheTaleApplication.getContext().getPackageName(), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        postParams.put("application_description", TheTaleApplication.getContext().getString(R.string.app_description));

        execute(null, postParams, callback);
    }

    @Override
    protected ThirdPartyAuthResponse getResponse(final String response) throws JSONException {
        return new ThirdPartyAuthResponse(response);
    }

    @Override
    protected long getStaleTime() {
        return 0;
    }

}
