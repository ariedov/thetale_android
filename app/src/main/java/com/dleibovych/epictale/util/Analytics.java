package com.dleibovych.epictale.util;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.dleibovych.epictale.TheTaleApplication;

/**
 * Created by Алексей on 16.05.2017.
 * Отправка аналитики по фрагментам
 */

public class Analytics
{
  public static void sendFragment(String name)
  {
    Bundle bundle = new Bundle();
    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "navigation");
    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.format("nav_fragment_%s", name));
    TheTaleApplication.getAnalytics().logEvent("nav_fragment", bundle);
  }
}
