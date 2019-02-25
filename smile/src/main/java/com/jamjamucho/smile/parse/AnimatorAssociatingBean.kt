package com.jamjamucho.smile.parse

data class AnimatorAssociatingBean(
    val states: List<StatesBean>,
    val triggers: List<TriggerBean>,
    val animations: Map<Int, AnimationsBean>,
    val bindings: Map<Int, BindingBean>)