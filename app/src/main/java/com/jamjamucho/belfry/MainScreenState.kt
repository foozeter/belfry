package com.jamjamucho.belfry

import android.view.ViewGroup
import com.jamjamucho.pumpkin.LayoutState
import com.jamjamucho.pumpkin.LayoutStateManager
import kotlin.reflect.KClass

class MainScreenState(layout: ViewGroup) {

    private class Default(
        val bottomPanelCornerRadius: Float)
        : LayoutState() {

        override fun onMakeState() {

            changeTranslationY(R.id.bottomPanel) {
                it.translationY = 0f
            }

            changeY(R.id.bottomPanelContentScroller) {
                it.y = it.height / 2f
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                (it as AnimatableIcon).progress = 0f
            }.with(AnimatableIcon.ChangeProgress())

        }
    }

    private class ShowingMenu(
        val bottomPanelCornerRadius: Float)
        : LayoutState() {

        override fun onMakeState() {

            changeY(
                R.id.bottomPanel,
                R.id.bottomPanelContentScroller
            ) { it.y = 0f }

            changeAlpha(R.id.menu, R.id.options) {
                it.alpha = if (it.id == R.id.menu) 1f else 0f
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                when (it) {
                    is AnimatableMenuIcon -> it.progress = 1f // to close icon
                    is AnimatableOptionsIcon -> it.progress = 0f // to options icon
                }
            }.with(AnimatableIcon.ChangeProgress())

        }
    }

    private class ShowingOptions(
        val bottomPanelCornerRadius: Float)
        : LayoutState() {

        override fun onMakeState() {

            changeY(
                R.id.bottomPanel,
                R.id.bottomPanelContentScroller
            ) { it.y = 0f }

            changeAlpha(R.id.menu, R.id.options) {
                it.alpha = if (it.id == R.id.options) 1f else 0f
            }

            change(R.id.menuIcon, R.id.optionsIcon) {
                when (it) {
                    is AnimatableMenuIcon -> it.progress = 0f // to menu icon
                    is AnimatableOptionsIcon -> it.progress = 1f // to close icon
                }
            }.with(AnimatableIcon.ChangeProgress())

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
            .addState(Default(layout.context.resources.getDimensionPixelSize(R.dimen.main_collapsed_bottom_panel_corner_radius).toFloat()))
            .addState(ShowingMenu(layout.context.resources.getDimensionPixelSize(R.dimen.main_expanded_bottom_panel_corner_radius).toFloat()))
            .addState(ShowingOptions(layout.context.resources.getDimensionPixelSize(R.dimen.main_expanded_bottom_panel_corner_radius).toFloat()))
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