package com.cuneytayyildiz.githubissueexplorer.ui.details.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository

class IssueDetailsViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IssueDetailsViewModel::class.java)) {
            return IssueDetailsViewModel(
                repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
