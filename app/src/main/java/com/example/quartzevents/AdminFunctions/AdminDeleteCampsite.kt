package com.example.quartzevents.AdminFunctions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Classes.Reservation
import com.example.quartzevents.Repositories.ReservationRepository
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Campsite
import android.util.Log
import androidx.compose.material.icons.filled.Done

@Composable
fun VarifyRemoveCampsite(campsite: Campsite?, confirm: () -> Unit, cancel: () -> Unit){
    if(campsite==null){
        Log.e("VerifyRemoveCampsite", "campsite is null, cannot remove")
        cancel
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){

        Box(
            modifier = Modifier
                .size(370.dp, 150.dp)
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                .padding(bottom = 15.dp)
        ){

            Row(){
                Image(
                    Icons.Default.Warning,
                    "Warning Icon",
                    colorFilter = ColorFilter.tint(Color(0xFF911A1A)),
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 5.dp)
                        .size(30.dp)
                )
                Text(
                    text = "Remove Campsite?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }//end row

            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "Are you sure you want to remove ${campsite!!.name}\n" +
                        "Campground? This action cannot be undone.",
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )

            //buttons
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                        .padding(horizontal = 15.dp)

                ){}

                //cancel button
                Button(
                    onClick = cancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                        .fillMaxWidth()
                        .height(35.dp)
                ) {
                    Text(text = "Cancel")
                }
                //remove button
                Button(
                    onClick = confirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF911A1A),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                        .fillMaxWidth()
                        .height(35.dp)
                ) {
                    Text(text = "Remove")
                }

            }//end buttons row

        }//end box

    }

}

@Composable
fun DeleteCampsiteConfirmed(onOkay: () -> Unit){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Box(
            modifier = Modifier
                .size(370.dp, 100.dp)
                .background(color = Color.White, shape = RoundedCornerShape(5.dp))
                .padding(bottom = 15.dp),
            contentAlignment = Alignment.CenterStart
        ){
            Row(){
                Image(
                    Icons.Default.Done,
                    "Done Icon",
                    colorFilter = ColorFilter.tint(Color(0xFF254D3C)),
                    modifier = Modifier
                        .padding(start = 10.dp, end = 5.dp)
                        .size(30.dp)
                )
                Text(
                    text = "The Campsite has been deleted.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                //Okay button
                Button(
                    onClick = onOkay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                        .height(35.dp)
                ) {
                    Text(text = "Okay")
                }
            }//end row
        }
    }
}

fun deleteCampsiteReservations(campId:String?){
    if(campId==null){
        println("error: campsite id is null, cannot remove reservations")
        return
    }

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