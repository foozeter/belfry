package com.jamjamucho.pumpkin.transition

import android.view.View

class ChangeTranslationY: ChangeViewProperty() {

    override val propertyName = "translationY"

    override fun onCaptureValue(view: View) = view.translationY
}