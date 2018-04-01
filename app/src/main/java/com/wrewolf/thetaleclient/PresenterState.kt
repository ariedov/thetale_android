package com.wrewolf.thetaleclient

class PresenterState(private var state: () -> Unit = {}) {

    fun apply(state: () -> Unit = this.state) {
        this.state = state
        state.invoke()
    }

    fun clear() {
        state = {}
    }
}