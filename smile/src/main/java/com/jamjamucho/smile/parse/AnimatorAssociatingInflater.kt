package com.jamjamucho.smile.parse

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class AnimatorAssociatingInflater(
    private val context: Context) {

    // Convert a string id to a integer id for better performance.
    private val idm = IdMapper<String>()

    companion object {
        private const val ID = "id"
        private const val REF = "ref"
        private const val TRIGGER = "trigger"
        private const val TARGET = "target"
        private const val BINDING = "binding"
        private const val ANIMATIONS = "animations"
        private const val ANIMATOR = "animator"
        private const val ALLOW_INTERRUPTION = "allowInterruption"
        private const val REVERSE_AT_INTERRUPTION = "reverseAtInterruption"
        private const val STATE_NAME = "stateName"
        private const val NECESSARY_STATE_CONDITION = "necessaryStateCondition"
        private const val NEXT_STATE = "nextState"
        private const val NAME = "name"
        private const val DEFAULT = "default"
        private const val STATE = "state"
        private const val STATES = "states"
        private const val TRIGGERED_BY_CLICK = "triggeredByClick"
        private const val TRIGGERED_BY_LONG_CLICK = "triggeredByLongClick"
    }

    @Throws(Resources.NotFoundException::class)
    fun inflate(@XmlRes xml: Int): AnimatorAssociatingBean {
        try {
            idm.reset()
            val parser = context.resources.getXml(xml)
            parser.skipToFirst()
            return parser.inflateAnimatorAssociating()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * format:
     * <animatorAssociating>
     *     <states .../>
     *     <trigger .../>
     *     <animations .../>
     *     <binding .../>
     * </animatorAssociating>
     */
    private fun XmlResourceParser.inflateAnimatorAssociating(): AnimatorAssociatingBean {
        var states = mutableListOf<StatesBean>()
        val triggers = mutableListOf<TriggerBean>()
        val animations = mutableMapOf<Int, AnimationsBean>()
        val bindings = mutableMapOf<Int, BindingBean>()

        forEachChildNode { name ->
            when (name) {
                STATES -> states.add(inflateStates())
                TRIGGER -> triggers.add(inflateTrigger())
                ANIMATIONS -> animations.putWith(inflateAnimations()) { it.id }
                BINDING -> bindings.putWith(inflateBinding()) { it.id }
                else -> onUnknownChildFound(this, name)
            }
        }

        return AnimatorAssociatingBean(states, triggers, animations, bindings)
    }

    /**
     * format:
     * <states>
     *     <state .../>
     * </states>
     */
    private fun XmlResourceParser.inflateStates(): StatesBean {
        val states = mutableListOf<StateBean>()
        forEachChildNode { name ->
            when (name) {
                STATE -> states.add(inflateState())
                else -> onUnknownChildFound(this, name)
            }
        }

        return StatesBean(states)
    }

    /**
     * format:
     * <state
     *   name="string"
     *   default="string"/>
     */
    private fun XmlResourceParser.inflateState(): StateBean {
        var name: String? = null
        var default: String? = null
        forEachAttribute { index, attr ->
            when (attr) {
                NAME -> name = getAttributeValue(index)
                DEFAULT -> default = getAttributeValue(index)
                else -> onUnknownAttributeFound(this, attr)
            }
        }

        name ?: onAttributeNotFound(this, NAME)
        default ?: onAttributeNotFound(this, DEFAULT)

        return StateBean(idm.map(name!!), idm.map(default!!))
    }

    /**
     * format:
     * <trigger
     *   trigger="view-id"
     *   animations="string"
     *   stateName="string" (optional)
     *   necessaryStateCondition="string" (optional)
     *   nextState="string" (optional)
     *   triggeredByClick="boolean" (default:false)
     *   triggeredByLongClick="boolean" (default:false)
     *   />
     */
    private fun XmlResourceParser.inflateTrigger(): TriggerBean {
        var trigger: Int? = null
        var animations: String? = null
        var stateName: String? = null
        var necessaryStateCondition: String? = null
        var nextState: String? = null
        var triggeredByClick = false
        var triggeredByLongClick = false

        forEachAttribute { index, name ->
            when (name) {
                TRIGGER -> trigger = getAttributeResourceValue(index, 0)
                ANIMATIONS -> animations = getAttributeValue(index)
                STATE_NAME -> stateName = getAttributeValue(index)
                NECESSARY_STATE_CONDITION -> necessaryStateCondition = getAttributeValue(index)
                NEXT_STATE -> nextState = getAttributeValue(index)
                TRIGGERED_BY_CLICK -> triggeredByClick = getAttributeBooleanValue(index, false)
                TRIGGERED_BY_LONG_CLICK -> triggeredByLongClick = getAttributeBooleanValue(index, false)
                else -> onUnknownAttributeFound(this, name)
            }
        }

        trigger ?: onAttributeNotFound(this, TRIGGER)
        animations ?: onAttributeNotFound(this,
            ANIMATIONS
        )

        return TriggerBean(
            trigger!!,
            idm.map(animations!!),
            if (stateName != null) idm.map(stateName!!) else null,
            if (necessaryStateCondition != null) idm.map(necessaryStateCondition!!) else null,
            if (nextState != null) idm.map(nextState!!) else null,
            triggeredByClick,
            triggeredByLongClick)
    }

    /**
     * format:
     * <animations
     *   id="string"
     *   allowInterruption="boolean" (default:true)
     *   reverseAtInterruption="boolean" (default:false) >
     *     <ref .../>
     * </animations>
     */
    private fun XmlResourceParser.inflateAnimations(): AnimationsBean {
        var id: String? = null
        var allowInterruption = true
        var reverseAtInterruption = false
        forEachAttribute { index, name ->
            when (name) {
                ID -> id = getAttributeValue(index)
                ALLOW_INTERRUPTION -> allowInterruption = getAttributeBooleanValue(index, true)
                REVERSE_AT_INTERRUPTION -> reverseAtInterruption = getAttributeBooleanValue(index, false)
                else -> onUnknownAttributeFound(this, name)
            }
        }

        val refs = mutableListOf<RefBean>()
        forEachChildNode { name ->
            when (name) {
                REF -> refs.add(inflateRef())
                else -> onUnknownChildFound(this, name)
            }
        }

        id ?: onAttributeNotFound(this, ID)

        return AnimationsBean(
            idm.map(id!!), allowInterruption,
            reverseAtInterruption, refs)
    }

    /**
     * format:
     * <binding
     *   id="string"
     *   target="view-id"
     *   animator="animator-id"/>
     */
    private fun XmlResourceParser.inflateBinding(): BindingBean {
        var id: String? = null
        var target: Int? = null
        var animator: Int? = null
        forEachAttribute { index, name ->
            when (name) {
                ID -> id = getAttributeValue(index)
                TARGET -> target = getAttributeResourceValue(index, 0)
                ANIMATOR -> animator = getAttributeResourceValue(index, 0)
                else -> onUnknownAttributeFound(this, name)
            }
        }

        id ?: onAttributeNotFound(this, ID)
        target ?: onAttributeNotFound(this, TARGET)
        animator ?: onAttributeNotFound(this, ANIMATOR)

        return BindingBean(idm.map(id!!), target!!, animator!!)
    }

    /**
     * format:
     * <ref binding="string"/>
     */
    private fun XmlResourceParser.inflateRef(): RefBean {
        var binding: String? = null
        forEachAttribute { index, name ->
            when (name) {
                BINDING -> binding = getAttributeValue(index)
                else -> onUnknownAttributeFound(this, name)
            }
        }

        binding ?: onAttributeNotFound(this, BINDING)

        return RefBean(idm.map(binding!!))
    }

    private fun XmlResourceParser.forEachAttribute(
        doSomething: (index: Int, name: String) -> Unit) {
        for (i in 0 until attributeCount) {
            doSomething(i, getAttributeName(i))
        }
    }

    private fun XmlResourceParser.forEachChildNode(
        doSomething: (name: String) -> Unit) {
        val startDepth = depth
        do {
            next()
            if (eventType == XmlPullParser.START_DOCUMENT) next()
            if (eventType == XmlPullParser.START_TAG) doSomething(name)
        } while (startDepth < depth)
    }

    private fun XmlResourceParser.skipToFirst() {
        while (eventType != XmlPullParser.START_TAG
            && eventType != XmlPullParser.END_DOCUMENT) next()
    }

    private fun <T, U> MutableMap<T, U>.putWith(value: U, keySelector: (value: U) -> T) {
        put(keySelector(value), value)
    }

    @Throws(XmlPullParserException::class)
    private fun onAttributeNotFound(parser: XmlResourceParser, attrName: String) {
        throw XmlPullParserException("'$attrName' attribute was not found in '${parser.name}' at line ${parser.lineNumber}.")
    }

    @Throws(XmlPullParserException::class)
    private fun onUnknownChildFound(parser: XmlResourceParser, childName: String) {
        throw XmlPullParserException("'${parser.name}' cannot contains '$childName'.")
    }

    @Throws(XmlPullParserException::class)
    private fun onUnknownAttributeFound(parser: XmlResourceParser, attrName: String) {
        throw XmlPullParserException("'${parser.name}' cannot has a '$attrName' attribute.")
    }
}