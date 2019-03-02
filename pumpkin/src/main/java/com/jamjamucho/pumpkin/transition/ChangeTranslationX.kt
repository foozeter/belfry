package com.jamjamucho.pumpkin.transition

import android.view.View

class ChangeTranslationX: ChangeViewProperty() {

    override val propertyName = "translationX"

    override fun onCaptureValue(view: View) = view.translationX
}