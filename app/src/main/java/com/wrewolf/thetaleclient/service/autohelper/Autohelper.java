package com.wrewolf.thetaleclient.service.autohelper;

import com.wrewolf.thetaleclient.api.response.GameInfoResponse;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public interface Autohelper {
    boolean shouldHelp(GameInfoResponse gameInfoResponse);
}
