package com.example.zedgeimagelist.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zedgeimagelist.R
import com.example.zedgeimagelist.databinding.ActivityMainBinding
import com.example.zedgeimagelist.model.ImageListResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.scope.emptyState
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModel(state = emptyState()) {
        parametersOf(intent.getIntExtra("test", 6))
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    private var listJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupList()

        model.favoriteListState.observe(this) {
            setList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (model.favoriteListState.value == true) {
            menu?.findItem(R.id.action_favorite)?.setIcon(R.drawable.ic_action_favorite)
        } else {
            menu?.findItem(R.id.action_favorite)?.setIcon(R.drawable.ic_action_favorite_empty)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> {
            model.toggleFavoriteList()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupList() {
        adapter = MainAdapter(arrayListOf()) {
            model.listRetry()
        }

        val layoutManager = LinearLayoutManager(this)

        binding.list.layoutManager = layoutManager
        binding.list.adapter = adapter

        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                model.listScrolled(lastVisibleItem, totalItemCount)
            }
        })
    }

    private fun setList(favored: Boolean) {
        listJob?.cancel()
        listJob = lifecycleScope.launch {
            model.imageList(favored).collect { result ->
                when (result) {
                    is ImageListResult.Success -> {
                        adapter.setImages(result.data)
                    }
                    is ImageListResult.Error -> {
                        adapter.setImages(result.data, MainAdapter.Status.Error)
                    }
                    is ImageListResult.Loading -> {
                        adapter.setImages(result.data, MainAdapter.Status.Loading)
                    }
                }

                binding.list.visibility = View.VISIBLE
                binding.progress.visibility = View.GONE
            }
        }

        invalidateOptionsMenu()
    }
}