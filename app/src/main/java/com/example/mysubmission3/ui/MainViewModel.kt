package com.example.mysubmission3.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.data.api.response.GetAllStoriesResponse
import com.example.mysubmission3.data.api.response.ListStoriesWithLocation
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.data.api.response.Story
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.datastore.user.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    val story: LiveData<PagingData<ListStoryItem>> =
        userRepository.getAllStories().cachedIn(viewModelScope)

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            userRepository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun message(): LiveData<String> {
        return userRepository.message
    }

    fun isRegistered(name: String, email: String, password: String) = liveData {
        emitSource(userRepository.isRegisteredUser(name, email, password))
    }

    fun login(email: String, password: String) = liveData {
        emitSource(userRepository.login(email, password))
    }

    fun getAllStories() = liveData {
        emitSource(userRepository.getAllStories())
    }

    fun getDetailStory(id: String) = liveData {
        emitSource(userRepository.detailStory(id))
    }

    fun uploadImage(file: File, description: String) = userRepository.uploadImage(file, description)

    fun getAllStoriesWithLocation() = liveData {
        emitSource(userRepository.getAllStoriesWithLocation())
    }
}