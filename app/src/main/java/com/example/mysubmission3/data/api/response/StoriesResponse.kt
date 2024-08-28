package com.example.mysubmission3.data.api.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
