package com.jamjamucho.belfry.timer

class IDMap<T> {

    companion object {
        private const val ID_INIT = Long.MIN_VALUE
    }

    private var nextID = ID_INIT
    private val recycledIDs = mutableListOf<Long>()
    private val map = mutableMapOf<Long, T>()

    fun put(entity: T): Long {
        val id = obtainID()
        map[id] = entity
        return id
    }

    operator fun get(id: Long) = map[id]

    fun remove(id: Long): T? {
        val removed = map.remove(id)
        if (removed != null) {
            recycleID(id)
        }
        return removed
    }

    fun clear() {
        map.clear()
        recycledIDs.clear()
        nextID = ID_INIT
    }

    fun isEmpty() = map.isEmpty()

    fun isNotEmpty() = map.isNotEmpty()

    fun has(check: (entity: T) -> Boolean): Boolean {
        map.forEach { if (check(it.value)) return true }
        return false
    }

    private fun obtainID()
            = recycledIDs.pop() ?: ++nextID

    private fun recycleID(id: Long) {
        recycledIDs.add(id)
    }

    private fun <T> MutableList<T>.pop()
            = if (isNotEmpty()) removeAt(lastIndex) else null
}