package com.example.memomind.data.remote.dto

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val name: String)
data class RefreshRequest(val refreshToken: String)

data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)

data class UserDto(
    val _id: String,
    val email: String,
    val name: String,
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)