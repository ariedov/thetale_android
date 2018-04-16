package com.dleibovych.epictale.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dleibovych.epictale.DataViewMode;
import com.dleibovych.epictale.R;
import com.dleibovych.epictale.TheTaleApplication;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.model.DiaryEntry;
import com.dleibovych.epictale.api.request.GameInfoRequest;
import com.dleibovych.epictale.api.response.GameInfoResponse;
import com.dleibovych.epictale.util.PreferencesManager;
import com.dleibovych.epictale.util.RequestUtils;
import com.dleibovych.epictale.util.UiUtils;

import java.net.CookieManager;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * @author Hamster
 * @since 06.10.2014
 */
public class DiaryFragment extends WrapperFragment {

    @Inject OkHttpClient client;
    @Inject CookieManager manager;

    private LayoutInflater layoutInflater;

    private View rootView;

    private ViewGroup diaryContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TheTaleApplication
                .getComponentProvider()
                .getAppComponent()
                .inject(this);


        layoutInflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        diaryContainer = rootView.findViewById(R.id.diary_container);

        return wrapView(layoutInflater, rootView);
    }

    @Override
    public void refresh(final boolean isGlobal) {
        super.refresh(isGlobal);

        final ApiResponseCallback<GameInfoResponse> callback = RequestUtils.wrapCallback(new ApiResponseCallback<GameInfoResponse>() {
            @Override
            public void processResponse(GameInfoResponse response) {
                diaryContainer.removeAllViews();
                for(int i = response.account.hero.diary.size() - 1; i >= 0; i--) {
                    final DiaryEntry diaryEntry = response.account.hero.diary.get(i);
                    final View diaryEntryView = layoutInflater.inflate(R.layout.item_diary, diaryContainer, false);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_place),
                            diaryEntry.place);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_time),
                            String.format("%s %s", diaryEntry.time, diaryEntry.date));
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_text),
                            diaryEntry.text);
                    diaryContainer.addView(diaryEntryView);
                }
                setMode(DataViewMode.DATA);
            }

            @Override
            public void processError(GameInfoResponse response) {
                setError(response.errorMessage);
            }
        }, this);

        final int watchingAccountId = PreferencesManager.getWatchingAccountId();
        if(watchingAccountId == 0) {
            new GameInfoRequest(client, manager, true).execute(callback, true);
        } else {
            new GameInfoRequest(client, manager, true).execute(watchingAccountId, callback, true);
        }
    }

}
