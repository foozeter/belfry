package com.jamjamucho.jam.helper

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 * Original source code is here -> https://gist.github.com/cyrilmottier/8179328
 *
 * Usage: val states = StateListLoader().obtain(context, R.xml.your_xml_file)
 * A resource xml file must be in res/xml directory.
 */
class StateListLoader {

    companion object {
        private val TAG_EXTRAS = "extras"
    }

    @Throws(Resources.NotFoundException::class)
    fun obtain(context: Context, resId: Int)
            = obtain(context, TAG_EXTRAS, resId)

    @Throws(Resources.NotFoundException::class)
    fun obtain(context: Context, rootTag: String, resId: Int): Map<String, String> {
        val res = context.resources
        val parser = res.getXml(resId)
        try {
            var type = parser.next()
            while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) type = parser.next()
            if (type != XmlPullParser.START_TAG) throw XmlPullParserException("No start tag found")
            if (parser.name != rootTag) throw XmlPullParserException("Unknown start tag. Should be '$rootTag'")
            val extras = Bundle()
            res.parseBundleExtras(parser, extras)
            return extras.toMap()

        } catch (e: Exception) {
            val nfe = Resources.NotFoundException()
            nfe.initCause(e)
            throw nfe
        }
    }

    private fun Bundle.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        keySet().forEach {
            val value = getString(it)
            if (value != null) map[it] = value
        }
        return map
    }
}