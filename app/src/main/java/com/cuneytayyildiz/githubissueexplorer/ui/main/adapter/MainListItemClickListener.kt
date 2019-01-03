package com.cuneytayyildiz.githubissueexplorer.ui.main.adapter

import android.view.View
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue

interface MainListItemClickListener {
    fun onIssueClick(item: Issue, view: View)
}