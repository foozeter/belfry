package com.jamjamucho.belfry

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    abstract class Super {

        fun echoName() {
            val name = this.javaClass.name
            println("My name is $name")
        }
    }

    class Derived1: Super()
    class Derived2: Super()

    @Test
    fun test() {
        Derived1().echoName()
        Derived2().echoName()
    }

    @Test
    fun castTest() {
        val foo = "foo"
        val intFoo = foo as? Int
        if (intFoo == null) {
            println("cast failed")
        } else println("cast successful")
        val strFoo = foo as? String
        if (strFoo == null) {
            println("cast failed")
        } else println("cast successful")
    }

    @Test
    fun compactTest() {
        val values = mutableListOf(0, 1, 2, -3, 4, 2, 1, 1, 0, -1, -5)

        println("before -> $values")

        values.compact { a, b ->
            if (a * b > 0 || (a == 0 && b == 0)) a + b
            else null
        }

        println("after -> $values")
    }

    private fun <T> MutableList<T>.compact(merge: (a: T, b: T) -> T?) {
        var start = size - 1
        while (0 < start) {
            var index = start - 1
            while (0 <= index) {
                val merged = merge(get(start), get(index))
                if (merged != null) {
                    set(start, merged)
                    removeAt(index)
                    --start
                }
                --index
            }
            --start
        }
    }
}
