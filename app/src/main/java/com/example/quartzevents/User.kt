package com.example.quartzevents

//username for login is email, check that this email does not already exist for an account
abstract class User(val email: String, var name: String, var password: String){
    abstract fun userType()
}