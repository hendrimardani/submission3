package com.example.mysubmission3.ui.story

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.mysubmission3.R
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.data.api.response.LoginResult
import com.example.mysubmission3.databinding.ActivityStoryBinding
import com.example.mysubmission3.datastore.user.UserModel
import com.example.mysubmission3.ui.MainActivity
import com.example.mysubmission3.ui.MainViewModel
import com.example.mysubmission3.ui.ViewModelFactory
import com.example.mysubmission3.ui.add.AddActivity
import com.example.mysubmission3.ui.detail.DetailActivity
import com.example.mysubmission3.ui.detail.DetailActivity.Companion.EXTRA_ID
import com.example.mysubmission3.ui.detail.DetailActivity.Companion.EXTRA_TOKEN

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.story)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.isLoading().observe(this) { bool -> showLoading(bool) }
        getDataExtra()
        backButtonCallback()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logoutUser()
            R.id.menu_change_language -> changeLanguage()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun backButtonCallback() {
        onBackPressedDispatcher.addCallback(this) {
            // Langsung keluar dari aplikasi
            finishAffinity()
        }
    }

    private fun setupRecyclerViewItem(token: String) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager(this).orientation)
        binding.rvList.addItemDecoration(itemDecoration)
        viewModel.getAllStoryItem(token)
        viewModel.getListStoryItem().observe(this) { listStoryItem ->
            setStoryItem(token, listStoryItem)
        }
    }

    private fun setStoryItem(token: String, listStoryItem: List<ListStoryItem>) {
        val adapter = StoryAdapter(this)
        adapter.submitList(listStoryItem)
        binding.rvList.adapter = adapter
        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(item: ListStoryItem) {
                val intent = Intent(this@StoryActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_ID, item.id)
                intent.putExtra(EXTRA_TOKEN, token)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun logoutUser() {
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(getString(R.string.title_logout_account_dialog))
            .setConfirmButton(getString(R.string.yes_logout_account_dialog)) {
                UserModel(userId = "", name = "", token = "", isLogin = false)
                viewModel.logout()
                val intent = Intent(this@StoryActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }

    private fun getDataExtra() {
        val getActivityData = intent.getStringExtra(EXTRA_ACTIVITY)
        if (getActivityData == "LoginActivity") {
            val getLoginResultData = intent.getParcelableExtra<LoginResult>(EXTRA_OBJECT)
            val getTokenData = getLoginResultData!!.token as String
            setupRecyclerViewItem(getTokenData)
        } else if (getActivityData == "AddActivity") {
            val getUserModelData = intent.getParcelableExtra<UserModel>(EXTRA_OBJECT)
            val getTokenData = getUserModelData!!.token
            setupRecyclerViewItem(getTokenData)
        } else {
            val getUserModelData = intent.getParcelableExtra<UserModel>(EXTRA_OBJECT)
            val getTokenData = getUserModelData!!.token
            setupRecyclerViewItem(getTokenData)
        }
    }

    companion object {
        const val EXTRA_OBJECT = "extra_object"
        const val EXTRA_ACTIVITY = "extra_activity"
    }
}