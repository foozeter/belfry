package com.jamjamucho.jam.inline

import android.content.Context
import android.support.annotation.XmlRes
import android.util.AttributeSet
import com.jamjamucho.jam.R

class Config(
    context: Context, attributeSet: AttributeSet)
    : BaseInline(context, attributeSet) {

    @XmlRes
    val defaultStateList: Int?

    init {

        val attrs = context.obtainStyledAttributes(
            attributeSet, R.styleable.Config, 0, 0)

        val id = attrs.getResourceId(R.styleable.Config_jam_defaultStateList, 0)
        defaultStateList = if (id > 0) id else null

        attrs.recycle()
    }
}