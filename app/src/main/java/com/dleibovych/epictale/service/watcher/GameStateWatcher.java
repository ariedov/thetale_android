package com.dleibovych.epictale.service.watcher;

import com.dleibovych.epictale.api.response.GameInfoResponse;

/**
 * @author Hamster
 * @since 17.10.2014
 */
public interface GameStateWatcher {

    void processGameState(GameInfoResponse gameInfoResponse);

}
