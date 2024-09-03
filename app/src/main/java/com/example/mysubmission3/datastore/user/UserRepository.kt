package com.example.mysubmission3.datastore.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.data.api.response.Story
import com.example.mysubmission3.data.api.retrofit.ApiConfig
import com.example.mysubmission3.data.api.retrofit.ApiService
import com.example.mysubmission3.ui.login.LoginActivity.Companion.ERROR_RESPONSE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private var _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private var _getListStoryItem = MutableLiveData<List<ListStoryItem>>()
    val getListStoryItem: LiveData<List<ListStoryItem>> = _getListStoryItem

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
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
        Log.d(TAG, "onIsRegisteredUser : ${_message.value}")
    }

    suspend fun login(token: String, email: String, password: String) {
        try {
            _isLoading.value = true
            val client = ApiConfig.getApiService(token).login(email, password)
            val loginResult = client.loginResult
            _loginResult.value = loginResult as LoginResult
            _isLoading.value = false
            Log.d(TAG, "onLoginResult : ${_loginResult.value}")
        } catch (e: HttpException) {
            ERROR_RESPONSE = true
        }
    }

    suspend fun getAllStories(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).getAllStories()
        _getListStoryItem.value = client.listStory as List<ListStoryItem>
        _isLoading.value = false
        Log.d(TAG, "onGetListStoryItem : ${_getListStoryItem.value}")
    }

    suspend fun detailStory(token: String, id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService(token).detailStory(id)
        _story.value = client.story as Story
        _isLoading.value = false
        Log.d(TAG, "onDetailStory : ${_story.value}")
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(apiService: ApiService, userPreference: UserPreference): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(
                    apiService,
                    userPreference
                )
            }.also { instance = it }
    }
}