package com.jamjamucho.pumpkin.transition

import android.view.View

class ChangeAlpha: ChangeViewProperty() {

    override val propertyName = "alpha"

    override fun onCaptureValue(view: View) = view.alpha
}