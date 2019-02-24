package com.jamjamucho.smile

import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes
import com.jamjamucho.smile.parse.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class AnimatorAssociatingInflater(
    private val context: Context) {

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
    }

    @Throws(Resources.NotFoundException::class)
    fun inflate(@XmlRes xml: Int): AnimatorAssociatingBean {
        try {
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
        val states = mutableListOf<StatesBean>()
        val triggers = mutableListOf<TriggerBean>()
        val animations = mutableListOf<AnimationsBean>()
        val bindings = mutableListOf<BindingBean>()

        forEachChildNode { name ->
            when (name) {
                STATES -> states.add(inflateStates())
                TRIGGER -> triggers.add(inflateTrigger())
                ANIMATIONS -> animations.add(inflateAnimations())
                BINDING -> bindings.add(inflateBinding())
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

        return StateBean(name!!, default!!)
    }

    /**
     * format:
     * <trigger
     *   trigger="view-id"
     *   animations="string"
     *   stateName="string" (optional)
     *   necessaryStateCondition="string" (optional)
     *   nextState="string" (optional)
     *   />
     */
    private fun XmlResourceParser.inflateTrigger(): TriggerBean {
        var trigger: Int? = null
        var animations: String? = null
        var stateName: String? = null
        var necessaryStateCondition: String? = null
        var nextState: String? = null

        forEachAttribute { index, name ->
            when (name) {
                TRIGGER -> trigger = getAttributeResourceValue(index, 0)
                ANIMATIONS -> animations = getAttributeValue(index)
                STATE_NAME -> stateName = getAttributeValue(index)
                NECESSARY_STATE_CONDITION -> necessaryStateCondition = getAttributeValue(index)
                NEXT_STATE -> nextState = getAttributeValue(index)
                else -> onUnknownAttributeFound(this, name)
            }
        }

        trigger ?: onAttributeNotFound(this, TRIGGER)
        animations ?: onAttributeNotFound(this, ANIMATIONS)

        return TriggerBean(trigger!!, animations!!,
            stateName, necessaryStateCondition, nextState)
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
            id!!, allowInterruption,
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

        return BindingBean(id!!, target!!, animator!!)
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

        return RefBean(binding!!)
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