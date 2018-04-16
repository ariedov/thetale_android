package com.dleibovych.epictale

import kotlin.reflect.KProperty

class ComponentDelegate<T: Any>(private val init: () -> T): kotlin.properties.ReadWriteProperty<Any?, T?> {

    var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        if (value == null) {
            value = init()
        }
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        this.value = value
    }
}