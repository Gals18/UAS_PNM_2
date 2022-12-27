package com.galuhsaputri.githubusers.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.galuhsaputri.githubusers.R
import com.galuhsaputri.githubusers.databinding.ActivityMainBinding
import com.galuhsaputri.githubusers.ui.settings.SettingsActivity
import com.galuhsaputri.core.domain.model.UserSearchItem
import com.galuhsaputri.core.utils.state.LoaderState
import com.galuhsaputri.core.utils.viewUtils.setGone
import com.galuhsaputri.core.utils.viewUtils.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
//membuat kelas model untuk view activity_main.xml
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
//kode diatas merupakan pendeklarasian variable digunakan untuk menghubungkan kekelas viewModel
    private val items = mutableListOf<UserSearchItem>()
    //kode diatas merupakan pendeklarasian variable item digunakan untuk menampilkan pencarian dari akun github
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter(this)
    }
    //kode diatas merupakan pendeklarasin variable untuk menghubungkan kelas main ke kelas adapter
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    //kode diatas merupakan pembuatan data binding untuk menuju ke Activity_main.xml
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        searchUsers()
        initRecyclerView()
        initObserver()
    }
    //kode diatas merupakan fungsi untuk pengeklikan suatu tombol sehingga menuju ke fungsi yang
    // lain yaitu fungsi initToolbar(), searchuser(), initRecyclerView(),initObserver().

    private fun initToolbar() {
        supportActionBar?.elevation = 0f
    }
    //kode diatas merupakan fungsi initToolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return super.onCreateOptionsMenu(menu)
    }
//merupakan fungsi tombol untuk menuju ke fragment menu_favorite.xml
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_favorite -> startActivity(
                Intent(
                    this,
                    Class.forName("com.galuhsaputri.favorite.ui.FavoriteUserActivity")
                )
            )
            R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.menu_language -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return super.onOptionsItemSelected(item)
    }
//kode diatas merupakan fungsi untuk mengirimkan data yang merupakan pengguna gihub yang telah
// di favoritkan, dan dikirimkan ke direktori favorit dan dibuatkan MVVM terbaru untuk kelas favorit.
    private fun searchUsers() {
        binding.apply {
            svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        if (it.isNotEmpty()) {
                            items.clear()
                            mainViewModel.getUserFromApi(query)
                            svSearch.clearFocus()
                            setIllustration(false)
                        } else {
                            svSearch.clearFocus()
                            setIllustration(true)
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean = false
            })
            svSearch.setOnCloseListener {
                svSearch.setQuery("", false)
                svSearch.clearFocus()
                mainAdapter.clearItems()
                setIllustration(true)
                true
            }
        }
    }
// kode diatas merupakan fungsi serachuser() yang digubakan untuk mencari pengguna github lainnya.
    private fun initObserver() {
        with(mainViewModel) {
            state.observe(this@MainActivity) {
                it?.let {
                    handleStateLoading(it)
                }
            }
            resultUserApi.observe(this@MainActivity) {
                it?.let {
                    handleUserFromApi(it)
                }
            }
            networkError.observe(this@MainActivity) {
                it?.let {
                    handleStateInternet(it)
                }
            }
        }
    }
//kode diatas untuk memproses data api
    private fun initRecyclerView() {
        binding.rvUser.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = mainAdapter
        }
        mainAdapter.setActivity(this)
    }
// kode diatas merupakan fungsi initrecycleview untuk memanggil adapter.
    private fun handleUserFromApi(result: List<UserSearchItem>) {
        items.clear()
        items.addAll(result)
        mainAdapter.setItems(items)
    }
//fungsi untuk menampilkan pengguna github ketika diklik dan menampilkan hasil.
    private fun handleStateInternet(error: Boolean) {
        with(binding) {
            if (error) {
                baseLoading.root.setVisible()
                rvUser.setGone()
            } else {
                baseLoading.root.setGone()
                rvUser.setVisible()
            }
        }

    }

    private fun handleStateLoading(loading: LoaderState) {
        with(binding) {
            if (loading is LoaderState.ShowLoading) {
                baseLoading.root.setVisible()
                setIllustration(false)
                rvUser.setGone()
            } else {
                baseLoading.root.setGone()
                setIllustration(false)
                rvUser.setVisible()
            }
        }

    }

    private fun setIllustration(status: Boolean) {
        binding.baseEmpty.visibility = if (status) VISIBLE else INVISIBLE
    }
}