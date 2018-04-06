package com.dleibovych.epictale.service.autohelper;

import com.dleibovych.epictale.api.response.GameInfoResponse;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public interface Autohelper {
    boolean shouldHelp(GameInfoResponse gameInfoResponse);
}
