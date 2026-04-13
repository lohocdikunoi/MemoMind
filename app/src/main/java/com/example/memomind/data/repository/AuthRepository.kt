package com.example.memomind.data.repository

import com.example.memomind.data.remote.ApiService
import com.example.memomind.data.remote.dto.LoginRequest
import com.example.memomind.data.remote.dto.RegisterRequest
import com.example.memomind.util.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
) {
    val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedIn

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                tokenManager.saveUser(body.user._id, body.user.name, body.user.email)
                Result.success(body.user.name)
            } else {
                Result.failure(Exception("Email hoặc mật khẩu không đúng"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server"))
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val response = api.register(RegisterRequest(email, password, name))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                tokenManager.saveUser(body.user._id, body.user.name, body.user.email)
                Result.success(body.user.name)
            } else {
                Result.failure(Exception("Email đã được sử dụng"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Không thể kết nối server"))
        }
    }

    suspend fun logout() {
        tokenManager.clear()
    }

    suspend fun getUserName(): String? = tokenManager.getUserName()
}