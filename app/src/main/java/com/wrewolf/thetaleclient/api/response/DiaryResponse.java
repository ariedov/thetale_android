package com.wrewolf.thetaleclient.api.response;

import android.util.Log;

import com.wrewolf.thetaleclient.api.AbstractApiResponse;
import com.wrewolf.thetaleclient.api.model.DiaryEntry;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 17.05.2017.
 * Обработка ответа с данными дневника
 */

public class DiaryResponse extends AbstractApiResponse
{

  private static final String TAG = "DiaryResponse";
  public List<DiaryEntry> diary;

  public DiaryResponse(final String response) throws JSONException {
    super(response);
    Log.d("DiaryResponse",response);
  }
  @Override
  protected void parseData(JSONObject data) throws JSONException
  {
    final JSONArray diaryJson = data.getJSONArray("messages");
    final int diaryEntriesCount = diaryJson.length();
    diary = new ArrayList<>(diaryEntriesCount);
    try
    {
      for (int i = 0; i < diaryEntriesCount; i++)
      {
        diary.add(ObjectUtils.getModelFromJson(DiaryEntry.class, diaryJson.getJSONObject(i)));
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
