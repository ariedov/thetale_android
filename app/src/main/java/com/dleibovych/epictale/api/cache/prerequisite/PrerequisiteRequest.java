package com.dleibovych.epictale.api.cache.prerequisite;

import androidx.fragment.app.Fragment;

import com.dleibovych.epictale.api.AbstractApiResponse;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.util.RequestUtils;

/**
 * @author Hamster
 * @since 01.01.2015
 */
public abstract class PrerequisiteRequest<T extends AbstractApiResponse> {

    private final Runnable task;
    private final ErrorCallback<T> errorCallback;
    private final Fragment fragment;

    public PrerequisiteRequest(final Runnable task, final ErrorCallback<T> errorCallback, final Fragment fragment) {
        this.task = task;
        this.errorCallback = errorCallback;
        this.fragment = fragment;
    }

    protected abstract boolean isPreExecuted();

    protected abstract void preExecuteAndRun();

    public void execute() {
        if(isPreExecuted()) {
            task.run();
        } else {
            preExecuteAndRun();
        }
    }

    protected ApiResponseCallback<T> getApiCallback() {
        final ApiResponseCallback<T> apiCallback = new ApiResponseCallback<T>() {
            @Override
            public void processResponse(T response) {
                if(isPreExecuted()) {
                    task.run();
                } else {
                    callErrorCallback(response);
                }
            }

            @Override
            public void processError(T response) {
                callErrorCallback(response);
            }
        };

        if(fragment == null) {
            return apiCallback;
        } else {
            return RequestUtils.wrapCallback(apiCallback, fragment);
        }
    }

    private void callErrorCallback(final T response) {
        if(errorCallback != null) {
            errorCallback.processError(response);
        }
    }

    public static abstract class ErrorCallback<T extends AbstractApiResponse> implements ApiResponseCallback<T> {

        @Override
        public final void processResponse(final T response) {
        }

        @Override
        public abstract void processError(final T response);

    }

}
