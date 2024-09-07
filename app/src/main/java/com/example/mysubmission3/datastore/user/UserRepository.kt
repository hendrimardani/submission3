package com.example.mysubmission3.datastore.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.data.api.response.AddNewStoryResponse
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.data.api.response.Story
import com.example.mysubmission3.data.api.retrofit.ApiConfig
import com.example.mysubmission3.data.api.retrofit.ApiService
import com.example.mysubmission3.ui.login.LoginActivity.Companion.ERROR_RESPONSE
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository(
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

    suspend fun isRegisteredUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = apiService.register(name, email, password)
        val message = client.message
        _message.value = message as String
        _isLoading.value = false
        Log.d(TAG, "onIsRegisteredUser : ${_message.value}")
    }

    suspend fun login(email: String, password: String) {
        try {
            _isLoading.value = true
            val client = apiService.login(email, password)
            val loginResult = client.loginResult
            _loginResult.value = loginResult as LoginResult
            _isLoading.value = false
            Log.d(TAG, "onLoginResult : ${_loginResult.value}")
        } catch (e: HttpException) {
            ERROR_RESPONSE = true
        }
    }

    suspend fun getAllStories() {
        _isLoading.value = true
        val client = apiService.getAllStories()
        _getListStoryItem.value = client.listStory as List<ListStoryItem>
        _isLoading.value = false
        Log.d(TAG, "onGetListStoryItem : ${_getListStoryItem.value}")
    }

    suspend fun detailStory(id: String) {
        _isLoading.value = true
        val client = apiService.detailStory(id)
        _story.value = client.story as Story
        _isLoading.value = false
        Log.d(TAG, "onDetailStory : ${_story.value}")
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        _isLoading.value = true
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
            _isLoading.value = false
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddNewStoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message as String))
            _isLoading.value = false
        }
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}