/**
 * Represents a date.
 *
 * Date is able to be validated, and compared
 *
 * @property date a string representation of date
 */

package com.example.quartzevents

import java.time.LocalDate
import java.time.format.DateTimeParseException

class Date(val date: String) {
//yyyy-mm-dd
    fun dateStr(): String{
    /**
     * provides a string representation of date
     *
     *  * @param None
     *  * @return the string representation of the date
     */
        return date
    }

    //Google Gemini: "kotlin validate date"
    fun isValidDate(): Boolean {
        /**
         * determines if the date is valid
         *
         *  * @param None
         *  * @return a boolean of whether or not the date is valid (true or false)
         */
        return try {
            LocalDate.parse(date)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    //Date comparisons
    fun isBefore(comp: Date): Boolean{
        /**
         * determines if the date comes before another date
         *
         *  * @param comp is the other date being compared with
         *  * @return a boolean of whether or not the date comes before the other date (true or false)
         */
        return date < comp.dateStr()
    }
    fun isAfter(comp: Date): Boolean{
        /**
         * determines if the date comes after another date
         *
         *  * @param comp is the other date being compared with
         *  * @return a boolean of whether or not the date comes after the other date (true or false)
         */
        return date > comp.dateStr()
    }
    fun isEqual(comp: Date): Boolean{
        /**
         * determines if the date is equal to another date
         *
         *  * @param comp is the other date being compared with
         *  * @return a boolean of whether or not the date is equal to the other date (true or false)
         */
        return date == comp.dateStr()
    }


}