package com.jamjamucho.smile

internal class IdMapper<T> {

    companion object {
        private const val ID_INIT = Long.MIN_VALUE
    }

    private var nextId = ID_INIT
    private val map = mutableMapOf<T, Long>()

    fun map(t: T): Long {
        var id = map[t]
        if (id == null) {
            id = nextId
            map[t] = id
            ++nextId
        }
        return id
    }
}
