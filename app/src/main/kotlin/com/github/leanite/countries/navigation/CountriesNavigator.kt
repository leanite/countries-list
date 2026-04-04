package com.github.leanite.countries.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class CountriesNavigator(val backStack: NavBackStack<NavKey>) {

    fun navigate(route: NavKey) {
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun popToRoot() {
        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}
