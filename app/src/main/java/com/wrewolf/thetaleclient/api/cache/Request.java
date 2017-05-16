package com.wrewolf.thetaleclient.api.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 08.10.2014
 */
public class Request
{

  private String url;
  private okhttp3.Request httpMethod;
  private Map<String, String> getParams;
  private Map<String, String> postParams;

  public Request(final String url, final okhttp3.Request httpMethod, final Map<String, String> getParams, final Map<String, String> postParams)
  {
    this.url = url;
    this.httpMethod = httpMethod;
    this.getParams = getParams == null ? null : new HashMap<>(getParams);
    this.postParams = postParams == null ? null : new HashMap<>(postParams);
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof Request))
    {
      return false;
    }

    final Request another = (Request) o;
    return another.url.equals(url)
        && another.httpMethod.equals(httpMethod)
        && ((another.getParams == null) && (getParams == null) || (another.getParams != null) && another.getParams.equals(getParams))
        && ((another.postParams == null) && (postParams == null) || (another.postParams != null) && another.postParams.equals(postParams));
  }

  @Override
  public int hashCode()
  {
    int result = url.hashCode();
    if (httpMethod.body() != null)
    {
      result = 31 * result + httpMethod.body().toString().hashCode();
    }
    else
    {
      result = 31 * result;
    }
    if (getParams != null)
    {
      result = 31 * result + getParams.hashCode();
    }
    if (postParams != null)
    {
      result = 31 * result + postParams.hashCode();
    }
    return result;
  }

  public okhttp3.Request getRequest()
  {
    return httpMethod;
  }
}
