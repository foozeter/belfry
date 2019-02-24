package com.jamjamucho.smile

import org.junit.Assert.assertEquals
import org.junit.Test

class IdMapperTest {

    @Test
    fun map() {
        val mapper = IdMapper<String>()
        val id1 = mapper.map("dog")
        val id2 = mapper.map("cat")
        val id3 = mapper.map("human")
        val id4 = mapper.map("zebra")
        val id5 = mapper.map("cat")
        val id6 = mapper.map("zebra")
        printId(id1, "dog")
        printId(id2, "cat")
        printId(id3, "human")
        printId(id4, "zebra")
        printId(id5, "cat")
        printId(id6, "zebra")
        assertEquals(false, id1 == id2)
        assertEquals(false, id2 == id3)
        assertEquals(false, id3 == id4)
        assertEquals(true, id4 == id6)
        assertEquals(true, id2 == id5)
    }

    private fun printId(id: Long, string: String) {
        println("$string.id = $id")
    }
}