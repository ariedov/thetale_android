package com.dleibovych.epictale.api.request;

import com.dleibovych.epictale.api.AbstractApiRequest;
import com.dleibovych.epictale.api.ApiResponseCallback;
import com.dleibovych.epictale.api.HttpMethod;
import com.dleibovych.epictale.api.response.DiaryResponse;

import org.json.JSONException;

import java.net.CookieManager;

import okhttp3.OkHttpClient;

/**
 * Created by Алексей on 17.05.2017.
 * Запрос данных дневника
 */

public class DiaryRequest extends AbstractApiRequest<DiaryResponse>
{
  public DiaryRequest(OkHttpClient client, CookieManager manager)
  {
    super(client, manager, HttpMethod.GET, "game/api/diary", "1.0", true);
  }

  public void execute(final ApiResponseCallback<DiaryResponse> callback)
  {
    execute(null, null, callback);
  }

  protected DiaryResponse getResponse(final String response) throws JSONException
  {
    return new DiaryResponse(response);
  }


  @Override
  protected long getStaleTime()
  {
    return 1000; // 1 second
  }

}
