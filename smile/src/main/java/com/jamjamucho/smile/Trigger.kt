package com.jamjamucho.smile

import android.view.View
import com.jamjamucho.smile.parse.TriggerBean
import java.util.*

class Trigger(src: TriggerBean) {

    internal val animation = src.animations
    private val trigger = src.trigger
    private val stateName = src.stateName
    private val necessaryStateCondition = src.necessaryStateCondition
    private val nextState = src.nextState

    private val triggerEvents = EnumSet
        .noneOf(TriggerEvent::class.java)
        .apply {
            if (src.triggeredByClick) add(TriggerEvent.ON_CLICK)
            if (src.triggeredByLongClick) add(TriggerEvent.ON_LONG_CLICK)
        }

    fun fire(trigger: View, event: TriggerEvent, smile: Smile) {
        if (checkTriggerCondition(trigger) &&
            checkEventCondition(event) &&
            checkStateCondition(smile)) {
            smile.states[stateName].postFutureState(nextState, this)
            smile.animations[animation]?.start()
        }
    }

    private fun checkTriggerCondition(trigger: View)
            = trigger.id == this.trigger

    private fun checkEventCondition(event: TriggerEvent)
            = triggerEvents.contains(event)

    private fun checkStateCondition(smile: Smile)
            = smile.states[stateName].isSameAs(necessaryStateCondition)
}