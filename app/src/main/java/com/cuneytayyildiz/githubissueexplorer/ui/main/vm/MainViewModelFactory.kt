package com.cuneytayyildiz.githubissueexplorer.ui.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository

class MainViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
