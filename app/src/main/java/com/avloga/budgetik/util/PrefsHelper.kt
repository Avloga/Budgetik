package com.avloga.budgetik.util

import android.content.Context

fun saveUserIdToPrefs(context: Context, userId: String) {
    val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    prefs.edit().putString("userId", userId).apply()
}
