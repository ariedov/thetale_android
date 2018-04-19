package com.dleibovych.epictale.api.model;

import org.thetale.api.enumerations.MapCellDangerLevel;
import org.thetale.api.enumerations.MapCellHumidity;
import org.thetale.api.enumerations.MapCellTemperature;
import org.thetale.api.enumerations.MapCellTerrain;
import org.thetale.api.enumerations.MapCellWindDirection;
import org.thetale.api.enumerations.MapCellWindSpeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamster
 * @since 15.10.2014
 */
public class MapCellTerrainInfo {

    public final List<String> text;
    public final MapCellTerrain terrain;
    public final boolean isWilderness;
    public final int neighborPlaceId;
    public final MapCellWindDirection windDirection;
    public final MapCellWindSpeed windSpeed;
    public final MapCellTemperature temperature;
    public final MapCellHumidity humidity;
    public final MapCellDangerLevel dangerLevel;

    public MapCellTerrainInfo(final JSONObject json) throws JSONException {
        final JSONArray textLines = json.getJSONArray("text");
        final int textLinesCount = textLines.length();
        text = new ArrayList<>(textLinesCount);
        for(int i = 0; i < textLinesCount; i++) {
            text.add(textLines.getString(i));
        }

        terrain = MapCellTerrain.values()[json.getInt("terrain")];
        isWilderness = json.getBoolean("is_wilderness");
        neighborPlaceId = isWilderness ? -1 : json.getInt("place");
        windDirection = MapCellWindDirection.values()[json.getInt("wind_direction")];
        windSpeed = MapCellWindSpeed.values()[json.getInt("wind_speed")];
        temperature = MapCellTemperature.values()[json.getInt("temperature")];
        humidity = MapCellHumidity.values()[json.getInt("humidity")];
        dangerLevel = MapCellDangerLevel.values()[json.getInt("danger_level")];
    }

}
