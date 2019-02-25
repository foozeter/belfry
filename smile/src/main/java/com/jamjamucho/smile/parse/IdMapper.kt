package com.jamjamucho.smile.parse

internal class IdMapper<T> {

    companion object {
        private const val ID_INIT = 0
    }

    private var nextId = ID_INIT
    private val map = mutableMapOf<T, Int>()

    fun map(t: T): Int {
        var id = map[t]
        if (id == null) {
            id = nextId
            map[t] = id
            ++nextId
        }
        return id
    }

    fun reset() {
        nextId = ID_INIT
        map.clear()
    }
}
