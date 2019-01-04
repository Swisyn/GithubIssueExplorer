package com.cuneytayyildiz.githubissueexplorer.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cuneytayyildiz.githubissueexplorer.R
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.gone
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.timeAgo
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.visible

class MainListAdapter(
    private val clickListener: MainListItemClickListener,
    private var items: MutableList<Issue> = mutableListOf()
) :
    RecyclerView.Adapter<MainListAdapter.ListItemViewHolder>(), Filterable {

    var filterableItems: MutableList<Issue> = items

    override fun getFilter(): Filter {
        val callback = makeTrackerFilterCallback()
        return MainListFilter(
            items,
            callback
        )
    }

    private fun makeTrackerFilterCallback(): IssueFilterCallback {
        return object :
            IssueFilterCallback {
            override fun publishResults(filteredList: MutableList<Issue>) {
                filterableItems = filteredList
                notifyDataSetChanged()
            }
        }
    }

    fun clearItems() {
        items.clear()
        filterableItems.clear()
        notifyDataSetChanged()
    }

    fun addItems(newItems: MutableList<Issue>) {
        items.addAll(newItems)
        filterableItems = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_issue,
                parent,
                false
            )
        )

    override fun getItemCount() = filterableItems.size

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bind(filterableItems[position], clickListener)
    }

    class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textIssueTitle: TextView =
            itemView.findViewById(com.cuneytayyildiz.githubissueexplorer.R.id.text_issue_title)
        private var textCommentCount: TextView =
            itemView.findViewById(com.cuneytayyildiz.githubissueexplorer.R.id.text_comment_count)
        private var imageStatus: ImageView =
            itemView.findViewById(com.cuneytayyildiz.githubissueexplorer.R.id.image_status)
        private var textExtraDetails: TextView =
            itemView.findViewById(com.cuneytayyildiz.githubissueexplorer.R.id.text_extra_details)

        @SuppressLint("SetTextI18n")
        fun bind(item: Issue, clickListener: MainListItemClickListener) = with(itemView) {
            textIssueTitle.text = item.title
            textCommentCount.text = item.comments.toString()
            textExtraDetails.text =
                    "#${item.number} opened an ${item.createdAt?.timeAgo()} by ${item.user?.login} "
            val isOpenIssue = item.state?.equals("open") == false

            imageStatus.setColorFilter(
                if (isOpenIssue) Color.argb(255, 203, 36, 49) else Color.argb(255, 40, 167, 69),
                android.graphics.PorterDuff.Mode.SRC_IN
            )

            if (item.comments > 0) textCommentCount.visible()
            else textCommentCount.gone()

            itemView.setOnClickListener { clickListener.onIssueClick(item, itemView) }
        }
    }
}