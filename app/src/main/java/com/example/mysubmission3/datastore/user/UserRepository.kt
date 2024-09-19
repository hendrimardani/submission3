package com.example.mysubmission3.datastore.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.data.api.response.AddNewStoryResponse
import com.example.mysubmission3.data.api.response.DetailStoryResponse
import com.example.mysubmission3.data.api.response.GetAllStoriesResponse
import com.example.mysubmission3.data.api.response.LoginResponse
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.data.api.response.RegisterResponse
import com.example.mysubmission3.data.api.response.StoriesWithLocationResponse
import com.example.mysubmission3.data.api.retrofit.ApiService
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
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        try {
            userPreference.logout()
        } catch (e: HttpException) {
            Log.e(TAG, e.message())
        }
    }

    fun isRegisteredUser(name: String, email: String, password: String) = liveData {
        var client: RegisterResponse? = null
        emit(ResultState.Loading)
        try {
            client = apiService.register(name, email, password)
            _message.value = client.message!!
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            Log.e("$TAG Error onRegisterdUser :", e.printStackTrace().toString())
            emit(ResultState.Error(e.message()))
        }
        Log.d(TAG, "onIsRegisteredUser : $client")
    }

    fun login(email: String, password: String) = liveData {
        var client: LoginResponse? = null
        emit(ResultState.Loading)
        try {
            client = apiService.login(email, password)
            val loginResult = client.loginResult
            emit(ResultState.Success(loginResult))
        } catch (e: HttpException) {
            Log.e("$TAG Error onLogin :", e.printStackTrace().toString())
            emit(ResultState.Error(e.message()))
        }
        Log.d(TAG, "onLoginResult : $loginResult")
    }

    fun getAllStories() = liveData {
        var client: GetAllStoriesResponse? = null
        emit(ResultState.Loading)
        try {
            client = apiService.getAllStories()
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            Log.e("$TAG Error onGetAllStories :", e.printStackTrace().toString())
            emit(ResultState.Error(e.message()))
        }
        Log.d(TAG, "onGetAllStories : ${client!!.listStory}")
    }

    fun detailStory(id: String) = liveData {
        var client: DetailStoryResponse? = null
        emit(ResultState.Loading)
        try {
            client = apiService.detailStory(id)
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            Log.e("$TAG Error onDetailStory :", e.printStackTrace().toString())
            emit(ResultState.Error(e.message()))
        }
        Log.d(TAG, "onDetailStory : ${client!!.story}")
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        var successResponse: AddNewStoryResponse? = null
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            successResponse = apiService.uploadImage(multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddNewStoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message as String))
        }
        Log.d(TAG, "onUploadImage : ${successResponse!!.message}")
    }

    fun getAllStoriesWithLocation() = liveData {
        var client: StoriesWithLocationResponse? = null
        emit(ResultState.Loading)
        try {
            client = apiService.getStoriesWithLocation()
            emit(ResultState.Success(client))
        } catch (e: HttpException) {
            Log.e("$TAG Error onGetStoryWithLocation ", e.printStackTrace().toString())
            emit(ResultState.Error(e.message()))
        }
        Log.d(TAG, "onGetStoryWithLocation : ${client!!.listStoryWithLocation}")
    }

    companion object {
        private val TAG = UserRepository::class.java.simpleName
    }
}