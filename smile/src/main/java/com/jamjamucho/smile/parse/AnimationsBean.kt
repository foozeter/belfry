package com.jamjamucho.smile.parse

data class AnimationsBean(
    val id: String,
    val allowInterruption: Boolean,
    val reverseAtInterruption: Boolean,
    val refs: List<RefBean>)