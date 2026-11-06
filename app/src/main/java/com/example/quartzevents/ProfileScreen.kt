package com.example.quartzevents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.model.UserProfile

private val avatarGray = Color(0xFFC7D6D0)
private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val fieldGray = Color(0xFFD9D9D9)
private val deleteRed = Color(0xFFC62828)

/**
 * Displays the entrant's profile information and account settings.
 *
 * Shows the user's name, email, account management options,
 * notification preferences, device identification, and policy viewing.
 *
 * @param profile The current user's profile information.
 * @param enableNotification Whether notifications are enabled.
 * @param onNotificationsChanged Called when the notification setting changes.
 * @param onEditProfile Called when the user clicks Edit Personal Information.
 * @param onViewPolicies Called when the user clicks App Policies & Guidelines.
 * @param onDeleteProfile Called when the user clicks Delete Profile
 * @param onBack Called when the user navigates back to the previous screen.
 */

@Composable
fun ProfileScreen(
    profile: UserProfile,
    enableNotification: Boolean,
    onNotificationsChanged: (Boolean) -> Unit,
    onEditProfile: () -> Unit,
    onViewPolicies: () -> Unit,
    onDeleteProfile: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    // TODO: replace temporary state with actual device identification status
    var deviceRemembered by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(darkGreen),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // profile information
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(avatarGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = darkGreen,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.name,
                color = darkGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.email,
                color = darkGreen,
                fontSize = 14.sp
            )
        }

        // ACCOUNT SECT.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Account",
                color = darkGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // edit personal information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fieldGray, RoundedCornerShape(12.dp))
                    .clickable { onEditProfile() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = darkGreen
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Edit Personal Information",
                    color = darkGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = darkGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TODO: device identification US 01.09.01
            // TEMPORARY UI, needs firebase
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fieldGray, RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .toggleable(
                        value = deviceRemembered,
                        role = Role.Switch,
                        onValueChange = { deviceRemembered }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = darkGreen
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Device Identification",
                        color = darkGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (deviceRemembered) {
                            "This device is remembered"
                        } else {
                            "This device is not remembered"
                        },
                        color = darkGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // circle for toggle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 3.dp,
                            color = darkGreen,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // toggles on and off
                    if (deviceRemembered) {
                        Box(modifier = Modifier
                            .size(16.dp)
                            .background(darkGreen,CircleShape)
                        )
                    }
                }
            }
        }

        // PREFERENCES SECT.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Preferences",
                color = darkGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // notification settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fieldGray, RoundedCornerShape(12))
                    .padding(16.dp)
                    .toggleable(
                        value = enableNotification,
                        role = Role.Switch,
                        onValueChange = onNotificationsChanged
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = darkGreen

                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Notifications",
                        color = darkGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (enableNotification) {
                            "Enabled"
                        } else {
                            "Disabled"
                        },
                        color = darkGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // circle notification toggle
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 3.dp,
                            color = darkGreen,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // toggles on and off
                    if (enableNotification) {
                        Box(modifier = Modifier
                            .size(16.dp)
                            .background(darkGreen,CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // app policies and guidelines
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fieldGray, RoundedCornerShape(12.dp))
                    .clickable { onViewPolicies() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = darkGreen
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "App Policies & Guidelines",
                    color = darkGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = darkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ACCOUNT MNG. SECT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Account Management",
                color = darkGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TODO: Delete Profile (another screen) US 01.08.01
            // Temporary UI, deleteProfileScreen and firebase account deletion
            // not implemented yet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fieldGray, RoundedCornerShape(12.dp))
                    .clickable { onDeleteProfile() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = deleteRed
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Delete Profile",
                    color = deleteRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = deleteRed
                )
            }
        }
    }
}