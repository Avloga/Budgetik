package com.avloga.budgetik.ui.components

import androidx.compose.ui.graphics.Color
import com.avloga.budgetik.ui.theme.*

enum class CategoryKind { EXPENSE, INCOME }

data class AppCategory(
    val name: String,
    val emoji: String,
    val color: Color,
    val kind: CategoryKind
)

object Categories {
    // Єдиний список категорій витрат
    val expenseCategories: List<AppCategory> = listOf(
        AppCategory("Зв'язок", "📞", CategoryPink, CategoryKind.EXPENSE),
        AppCategory("Їжа", "🍽️", CategoryGreen, CategoryKind.EXPENSE),
        AppCategory("Кафе", "☕", CategoryOrange, CategoryKind.EXPENSE),
        AppCategory("Транспорт", "🚌", CategoryBlue, CategoryKind.EXPENSE),
        AppCategory("Техніка", "💻", CategoryYellow, CategoryKind.EXPENSE),
        AppCategory("Гігієна", "🧴", CategoryCyan, CategoryKind.EXPENSE),
        AppCategory("Улюбленці", "🐱", CategoryTeal, CategoryKind.EXPENSE),
        AppCategory("Одяг", "👕", CategoryPurple, CategoryKind.EXPENSE),
        AppCategory("Подарунки", "🎁", CategoryRed, CategoryKind.EXPENSE),
        AppCategory("Спорт", "⚽", CategoryLime, CategoryKind.EXPENSE),
        AppCategory("Здоров'я", "🏥", CategoryDeepOrange, CategoryKind.EXPENSE),
        AppCategory("Ігри", "🎮", CategoryIndigo, CategoryKind.EXPENSE),
        AppCategory("Розваги", "🍺", CategoryAmber, CategoryKind.EXPENSE),
        AppCategory("Житло", "🏠", CategoryBrown, CategoryKind.EXPENSE)
    )

    // Єдиний список категорій доходів
    val incomeCategories: List<AppCategory> = listOf(
        AppCategory("Зарплата", "💰", IncomeGreen, CategoryKind.INCOME),
        AppCategory("Подарунок", "🎁", CategoryPurple, CategoryKind.INCOME),
        AppCategory("Заощадження", "🏦", CategoryBlue, CategoryKind.INCOME)
    )

    // Пошук категорії за назвою (незалежно від регістру)
    private val nameToCategory: Map<String, AppCategory> =
        (expenseCategories + incomeCategories).associateBy { it.name.lowercase() }

    fun findByName(name: String?): AppCategory? = name?.let { nameToCategory[it.lowercase()] }

    // Імена для випадаючих списків
    fun expenseNames(): List<String> = expenseCategories.map { it.name }
    fun incomeNames(): List<String> = incomeCategories.map { it.name }

    // Побудова елементів для гріда категорій на головному екрані
    fun toCategoryItems(): List<CategoryItem> = expenseCategories.map {
        CategoryItem(text = it.emoji, color = it.color, contentDescription = it.name)
    }
}


