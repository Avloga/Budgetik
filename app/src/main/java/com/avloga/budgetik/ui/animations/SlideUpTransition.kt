package com.avloga.budgetik.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.navigation.NavBackStackEntry

// Оптимізовані анімації для кращої продуктивності
fun slideUpTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        slideInVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
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
                easing = FastOutSlowInEasing
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
                durationMillis = 250,
                easing = FastOutSlowInEasing
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
                easing = FastOutSlowInEasing
            )
        ) { fullHeight ->
            fullHeight
        }
    }
}

// Додаткові оптимізовані анімації
fun fastSlideUpTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) {
    return {
        slideInVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearOutSlowInEasing
            )
        ) { fullHeight ->
            fullHeight
        }
    }
}

fun fastSlideDownTransition(): (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) {
    return {
        slideOutVertically(
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearOutSlowInEasing
            )
        ) { fullHeight ->
            fullHeight
        }
    }
} 