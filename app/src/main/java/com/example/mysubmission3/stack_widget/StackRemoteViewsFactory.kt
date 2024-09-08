package com.example.mysubmission3.stack_widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.story.StoryActivity.Companion.mListStoryItem
import java.util.function.IntUnaryOperator


class StackRemoteViewsFactory(private val mContext: Context)
    : RemoteViewsService.RemoteViewsFactory {

        private var listPhotoUrl = ArrayList<String>()

    override fun onCreate() { }

    override fun onDataSetChanged() {
        mListStoryItem.forEach { listStoryItem ->
            println("data list = $listStoryItem")
            listStoryItem.forEach { item ->
                listPhotoUrl.add(item.photoUrl as String)
            }
        }
        println("data photoURL = $listPhotoUrl")
    }

    override fun onDestroy() { }

    override fun getCount(): Int = mListStoryItem.size


    override fun getViewAt(position: Int): RemoteViews {
//        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
////        rv.setImageViewUri(R.id.imageView, listStoryItem[position].photoUrl!!.toUri())
//
//        val extras = bundleOf(
//            ImagesBannerWidget.EXTRA_ITEM to position
//        )
//        val fillInIntent = Intent()
//        fillInIntent.putExtras(extras)
//
//        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
//        return rv

        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        listPhotoUrl.forEach {
            Glide.with(mContext)
                .asBitmap()
                .load(it)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        rv.setImageViewBitmap(R.id.imageView, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                })
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}