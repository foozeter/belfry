package com.jamjamucho.smile.parse

data class AnimatorAssociatingBean(
    val states: List<StatesBean>,
    val triggers: List<TriggerBean>,
    val animations: List<AnimationsBean>,
    val bindings: List<BindingBean>)