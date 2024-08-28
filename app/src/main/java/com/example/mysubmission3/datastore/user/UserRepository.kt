package com.example.mysubmission3.datastore.user

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.mysubmission3.data.api.response.RegisterResponse
import com.example.mysubmission3.data.api.retrofit.ApiConfig
import com.example.mysubmission3.data.api.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun isRegisteredUser(token: String, name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).register(name, email, password)
        val message = client.message
        _message.value = message as String
        _isLoading.value = false
        Log.d(TAG, message.toString())
    }

    suspend fun login(token: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).login(email, password)
        val loginResult = client.loginResult
        _isLoading.value = false
        Log.d(TAG, loginResult.toString())
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}