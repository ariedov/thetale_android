package com.dleibovych.epictale.game.data

import org.thetale.api.models.Turn

private const val TURNS_TO_SAVE = 3

class GameTurnsCache {

    private val turns = mutableListOf<Turn>()

    fun saveTurn(turn: Turn) {
        appendTurn(turn)
        cleanupOverhead()
    }

    private fun appendTurn(turn: Turn) {
        if (!turns.contains(turn)) {
            turns.add(turn)
        }
    }

    private fun cleanupOverhead() {
        if (turns.size > TURNS_TO_SAVE) {
            turns.removeAt(0)
        }
    }

    fun concatIds(): String {
        return turns.fold(StringBuilder(), { builder, turn ->
            builder.append(turn.number)
        }).toString()
    }
}