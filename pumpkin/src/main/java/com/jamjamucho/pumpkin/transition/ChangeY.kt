package com.jamjamucho.pumpkin.transition

import android.view.View

class ChangeY: ChangeViewProperty() {

    override val propertyName = "y"

    override fun onCaptureValue(view: View) = view.y
}