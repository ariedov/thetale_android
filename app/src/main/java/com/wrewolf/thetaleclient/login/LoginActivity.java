package com.wrewolf.thetaleclient.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.wrewolf.thetaleclient.BuildConfig;
import com.wrewolf.thetaleclient.DataViewMode;
import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.TheTaleClientApplication;
import com.wrewolf.thetaleclient.activity.MainActivity;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.ApiResponseStatus;
import com.wrewolf.thetaleclient.api.request.AuthRequest;
import com.wrewolf.thetaleclient.api.request.ThirdPartyAuthRequest;
import com.wrewolf.thetaleclient.api.request.ThirdPartyAuthStateRequest;
import com.wrewolf.thetaleclient.api.response.AuthResponse;
import com.wrewolf.thetaleclient.api.response.ThirdPartyAuthResponse;
import com.wrewolf.thetaleclient.api.response.ThirdPartyAuthStateResponse;
import com.wrewolf.thetaleclient.login.views.LoginContentStartView;
import com.wrewolf.thetaleclient.login.views.LoginPasswordView;
import com.wrewolf.thetaleclient.service.WatcherService;
import com.wrewolf.thetaleclient.service.widget.AppWidgetHelper;
import com.wrewolf.thetaleclient.util.DialogUtils;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.RequestUtils;
import com.wrewolf.thetaleclient.util.UiUtils;

import java.net.CookieManager;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 05.10.2014
 */
public class LoginActivity extends FragmentActivity implements LoginView, LoginNavigation {

    private static final String URL_HOME = "http://the-tale.org/?action=the-tale-client";
    private static final String URL_REGISTRATION = "http://the-tale.org/accounts/registration/fast?action=the-tale-client";
    private static final String URL_PASSWORD_REMIND = "http://the-tale.org/accounts/profile/reset-password?action=the-tale-client";
    private static final long THIRD_PARTY_AUTH_STATE_TIMEOUT = 10000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject LoginPresenter presenter;
    @Inject OkHttpClient client;
    @Inject CookieManager cookieManager;

    private LoginContentStartView loginStart;
    private LoginPasswordView loginPassword;

    private View dataView;
    private View loadingView;
    private View errorView;

    private View actionRetry;
    private TextView textErrorGlobal;

    private boolean wasError = false;
    private boolean isStartLoginContainerVisible = true;
    private boolean isThirdPartyAuthInProgress = false;

    private Runnable thirdPartyAuthStateRequester = new Runnable() {
        @Override
        public void run() {
            new ThirdPartyAuthStateRequest(client, cookieManager).execute(new ApiResponseCallback<ThirdPartyAuthStateResponse>() {
                @Override
                public void processResponse(ThirdPartyAuthStateResponse response) {
                    switch(response.authState) {
                        case NOT_REQUESTED:
                        case REJECT:
                            isThirdPartyAuthInProgress = false;
//                            UiUtils.setText(textError, response.authState.getDescription());
                            setLoginContainersVisibility(true);
                            setMode(DataViewMode.DATA);
                            break;

                        case SUCCESS:
                            isThirdPartyAuthInProgress = false;
                            onSuccessfulLogin();
                            break;

                        default:
                            isThirdPartyAuthInProgress = true;
                            handler.postDelayed(thirdPartyAuthStateRequester, THIRD_PARTY_AUTH_STATE_TIMEOUT);
                    }
                }

                @Override
                public void processError(ThirdPartyAuthStateResponse response) {
                    isThirdPartyAuthInProgress = true;
                    handler.postDelayed(thirdPartyAuthStateRequester, THIRD_PARTY_AUTH_STATE_TIMEOUT);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!BuildConfig.DEBUG) {
            // TODO: Вставить FireBase
            // Crashlytics.start(this);
        }

        TheTaleClientApplication.getComponentProvider()
                .getLoginComponent()
                .inject(this);
        presenter.setView(this);
        presenter.setNavigator(this);

        setContentView(R.layout.activity_login);

        loginStart = findViewById(R.id.login_content_start);
        loginPassword = findViewById(R.id.login_password);

        dataView = findViewById(R.id.login_content);
        loadingView = findViewById(R.id.login_progressbar);
        errorView = findViewById(R.id.login_error_global);

        actionRetry = findViewById(R.id.login_error_global_retry);
        textErrorGlobal = findViewById(R.id.login_error_global_text);

        findViewById(R.id.login_logo).setOnClickListener(v -> startActivity(UiUtils.getOpenLinkIntent(URL_HOME)));

        setMode(DataViewMode.DATA);
        setLoginContainersVisibility(true);

//        findViewById(R.id.login_action_authorization_site).setOnClickListener(v -> startRequestAuthSite());


//        findViewById(R.id.login_action_password_remind).setOnClickListener(v -> startActivity(UiUtils.getOpenLinkIntent(URL_PASSWORD_REMIND)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesManager.setShouldExit(false);
        if(isThirdPartyAuthInProgress) {
            thirdPartyAuthStateRequester.run();
        }
    }

    private void startRequestAuthSite() {
        setMode(DataViewMode.LOADING);
        new ThirdPartyAuthRequest(client, cookieManager).execute(new ApiResponseCallback<ThirdPartyAuthResponse>() {
            @Override
            public void processResponse(final ThirdPartyAuthResponse response) {
                // TODO check for isPaused if there will be crashes
                DialogUtils.showMessageDialog(
                        getSupportFragmentManager(),
                        getString(R.string.common_dialog_attention_title),
                        getString(R.string.login_dialog_confirm_authorization),
                        () -> {
                            isThirdPartyAuthInProgress = true;
                            thirdPartyAuthStateRequester.run();
                            startActivity(UiUtils.getOpenLinkIntent(RequestUtils.URL_BASE + response.nextUrl));
                        },
                        () -> {
                            setLoginContainersVisibility(true);
                            setMode(DataViewMode.DATA);
                        });
            }

            @Override
            public void processError(ThirdPartyAuthResponse response) {
                actionRetry.setOnClickListener(v -> startRequestAuthSite());
                setError(response.errorMessage);
            }
        });
    }

    private void sendAuthRequest(final String login, final String password) {
        new AuthRequest(client, cookieManager).execute(login, password, true,
                new ApiResponseCallback<AuthResponse>() {
                    @Override
                    public void processResponse(AuthResponse response) {
                        if (response.status == ApiResponseStatus.OK) {
                            onSuccessfulLogin();
                        } else {
                            setMode(DataViewMode.LOADING);
                        }
                    }

                    @Override
                    public void processError(AuthResponse response) {
                        LoginActivity.this.processError(response.errorMessage, login, password);
                        if (response.errorsLogin != null) {
//                            UiUtils.setText(textErrorLogin, TextUtils.join("\n", response.errorsLogin));
                        }
                        if (response.errorsPassword != null) {
//                            UiUtils.setText(textErrorPassword, TextUtils.join("\n", response.errorsPassword));
                        }
                    }
                });
    }

    private void setMode(final DataViewMode mode) {
        dataView.setVisibility(mode == DataViewMode.DATA ? View.VISIBLE : View.GONE);
        loadingView.setVisibility(mode == DataViewMode.LOADING ? View.VISIBLE : View.GONE);
        errorView.setVisibility(mode == DataViewMode.ERROR ? View.VISIBLE : View.GONE);
    }

    private void setLoginContainersVisibility(final boolean isStartContainer) {
        isStartLoginContainerVisible = isStartContainer;
        loginStart.setVisibility(isStartContainer ? View.VISIBLE : View.GONE);
        loginPassword.setVisibility(isStartContainer ? View.GONE : View.VISIBLE);
    }

    private void setError(final String error) {
        textErrorGlobal.setText(error);
        setMode(DataViewMode.ERROR);
        AppWidgetHelper.updateWithError(this, error);
    }

    private void processError(final String error, final String login, final String password) {
        wasError = true;
        setMode(DataViewMode.DATA);
//        UiUtils.setText(textError, error);
//        textLogin.setText(login);
//        textPassword.setText(password);
    }

    private void clearErrors() {
//        UiUtils.setText(textError, null);
//        UiUtils.setText(textErrorLogin, null);
//        UiUtils.setText(textErrorPassword, null);
    }

    private void onSuccessfulLogin() {
        startService(new Intent(this, WatcherService.class));
        startMainActivity();
    }

    private void startMainActivity() {
        AppWidgetHelper.updateWithRequest(this, client, cookieManager);

        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(getIntent());

        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(thirdPartyAuthStateRequester);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            TheTaleClientApplication
                    .getComponentProvider()
                    .setLoginComponent(null);
        }
    }

    @Override
    public void onBackPressed() {
        if(isThirdPartyAuthInProgress) {
            isThirdPartyAuthInProgress = false;
            handler.removeCallbacks(thirdPartyAuthStateRequester);
            setLoginContainersVisibility(true);
            setMode(DataViewMode.DATA);
        } else {
            if (isStartLoginContainerVisible) {
                PreferencesManager.setShouldExit(true);
                finish();
            } else {
                setLoginContainersVisibility(true);
            }
        }
    }

    @Override
    public void enableLogin() {
        setLoginContainersVisibility(true);
        setMode(DataViewMode.DATA);
    }

    @Override
    public void showInfoError() {
        setError("");
    }

    @Override
    public void showLoginError() {
        setMode(DataViewMode.DATA);
        setLoginContainersVisibility(false);
    }

    @Override
    public void setLoading() {
        setMode(DataViewMode.LOADING);
    }

    @Override
    public void startRegistration() {
        startActivity(UiUtils.getOpenLinkIntent(URL_REGISTRATION));
    }

    @Override
    public void proceedToGame() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(getIntent());

        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginMethodChooser() {
        setLoginContainersVisibility(true);
    }

    @Override
    public void showLoginEmail() {
        clearErrors();
        setLoginContainersVisibility(false);
    }
}
