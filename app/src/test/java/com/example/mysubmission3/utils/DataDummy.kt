package com.example.mysubmission3.utils

import com.example.mysubmission3.data.api.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val item = ListStoryItem(
                i.toString(),
                "photo url + $i",
                "name + $i"
            )
            items.add(item)
        }
        return items
    }
}