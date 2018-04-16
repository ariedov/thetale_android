package org.thetale.core

class PresenterState(private var state: () -> Unit = {}) {

    private var isStarted = false

    fun start() {
        isStarted = true
        state.invoke()
    }

    fun stop() {
        isStarted = false
    }

    fun apply(state: () -> Unit = this.state) {
        this.state = state
        if (isStarted) {
            state.invoke()
        }
    }

    fun clear() {
        state = {}
    }
}