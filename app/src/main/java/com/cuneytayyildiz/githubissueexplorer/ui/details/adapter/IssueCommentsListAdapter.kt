package com.cuneytayyildiz.githubissueexplorer.ui.details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cuneytayyildiz.githubissueexplorer.R
import com.cuneytayyildiz.githubissueexplorer.data.model.IssueComment
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.load
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.timeAgo
import ru.noties.markwon.Markwon

class IssueCommentsListAdapter(private var items: MutableList<IssueComment> = mutableListOf()) :
    RecyclerView.Adapter<IssueCommentsListAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(items[position])

    fun addItems(data: List<IssueComment>) {
        this.items.addAll(data)
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageCommenterAvatar =   itemView.findViewById<ImageView>(R.id.image_commenter_avatar)
        private var textComment = itemView.findViewById<TextView>(R.id.text_comment)
        private var textCommentDetails = itemView.findViewById<TextView>(R.id.text_comment_details)

        fun bind(item: IssueComment) = with(itemView) {
            imageCommenterAvatar.load(item.user?.avatarUrl)
            item.body?.let { body -> Markwon.setMarkdown(textComment, body) }
            textCommentDetails.text = "${item.user?.login} commented ${item.createdAt?.timeAgo()} "
        }
    }
}