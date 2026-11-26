package com.example.quartzevents.AdminFunctions

import com.example.quartzevents.Classes.Reservation
import com.example.quartzevents.Repositories.ReservationRepository

fun deleteCampsiteReservations(campId:String){
    val resRepo = ReservationRepository()
    var reservations: List<Reservation> = emptyList()
    resRepo.getCampsiteReservations(campId){ result, error ->
        if (result != null) {
            reservations = result
        }
    }

    for(reservation in reservations){
        resRepo.cancelReservation(reservation.id){success, message -> }
    }

}