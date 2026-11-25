package com.example.quartzevents.AdminFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Message
import com.example.quartzevents.Repositories.MessageRepo
import com.example.quartzevents.Repositories.UserRepoMain
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AdminActivityLogsScreen(back: () -> Unit){
    val messagesRepository = remember{ MessageRepo() }
    var viewNotifications by remember { mutableStateOf(false) }
    var viewDirectMessages by remember { mutableStateOf(false) }

    val usersRepository = remember { UserRepoMain() }
    val users = usersRepository.getAllUsers()

    var messages by remember { mutableStateOf(listOf<Message>()) }
    if(viewNotifications){
        viewNotifications(messagesRepository, usersRepository)
//        messages = messagesRepository.getAllNotifications()
//        if(messages.isEmpty()) println("message notifications are empty")
    }
    else if(viewDirectMessages){
//        messages = messagesRepository.getAllDirectMessages()
//        if(messages.isEmpty()) println("message directs are empty")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6ED))
            .safeDrawingPadding()
            .imePadding(),
    ) {
        //Banner Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF254D3C)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row() {
                //back button, leads back to AdminDashboard
                TextButton(onClick = back) {
                    Text(
                        text = "<",
                        color = Color(0xFFFFFFFF),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
                Text(
                    text = "Activity Logs",
                    color = Color(0xFFFFFFFF),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 5.dp)
                )
            }
        }//end Banner Box

        //Acivities Log Choices
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            //Notifications selection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if(viewNotifications) Color(0xFFF7F5EC) else Color(0xFFD9D9D9))
                    .clickable { viewNotifications = true
                               viewDirectMessages = false},
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Notifications",
                    fontSize = 15.sp
                )
            }//end Notifications

            //Direct Messages Selection
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if(viewDirectMessages) Color(0xFFF7F5EC) else Color(0xFFD9D9D9))
                    .clickable { viewDirectMessages = true
                        viewNotifications = false},
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Direct Messages",
                    fontSize = 15.sp
                )
            }//end direct
        }//end menu row

        //Title 2
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if(viewNotifications)"All Notifications"
            else if(viewDirectMessages)"All Direct Messages"
            else "",
            color = Color(0xFF244D3D),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

//        Column(
//            modifier = Modifier.fillMaxWidth()
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            for (message in messages) {
//                //Finding the email of the sender and of the receiver
//                var senderEmail: String = ""
//                var receiverEmail: String = ""
//                for(user in users){
//                    if(user.uid == message.senderId) senderEmail = user.email
//                    else if(user.uid == message.receiverId) receiverEmail = user.email
//                }
//
//                //drawing one box for the message's data to be written in
//                Box(
//                    modifier = Modifier
//                        .size(350.dp, 105.dp)
//                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp)),
//                ){
//                    Column {
//                        //sender email
//                        Text(
//                            text = "Sender: $senderEmail",
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 15.sp,
//                            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
//                        )
//                        //receiver email
//                        Text(
//                            text = "Receiver: $receiverEmail",
//                            fontSize = 15.sp,
//                            modifier = Modifier.padding(start = 15.dp, top = 5.dp)
//                        )
//                        //message
//                        Text(
//                            text = "Message: ${message.message}",
//                            fontSize = 15.sp,
//                            modifier = Modifier.padding(start = 15.dp, top = 5.dp),
//                            softWrap = true,
//                            maxLines = 3,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//                }//end message box
//            }
//        }//end all messages

    }
}

@Composable
fun viewNotifications(mRepo: MessageRepo, uRepo: UserRepoMain){
    val messages = mRepo.getAllNotifications()
    val users = uRepo.getAllUsers()
    Column(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (message in messages) {
            //Finding the email of the sender and of the receiver
            var senderEmail: String = ""
            var receiverEmail: String = ""
            for(user in users){
                if(user.uid == message.senderId) senderEmail = user.email
                else if(user.uid == message.receiverId) receiverEmail = user.email
            }

            //drawing one box for the message's data to be written in
            Box(
                modifier = Modifier
                    .size(350.dp, 105.dp)
                    .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp)),
            ){
                Column {
                    //sender email
                    Text(
                        text = "Sender: $senderEmail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                    )
                    //receiver email
                    Text(
                        text = "Receiver: $receiverEmail",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 5.dp)
                    )
                    //message
                    Text(
                        text = "Message: ${message.message}",
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 5.dp),
                        softWrap = true,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }//end message box
        }
    }//end all messages
}