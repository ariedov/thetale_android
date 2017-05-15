package com.lonebytesoft.thetaleclient.api;

import android.net.Uri;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Hamster
 * @since 30.09.2014
 */
public enum HttpMethod
{

  GET
      {
        public com.lonebytesoft.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams)
        {
          return new com.lonebytesoft.thetaleclient.api.cache.Request(url, new Request.Builder().url(appendGetParams(url, getParams)).build(),
                                                                      getParams,
                                                                      postParams);
        }
      },
  POST
      {
        public com.lonebytesoft.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams)
        {
          Request.Builder httpPostBuilder = new Request.Builder().url(appendGetParams(url, getParams));
          if (postParams == null)
          {
            return new com.lonebytesoft.thetaleclient.api.cache.Request(url, httpPostBuilder.build(), getParams, postParams);
          }

          FormBody.Builder formBodyBuilder = new FormBody.Builder();

          for (final Map.Entry<String, String> postParam : postParams.entrySet())
          {
            formBodyBuilder.add(postParam.getKey(), postParam.getValue());
          }
          return new com.lonebytesoft.thetaleclient.api.cache.Request(url, httpPostBuilder.post(formBodyBuilder.build()).build(), getParams, postParams);
        }
      };

  public abstract com.lonebytesoft.thetaleclient.api.cache.Request getHttpRequest(final String url, final Map<String, String> getParams, final Map<String, String> postParams);

  private static String appendGetParams(final String url, final Map<String, String> getParams)
  {
    if (getParams == null)
    {
      return url;
    }

    final Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
    for (final Map.Entry<String, String> getParam : getParams.entrySet())
    {
      uriBuilder.appendQueryParameter(getParam.getKey(), getParam.getValue());
    }
    return uriBuilder.build().toString();
  }

}
