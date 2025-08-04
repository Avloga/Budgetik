package com.avloga.budgetik.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.navigation.NavBackStackEntry

fun slideUpTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        slideInVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseOutCubic
            )
        ) { fullHeight ->
            fullHeight
        }
    }
}

fun slideDownTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return {
        slideOutVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = EaseInCubic
            )
        ) { fullHeight ->
            -fullHeight
        }
    }
}

fun slideUpPopTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        slideInVertically(
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseOutCubic
            )
        ) { fullHeight ->
            -fullHeight
        }
    }
}

fun slideDownPopTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return {
        slideOutVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = EaseInCubic
            )
        ) { fullHeight ->
            fullHeight
        }
    }
} 