package com.example.quartzevents.Classes

import com.example.quartzevents.Campsite

class Reservation(val entrantName: String, val campsite: Campsite, var guests: Int, val start: Date, val end: Date) {

}