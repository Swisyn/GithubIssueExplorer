package com.cuneytayyildiz.githubissueexplorer.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cuneytayyildiz.githubissueexplorer.IssueExplorerApp
import com.cuneytayyildiz.githubissueexplorer.R
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.data.model.Resource
import com.cuneytayyildiz.githubissueexplorer.ui.details.IssueDetailsActivity
import com.cuneytayyildiz.githubissueexplorer.ui.main.adapter.MainListAdapter
import com.cuneytayyildiz.githubissueexplorer.ui.main.adapter.MainListItemClickListener
import com.cuneytayyildiz.githubissueexplorer.ui.main.vm.MainViewModel
import com.cuneytayyildiz.githubissueexplorer.ui.main.vm.MainViewModelFactory
import com.cuneytayyildiz.githubissueexplorer.utils.InfiniteScrollListener
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.*
import com.cuneytayyildiz.views.multistateview.LinearMultiStateView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    MainListItemClickListener {

    //<editor-fold desc="Views">
    private lateinit var toolbar: Toolbar
    private lateinit var multiStateView: LinearMultiStateView

    private lateinit var fabFilter: FloatingActionButton
    private lateinit var textSearchQuery: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbarSearchContainer: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var buttonSearch: ImageButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var appbarLayout: AppBarLayout
    //</editor-fold>

    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    lateinit var infiniteScrollListener: InfiniteScrollListener
    lateinit var mainViewModel: MainViewModel

    private var mainListAdapter =
        MainListAdapter(this)

    private var canLoadMore = true
    private var isFiltering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IssueExplorerApp.appComponent.inject(this)

        setContentView(R.layout.activity_main)

        initializeViews()

        initializeListeners()

        initViewModel()
    }

    @SuppressLint("CheckResult")
    private fun initializeListeners() {
        setSupportActionBar(toolbar)

        keyboardVisibilityChanges().subscribe {
            isFiltering = it
        }

        infiniteScrollListener = object :
            InfiniteScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (!isFiltering && canLoadMore) {
                    mainViewModel.setPage(page)
                }
            }
        }

        textSearchQuery.afterTextChanged {
            mainListAdapter.filter.filter(it)
        }

        textSearchQuery.onDoneAction {
            textSearchQuery.clearFocus()
            fabFilter.requestFocus()
            isFiltering = false
            hideKeyboard()
        }

        recyclerView.apply {
            adapter = mainListAdapter
            addOnScrollListener(infiniteScrollListener)
        }

        fabFilter.setOnClickListener {
            showFilterDialog()
        }

        swipeRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
    }

    private fun onRefresh() {
        resetItems()
        mainViewModel.listRepositoryIssues()
    }

    private fun showFilterDialog() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(R.string.filter_dialog_title)
            setItems(R.array.issue_states) { dialog, which ->
                dialog.dismiss()

                resetItems()

                mainViewModel.setFilter(state = if (which == 0) "open" else "closed")
            }.show()
        }
    }

    private fun resetItems() {
        mainListAdapter.clearItems()
        infiniteScrollListener.resetState()
    }

    private fun initializeViews() {
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        fabFilter = findViewById<FloatingActionButton>(R.id.fab_filter)
        multiStateView = findViewById<LinearMultiStateView>(R.id.multi_state_view)

        textSearchQuery = findViewById<EditText>(R.id.text_search_query)
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        toolbarSearchContainer = findViewById<Toolbar>(R.id.toolbar_search_container)
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        buttonSearch = findViewById<ImageButton>(R.id.button_search)
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        appbarLayout = findViewById<AppBarLayout>(R.id.appbar_layout)
    }

    private fun initViewModel() {
        if (isConnectedToInternet()) {
            mainViewModel = createViewModel(mainViewModelFactory) {
                listRepositoryIssues(1)

                observe(issuesLiveData) {
                    renderList(it)
                }
            }
        } else {
            showError(
                R.drawable.ic_no_internet_black_48dp,
                getString(R.string.error_no_internet),
                getString(R.string.error_no_internet_description)
            ) {
                initViewModel()
            }
        }
    }

    private fun renderList(result: Resource<MutableList<Issue>>?) {
        result?.let {
            when (it.status) {
                Resource.Status.LOADING -> swipeRefreshLayout.showLoading()
                Resource.Status.SUCCESS -> {
                    if (!multiStateView.isContentCurrentState) multiStateView.showContent()

                    swipeRefreshLayout.hideLoading()

                    it.data?.let { data ->
                        if (data.isNotEmpty()) {
                            canLoadMore = true
                            mainListAdapter.addItems(data)

                        } else {
                            canLoadMore = false
                        }
                    } ?: otherwise {
                        canLoadMore = false
                    }
                }

                Resource.Status.ERROR -> {
                    swipeRefreshLayout.hideLoading()
                    swipeRefreshLayout.snack(
                        it.exception?.message
                            ?: getString(com.cuneytayyildiz.githubissueexplorer.R.string.error_message_unknown)
                    ) { snack ->
                        renderList(it)
                        snack.dismiss()
                    }
                }

            }
        }
    }

    private fun showError(
        @DrawableRes icon: Int, title: String, description: String, action: () -> Unit
    ) {
        multiStateView.showError(
            icon,
            title,
            description,
            getString(R.string.button_retry),
            View.OnClickListener { action() },
            listOf(R.id.fab_filter, R.id.toolbar_search_container)
        )
    }

    override fun onIssueClick(item: Issue, view: View) {
        IssueDetailsActivity.start(this@MainActivity, item, view)
    }
}