package com.jamjamucho.belfry

import android.view.ViewGroup
import com.jamjamucho.pumpkin.LayoutState
import com.jamjamucho.pumpkin.LayoutStateManager
import kotlin.reflect.KClass

class MainScreenState(layout: ViewGroup) {

    private class Default: LayoutState() {
        override fun onMakeState() {

            changeBounds(R.id.bottomPanel) {
                (it as BottomPanel).collapse()
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                (it as AnimationIcon).progress = 0f
            }.with(AnimationIcon.ChangeProgress())

        }
    }

    private class ShowingMenu: LayoutState() {
        override fun onMakeState() {

            changeBounds(R.id.bottomPanel) {
                (it as BottomPanel).expand()
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                when (it) {
                    is AnimationMenuIcon -> it.progress = 1f
                    is AnimationOptionsIcon -> it.progress = 0f
                }
            }.with(AnimationIcon.ChangeProgress())

        }
    }

    private class ShowingOptions: LayoutState() {
        override fun onMakeState() {

            changeBounds(R.id.bottomPanel) {
                (it as BottomPanel).expand()
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                when (it) {
                    is AnimationMenuIcon -> it.progress = 0f
                    is AnimationOptionsIcon -> it.progress = 1f
                }
            }.with(AnimationIcon.ChangeProgress())

        }
    }

    private enum class State(val kClass: KClass<out LayoutState>) {
        DEFAULT(Default::class),
        SHOWING_MENU(ShowingMenu::class),
        SHOWING_OPTIONS(ShowingOptions::class)
    }

    private var currentState = State.DEFAULT

    private val stateManager =
        LayoutStateManager
            .setupWith(layout)
            .addState(Default())
            .addState(ShowingMenu())
            .addState(ShowingOptions())
            .build()

    init {
        stateManager.postState(Default::class)
    }

    fun goToDefaultState() = goTo(State.DEFAULT)

    fun goToShowingMenuState() = goTo(State.SHOWING_MENU)

    fun goToShowingOptionsState() = goTo(State.SHOWING_OPTIONS)

    fun isDefaultState() = currentState == State.DEFAULT

    fun isShowingMenuState() = currentState == State.SHOWING_MENU

    fun isShowingOptionsState() = currentState == State.SHOWING_OPTIONS

    private fun goTo(state: State) {
        if (currentState != state) {
            currentState = state
            stateManager.go(state.kClass)
        }
    }
}