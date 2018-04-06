package com.dleibovych.epictale.api.model;

import com.dleibovych.epictale.api.dictionary.ChatMilestone;
import com.dleibovych.epictale.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 24.10.2014
 */
public class ChatMessage {

    public final int id;
    public final int time; // seconds
    public final String nickname;
    public final String message;
    public final ChatMilestone milestone;
    public final boolean isDeleted;

    public ChatMessage(final JSONObject json) throws JSONException {
        id = json.getInt("id");
        time = json.getInt("timestamp");
        nickname = json.getString("nickname");
        message = json.getString("body");
        milestone = ObjectUtils.getEnumForCode(ChatMilestone.class, json.getInt("closest_milestone"));
        isDeleted = json.getBoolean("deleted");
    }

}
