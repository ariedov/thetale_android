package com.dleibovych.epictale.api.response;

import com.dleibovych.epictale.api.AbstractApiResponse;
import com.dleibovych.epictale.api.dictionary.Action;
import com.dleibovych.epictale.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hamster
 * @since 01.10.2014
 */
public class InfoResponse extends AbstractApiResponse
{

  public String dynamicContentUrl;
  public String staticContentUrl;
  public String gameVersion;
  public int turnDelta;
  public int accountId;
  public String accountName;
  public Map<Action, Integer> abilitiesCost;

  public InfoResponse(final String response) throws JSONException
  {
    super(response);
  }

  protected void parseData(final JSONObject data) throws JSONException
  {
    if (data.has("dynamic_content"))
    {
      dynamicContentUrl = data.getString("dynamic_content");
    }
    staticContentUrl = data.getString("static_content");
    gameVersion = data.getString("game_version");
    turnDelta = data.getInt("turn_delta");
    accountId = data.optInt("account_id", 0);
    accountName = ObjectUtils.getOptionalString(data, "account_name");

    abilitiesCost = new HashMap<>(Action.values().length);
    final JSONObject abilitiesJson = data.getJSONObject("abilities_cost");
    for (final Action action : Action.values())
    {
      if (data.has(action.getCode())) {
        abilitiesCost.put(action, abilitiesJson.getInt(action.getCode()));
      } else {
        abilitiesCost.put(action, 0);
      }
    }
  }

}
