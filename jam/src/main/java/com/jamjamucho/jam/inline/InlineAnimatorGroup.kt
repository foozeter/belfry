package com.jamjamucho.jam.inline

import android.content.Context
import android.util.AttributeSet

class InlineAnimatorGroup(
    context: Context, attributeSet: AttributeSet)
    : BaseInlineAnimator(context, attributeSet) {

//    fun obtainChildren() = mutableListOf<InlineAnimator>().apply {
//        for (i in 0 until childCount) {
//            val child = getChildAt(i)
//            if (child is InlineAnimator) {
//                overwriteChildAttributes(child)
//                add(child)
//            }
//        }
//    }
//
//    private fun overwriteChildAttributes(child: InlineAnimator) {
//        child.trigger = this.trigger
//        child.allowInterruption = this.allowInterruption
//        child.repeatAtInterruption = this.repeatAtInterruption
//        child.stateName = this.stateName
//        child.necessaryStateCondition = this.necessaryStateCondition
//        child.nextState = this.nextState
//        child.triggeredByClick = this.triggeredByClick
//        child.triggeredByLongClick = this.triggeredByLongClick
//    }
}