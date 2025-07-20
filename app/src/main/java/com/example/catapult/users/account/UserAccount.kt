package com.example.catapult.users.account

import kotlinx.serialization.Serializable

@Serializable
data class UserAccount (
    var firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
)