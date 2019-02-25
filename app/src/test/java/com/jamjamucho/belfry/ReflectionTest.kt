package com.jamjamucho.belfry

import org.junit.Test
import java.lang.reflect.InvocationTargetException

class ReflectionTest {

    class Foo {

        fun yahoo(arg: String) {
            println("yahoo(string)")
        }

        fun yahoo(arg: Int) {
            println("yahoo(int)")
        }

        fun yahoo(arg: Any) {
            println("yahoo(any)")
        }
    }

    @Test
    fun test() {
        try {
            val foo = Foo()
            val classs = Foo::class.java
            val yahoo1 = classs.getMethod("yahoo", String::class.java)
            val yahoo2 = classs.getMethod("yahoo", Int::class.java)
            val yahoo3 = classs.getMethod("yahoo", Any::class.java)

            yahoo1.invoke(foo, "string")
            yahoo2.invoke(foo, 0)
            yahoo3.invoke(foo, Any())

            yahoo1.invoke(foo, 0)

        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}