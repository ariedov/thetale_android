package com.wrewolf.thetaleclient.api.model;

import com.wrewolf.thetaleclient.api.dictionary.SocialLink;
import com.wrewolf.thetaleclient.util.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Hamster
 * @since 05.05.2015
 */
public class CouncilMemberConnectionInfo {

    public SocialLink type;
    public int councilMemberId;

    public CouncilMemberConnectionInfo(final JSONObject json) throws JSONException {
        type = ObjectUtils.getEnumForCode(SocialLink.class, json.getInt("social_link"));
        councilMemberId = json.getInt("council_member");
    }

}
