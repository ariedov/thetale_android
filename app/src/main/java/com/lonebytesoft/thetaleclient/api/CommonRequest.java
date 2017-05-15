package com.lonebytesoft.thetaleclient.api;

import android.os.AsyncTask;

import com.lonebytesoft.thetaleclient.api.cache.Request;
import com.lonebytesoft.thetaleclient.api.cache.RequestCacheManager;
import com.lonebytesoft.thetaleclient.util.RequestUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author Hamster
 * @since 07.10.2014
 */
public abstract class CommonRequest
{

  public void execute(final String url, final HttpMethod httpMethod,
                      final Map<String, String> getParams, final Map<String, String> postParams,
                      final CommonResponseCallback<String, Throwable> callback)
  {
    final Request request = httpMethod.getHttpRequest(url, getParams, postParams);

    final long staleTime = getStaleTime();
    if ((staleTime > 0) && !RequestCacheManager.initRequest(request, staleTime))
    {
      RequestCacheManager.addListener(request, new CommonResponseCallback<String, Void>()
      {
        @Override
        public void processResponse(String response)
        {
          RequestUtils.processResultInMainThread(callback, false, response, null);
        }

        @Override
        public void processError(Void error)
        {
          CommonRequest.this.execute(url, httpMethod, getParams, postParams, callback);
        }
      }, staleTime);
      return;
    }

    OkHttpClient client = new OkHttpClient();
    client.newCall(request.getRequest()).enqueue(new Callback()
    {
      @Override
      public void onFailure(Call call, IOException e)
      {
        if (staleTime > 0)
        {
          RequestCacheManager.onRequestFinishError(request);
        }
        RequestUtils.processResultInMainThread(callback, true, null, e);
      }


      @Override
      public void onResponse(Call call, Response response) throws IOException
      {
        String result = response.body().string();
        if (staleTime > 0)
        {
          RequestCacheManager.onRequestFinished(request, result);
        }
        RequestUtils.processResultInMainThread(callback, false, result, null);
      }

      {
        RequestUtils.processResultInMainThread(callback, true, null, null);
      }

    });


  }

  protected abstract long getStaleTime();

}
