//package com.dleibovych.epictale.util;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.dleibovych.epictale.R;
//
//import net.grandcentrix.tray.TrayPreferences;
//import net.grandcentrix.tray.core.TrayStorage;
//
///**
// * Created by Алексей on 16.05.2017.
// * Заглушка на параметры, не хотел переписывать весь оригинальный файл
// */
//
//class WreWolfPreferences extends TrayPreferences
//{
//  WreWolfPreferences(@NonNull Context context)
//  {
//    super(context, );
//  }
//
//  private static TrayPreferences _instance = null;
//
//  static TrayPreferences getDefaultSharedPreferences(Context context)
//  {
//    if (_instance == null)
//    {
//      _instance = new WreWolfPreferences(context);
//    }
//    return _instance;
//  }
//
//  void putBoolean(@NonNull String key, @NonNull Boolean value)
//  {
//    _instance.put(key, value);
//
//  }
//
//  public Boolean getBoolean(@NonNull String key, Boolean defaultValue)
//  {
//    return _instance.getBoolean(key, defaultValue);
//  }
//
//  void putInt(@NonNull String key, @NonNull Integer value)
//  {
//    _instance.put(key, value);
//  }
//
//  public Integer getInt(@NonNull String key, Integer defaultValue)
//  {
//    return _instance.getInt(key, defaultValue);
//  }
//
//  void putString(@NonNull String key, @NonNull String value)
//  {
//    _instance.put(key, value);
//  }
//
//  public String getString(@NonNull String key, String defaultValue)
//  {
//    return _instance.getString(key, defaultValue);
//
//  }
//}
