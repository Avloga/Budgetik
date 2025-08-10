package com.avloga.budgetik.util

import com.avloga.budgetik.data.model.Expense
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class Period {
    DAY, WEEK, MONTH, YEAR, ALL
}

object PeriodUtils {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val ukrainianLocale = Locale("uk")
    
    /**
     * Повертає назву місяця в називному відмінку українською мовою
     */
    private fun getMonthNameNominative(month: Month): String {
        return when (month) {
            Month.JANUARY -> "Січень"
            Month.FEBRUARY -> "Лютий"
            Month.MARCH -> "Березень"
            Month.APRIL -> "Квітень"
            Month.MAY -> "Травень"
            Month.JUNE -> "Червень"
            Month.JULY -> "Липень"
            Month.AUGUST -> "Серпень"
            Month.SEPTEMBER -> "Вересень"
            Month.OCTOBER -> "Жовтень"
            Month.NOVEMBER -> "Листопад"
            Month.DECEMBER -> "Грудень"
        }
    }
    
    /**
     * Фільтрує витрати за вказаним періодом
     */
    fun filterExpensesByPeriod(expenses: List<Expense>, period: Period): List<Expense> {
        val today = LocalDate.now()
        
        return when (period) {
            Period.DAY -> {
                val todayStr = today.format(dateFormatter)
                expenses.filter { it.date == todayStr }
            }
            Period.WEEK -> {
                val weekAgo = today.minusDays(6) // 7 днів включно з сьогодні
                expenses.filter { expense ->
                    try {
                        val expenseDate = LocalDate.parse(expense.date, dateFormatter)
                        !expenseDate.isBefore(weekAgo) && !expenseDate.isAfter(today)
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            Period.MONTH -> {
                val currentMonth = today.monthValue
                val currentYear = today.year
                expenses.filter { expense ->
                    try {
                        val expenseDate = LocalDate.parse(expense.date, dateFormatter)
                        expenseDate.monthValue == currentMonth && expenseDate.year == currentYear
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            Period.YEAR -> {
                val currentYear = today.year
                expenses.filter { expense ->
                    try {
                        val expenseDate = LocalDate.parse(expense.date, dateFormatter)
                        expenseDate.year == currentYear
                    } catch (e: Exception) {
                        false
                    }
                }
            }
            Period.ALL -> expenses
        }
    }
    
    /**
     * Форматує дату для відображення залежно від періоду
     */
    fun formatDateForPeriod(period: Period, expenses: List<Expense> = emptyList()): String {
        val today = LocalDate.now()
        
        return when (period) {
            Period.DAY -> {
                val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, ukrainianLocale)
                    .replaceFirstChar { it.uppercase(ukrainianLocale) }
                val day = today.dayOfMonth.toString()
                val month = today.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                    .lowercase(ukrainianLocale)
                    .replaceFirstChar { it.uppercase(ukrainianLocale) }
                "$dayOfWeek, $day $month"
            }
            Period.WEEK -> {
                val weekAgo = today.minusDays(6)
                val startDay = weekAgo.dayOfMonth
                val endDay = today.dayOfMonth
                
                // Якщо початок і кінець тижня в різних місяцях
                if (weekAgo.monthValue != today.monthValue) {
                    val startMonth = weekAgo.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                        .lowercase(ukrainianLocale)
                        .replaceFirstChar { it.uppercase(ukrainianLocale) }
                    val endMonth = today.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                        .lowercase(ukrainianLocale)
                        .replaceFirstChar { it.uppercase(ukrainianLocale) }
                    "$startDay $startMonth - $endDay $endMonth"
                } else {
                    val month = today.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                        .lowercase(ukrainianLocale)
                        .replaceFirstChar { it.uppercase(ukrainianLocale) }
                    "$startDay-$endDay $month"
                }
            }
            Period.MONTH -> {
                getMonthNameNominative(today.month)
            }
            Period.YEAR -> {
                today.year.toString()
            }
            Period.ALL -> {
                if (expenses.isEmpty()) {
                    "Усі операції"
                } else {
                    val sortedExpenses = expenses.sortedBy { expense ->
                        try {
                            LocalDate.parse(expense.date, dateFormatter)
                        } catch (e: Exception) {
                            LocalDate.MIN
                        }
                    }
                    
                    if (sortedExpenses.isEmpty()) {
                        "Усі операції"
                    } else {
                        val firstExpense = sortedExpenses.first()
                        val lastExpense = sortedExpenses.last()
                        
                        try {
                            val firstDate = LocalDate.parse(firstExpense.date, dateFormatter)
                            val lastDate = LocalDate.parse(lastExpense.date, dateFormatter)
                            
                            val firstDay = firstDate.dayOfMonth
                            val lastDay = lastDate.dayOfMonth
                            val firstMonth = firstDate.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                                .lowercase(ukrainianLocale)
                                .replaceFirstChar { it.uppercase(ukrainianLocale) }
                            val lastMonth = lastDate.month.getDisplayName(TextStyle.FULL, ukrainianLocale)
                                .lowercase(ukrainianLocale)
                                .replaceFirstChar { it.uppercase(ukrainianLocale) }
                            
                            if (firstDate.year == lastDate.year) {
                                "$firstDay $firstMonth - $lastDay $lastMonth"
                            } else {
                                "$firstDay $firstMonth ${firstDate.year} - $lastDay $lastMonth ${lastDate.year}"
                            }
                        } catch (e: Exception) {
                            "Усі операції"
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Конвертує назву періоду в enum
     */
    fun periodFromString(periodName: String): Period {
        return when (periodName) {
            "День" -> Period.DAY
            "Тиждень" -> Period.WEEK
            "Місяць" -> Period.MONTH
            "Рік" -> Period.YEAR
            "Усі" -> Period.ALL
            else -> Period.DAY
        }
    }
    
    /**
     * Конвертує enum в назву періоду
     */
    fun periodToString(period: Period): String {
        return when (period) {
            Period.DAY -> "День"
            Period.WEEK -> "Тиждень"
            Period.MONTH -> "Місяць"
            Period.YEAR -> "Рік"
            Period.ALL -> "Усі"
        }
    }
}
