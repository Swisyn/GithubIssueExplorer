package com.cuneytayyildiz.githubissueexplorer.ui.details

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuneytayyildiz.githubissueexplorer.IssueExplorerApp
import com.cuneytayyildiz.githubissueexplorer.R
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.data.model.IssueComment
import com.cuneytayyildiz.githubissueexplorer.data.model.Resource
import com.cuneytayyildiz.githubissueexplorer.ui.details.adapter.IssueCommentsListAdapter
import com.cuneytayyildiz.githubissueexplorer.ui.details.vm.IssueDetailsViewModel
import com.cuneytayyildiz.githubissueexplorer.ui.details.vm.IssueDetailsViewModelFactory
import com.cuneytayyildiz.githubissueexplorer.utils.InfiniteScrollListener
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_NAME
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.*
import ru.noties.markwon.Markwon
import javax.inject.Inject
import android.util.Pair as UtilPair

class IssueDetailsActivity : AppCompatActivity() {

    //<editor-fold desc="Views">
    private lateinit var textBody: TextView
    private lateinit var imageAuthorAvatar: ImageView
    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var textIssueStatus: TextView
    private lateinit var cardIssueStatus: CardView

    private lateinit var textIssueExtraDetails: TextView
    private lateinit var textIssueTitle: TextView
    private lateinit var recyclerView: RecyclerView
    //</editor-fold>

    @Inject
    lateinit var issueDetailsViewModelFactory: IssueDetailsViewModelFactory

    private lateinit var infiniteScrollListener: InfiniteScrollListener
    lateinit var issueDetailsViewModel: IssueDetailsViewModel

    private var commentsListAdapter: IssueCommentsListAdapter =
        IssueCommentsListAdapter()

    private var canLoadMore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IssueExplorerApp.appComponent.inject(this@IssueDetailsActivity)

        setContentView(R.layout.activity_issue_details)

        initializeViews()

        fillDetails()
    }

    private fun fillDetails() {
        if (intent.hasExtra(EXTRA_ISSUE_DETAILS)) {
            intent?.extras?.getParcelable<Issue>(EXTRA_ISSUE_DETAILS)?.let {

                when (it.state) {
                    "open" -> cardIssueStatus.setCardBackgroundColor(color(R.color.color_green))
                    "closed" -> cardIssueStatus.setCardBackgroundColor(color(R.color.color_red))
                }

                textIssueStatus.text = it.state?.capitalize()
                imageAuthorAvatar.load(it.user?.avatarUrl)

                textIssueTitle.text = getStyledTitle(it).toString()
                textIssueExtraDetails.text = getString(
                    R.string.issue_title_placeholder,
                    it.user?.login,
                    it.createdAt?.timeAgo(),
                    it.comments
                )

                it.body?.let { body -> Markwon.setMarkdown(textBody, body) }

                initViewModel(it)
            }

        } else finish()
    }

    private fun getStyledTitle(it: Issue): SpannableString {
        val issueNumber = getString(R.string.issue_number_placeholder, it.number)

        val styledTitle = SpannableString(it.title + issueNumber)
        styledTitle.setSpan(
            ForegroundColorSpan(Color.GRAY),
            styledTitle.length - issueNumber.length,
            styledTitle.length,
            0
        )
        return styledTitle
    }

    private fun initViewModel(issue: Issue) {
        if (isConnectedToInternet()) {
            issueDetailsViewModel = createViewModel(issueDetailsViewModelFactory) {
                setRepositoryName(REPOSITORY_NAME)
                setIssueNumber(issue.number)
                setPage(1)

                observe(issueCommentsLiveData) {
                    renderList(it)
                }
            }
        } else {
            coordinatorLayout.snack(getString(com.cuneytayyildiz.githubissueexplorer.R.string.error_comments_not_loaded)) {
                initViewModel(issue)
                it.dismiss()
            }
        }
    }

    private fun initializeViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textIssueStatus = findViewById<TextView>(R.id.text_issue_status)
        cardIssueStatus = findViewById<CardView>(R.id.card_issue_status)
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)

        imageAuthorAvatar = findViewById<ImageView>(R.id.image_author_avatar)

        textBody =
                findViewById<TextView>(R.id.text_body)
        textIssueExtraDetails =
                findViewById<TextView>(R.id.text_issue_extra_details)
        textIssueTitle =
                findViewById<TextView>(R.id.text_issue_title)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        infiniteScrollListener = object :
            InfiniteScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (canLoadMore) {
                    issueDetailsViewModel.setPage(page)
                }
            }
        }

        recyclerView.apply {
            adapter = commentsListAdapter
            addOnScrollListener(infiniteScrollListener)
        }
    }

    private fun renderList(result: Resource<MutableList<IssueComment>>?) {
        result?.let {
            if (it.status == Resource.Status.SUCCESS) {
                it.data?.let { data ->
                    if (data.isNotEmpty()) {
                        canLoadMore = true
                        commentsListAdapter.addItems(data)
                    } else {
                        canLoadMore = false
                    }
                } ?: otherwise {
                    canLoadMore = false
                }
            } else if (it.status == Resource.Status.ERROR) {
                recyclerView.gone()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_ISSUE_DETAILS = "extra_issue_details"

        fun start(context: Context, issue: Issue, itemView: View) {
            val intent = Intent(
                context,
                IssueDetailsActivity::class.java
            ).apply {
                putExtra(EXTRA_ISSUE_DETAILS, issue)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    context as AppCompatActivity,
                    UtilPair.create(itemView, context.getString(R.string.transition_issue_title)),
                    UtilPair.create(
                        itemView,
                        context.getString(R.string.transition_issue_extra_details)
                    )
                )

                context.startActivity(intent, options.toBundle())
            } else {
                (context as AppCompatActivity).startActivity(intent)
            }
        }
    }
}
