package com.avloga.budgetik.ui.components

import androidx.compose.ui.graphics.Color
import com.avloga.budgetik.ui.theme.*

enum class CategoryKind { EXPENSE, INCOME }

data class AppCategory(
    val name: String,
    val emoji: String,
    val color: Color,
    val kind: CategoryKind,
    val drawableName: String? = null
)

object Categories {
    // Єдиний список категорій витрат
    val expenseCategories: List<AppCategory> = listOf(
        // #4A90E2
        AppCategory("Зв'язок", "📞", CategoryBlue, CategoryKind.EXPENSE, drawableName = "phone_icon"),
        
        // #7ED321
        AppCategory("Їжа", "🍽️", CategoryGreen, CategoryKind.EXPENSE, drawableName = "food_icon"),
        
        // #A97C50
        AppCategory("Кафе", "☕", CategoryOrange, CategoryKind.EXPENSE, drawableName = "cafe_icon"),
        
        // #FF9800
        AppCategory("Транспорт", "🚌", CategoryDeepOrange, CategoryKind.EXPENSE, drawableName = "transport_icon"),
        
        // #607D8B
        AppCategory("Техніка", "💻", CategoryBlueGrey, CategoryKind.EXPENSE, drawableName = "electronics_icon"),
        
        // #00BCD4
        AppCategory("Гігієна", "🧴", CategoryCyan, CategoryKind.EXPENSE, drawableName = "hygiene_icon"),
        
        //rgb(29, 28, 27)
        AppCategory("Інше", "🐱", CategoryLightPink, CategoryKind.EXPENSE, drawableName = "other_icon"),
        
        // #9C27B0
        AppCategory("Одяг", "👕", CategoryPurple, CategoryKind.EXPENSE, drawableName = "clothes_icon"),
        
        // #F44336
        AppCategory("Подарунки", "🎁", CategoryRed, CategoryKind.EXPENSE, drawableName = "gift_icon"), 
        
        // #4CAF50
        AppCategory("Спорт", "⚽", CategoryGreenDark, CategoryKind.EXPENSE, drawableName = "sport_icon"),
        
        // #E53935
        AppCategory("Здоров'я", "🏥", CategoryRedDark, CategoryKind.EXPENSE, drawableName = "health_icon"),
        
        // #3F51B5
        AppCategory("Ігри", "🎮", CategoryIndigo, CategoryKind.EXPENSE, drawableName = "games_icon"),
        
        // #FFC107
        AppCategory("Розваги", "🍺", CategoryAmber, CategoryKind.EXPENSE, drawableName = "entertainment_icon"),
        
        // #8D6E63
        AppCategory("Житло", "🏠", CategoryBrown, CategoryKind.EXPENSE, drawableName = "home_icon")
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
        CategoryItem(text = it.emoji, color = it.color, contentDescription = it.name, drawableName = it.drawableName)
    }
}


