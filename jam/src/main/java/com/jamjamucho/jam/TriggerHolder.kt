package com.jamjamucho.jam

import android.view.View
import com.jamjamucho.jam.inline.Trigger
import com.jamjamucho.jam.internal.StateHolder
import com.jamjamucho.jam.internal.TriggerEvent
import java.util.*

internal class TriggerHolder(
    src: Trigger,
    private val jam: Jam,
    private val sharedState: StateHolder?) {
    private val triggerId = src.trigger
    private val animationName = src.animationName
    private val necessaryStateCondition = src.necessaryStateCondition
    private val nextState = src.nextState
    private val triggerEvents = EnumSet
        .noneOf(TriggerEvent::class.java)
        .apply {
            if (src.triggeredByClick) add(TriggerEvent.ON_CLICK)
            if (src.triggeredByLongClick) add(TriggerEvent.ON_LONG_CLICK)
        }

    fun fire(trigger: View, event: TriggerEvent) {
        if (checkTriggerCondition(trigger) &&
            checkEventCondition(event) &&
            checkStateCondition() &&
            animationName != null) {
            jam.findAnimations(animationName).forEach { it.start() }
        }
    }

    private fun checkTriggerCondition(trigger: View)
            = trigger.id == triggerId

    private fun checkEventCondition(event: TriggerEvent)
            = triggerEvents.contains(event)

    private fun checkStateCondition()
            = necessaryStateCondition == null ||
            sharedState == null ||
            sharedState.isSameAs(necessaryStateCondition)
}