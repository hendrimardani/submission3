package com.example.mysubmission3.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.mysubmission3.R
import com.example.mysubmission3.ResultState
import com.example.mysubmission3.databinding.ActivityDetailBinding
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var sweetAlertDialog: SweetAlertDialog? = null

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
        supportActionBar!!.title = getString(R.string.detail_activity)
        getDataExtra()
    }

    private fun getDataExtra() {
        val getIdData = intent.getStringExtra(EXTRA_ID) as String
        val getTokenData = intent.getStringExtra(EXTRA_TOKEN) as String
        Log.d(TAG, "ID User: $getIdData, Token User : $getTokenData")
        viewModel.getDetailStory(getIdData).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> { showLoading(true) }
                    is ResultState.Error -> {
                        val message = result.error
                        showError(message)
                        showLoading(false)
                    }
                    is ResultState.Success -> {
                        val data = result.data.story!!
                        Glide.with(this@DetailActivity)
                            .load(data.photoUrl)
                            .into(binding.ivDetail)
                        binding.tvNameDetail.text = data.name
                        binding.tvCreatedAtDetail.text = getString(R.string.createdAt, data.createdAt)
                        binding.tvIdDetail.text = data.id
                        binding.tvDescriptionDetail.text = data.description
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
        sweetAlertDialog!!.setTitleText(getString(R.string.error_title_request))
        sweetAlertDialog!!.setContentText(message)
        sweetAlertDialog!!.show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    companion object {
        private val TAG = DetailActivity::class.java.simpleName
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}