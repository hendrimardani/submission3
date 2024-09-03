package com.example.mysubmission3.ui.detail_story

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mysubmission3.R
import com.example.mysubmission3.databinding.ActivityDetailBinding
import com.example.mysubmission3.databinding.ActivityStoryBinding
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.isLoading().observe(this) { bool -> showLoading(bool) }
        getDataExtra()
    }

    private fun getDataExtra() {
        val getIdData = intent.getStringExtra(EXTRA_ID) as String
        val getTokenData = intent.getStringExtra(EXTRA_TOKEN) as String
        Log.d(TAG, "ID User: $getIdData, Token User : $getTokenData")
        viewModel.detailStory(getTokenData, getIdData)
        viewModel.getDetailStory().observe(this) { storyUser ->
            Glide.with(this@DetailActivity)
                .load(storyUser.photoUrl)
                .into(binding.ivDetail)
            binding.tvNameDetail.text = storyUser.name
            binding.tvCreatedAtDetail.text = getString(R.string.createdAt, storyUser.createdAt)
            binding.tvIdDetail.text = storyUser.id
            binding.tvDescriptionDetail.text = storyUser.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        private val TAG = DetailActivity::class.java.simpleName
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}