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
}
