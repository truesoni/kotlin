// WITH_STDLIB

// DISABLE_IR_VISIBILITY_CHECKS: NATIVE, WASM
// ^ UninitializedPropertyAccessException is internal on Native and Wasm

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

import kotlin.UninitializedPropertyAccessException

fun runNoInline(f: () -> Unit) = f()

fun box(): String {
    lateinit var str: String
    var i: Int = 0
    try {
        runNoInline {
            i = str.length
        }
        return "Should throw an exception"
    }
    catch (e: UninitializedPropertyAccessException) {
        return "OK"
    }
    catch (e: Throwable) {
        return "Unexpected exception: ${e::class}"
    }
}
