package com.jamjamucho.pumpkin.transition

import android.view.View

class ChangeX: ChangeViewProperty() {

    override val propertyName = "x"

    override fun onCaptureValue(view: View) = view.x
}