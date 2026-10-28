package com.example.quartzevents

import java.time.LocalDate
import java.time.format.DateTimeParseException

class Date(val date: String) {

    fun dateStr(): String{
        return date
    }

    //Google Gemini: "kotlin validate date"
    fun isValidDate(): Boolean {
        return try {
            LocalDate.parse(date)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    //Date comparisons
    fun isBefore(comp: Date): Boolean{
        return date < comp.dateStr()
    }
    fun isAfter(comp: Date): Boolean{
        return date > comp.dateStr()
    }
    fun isEqual(comp: Date): Boolean{
        return date == comp.dateStr()
    }


}