package com.jamjamucho.smile.parse

data class AnimationsBean(
    val id: Int,
    val allowInterruption: Boolean,
    val reverseAtInterruption: Boolean,
    val refs: List<RefBean>)