package com.cuneytayyildiz.githubissueexplorer.ui.main.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.data.model.Resource
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository
import com.cuneytayyildiz.githubissueexplorer.utils.DEFAULT_ISSUE_STATE
import com.cuneytayyildiz.githubissueexplorer.utils.DoubleTrigger
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_NAME
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_OWNER
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainViewModel @Inject constructor(private val repository: GithubRepository) : ViewModel() {

    private val repositoryName: MutableLiveData<String> = MutableLiveData()
    private val filterState: MutableLiveData<String> = MutableLiveData()
    private val currentPage: MutableLiveData<Int> = MutableLiveData()

    val issuesLiveData: LiveData<Resource<MutableList<Issue>>> =
        Transformations.switchMap(DoubleTrigger(repositoryName, currentPage)) {
            if (it.first?.isNotEmpty() == true) {
                repository.listRepositoryIssues(
                    REPOSITORY_OWNER, it.first ?: REPOSITORY_NAME, it.second
                        ?: 1, filterState.value ?: DEFAULT_ISSUE_STATE
                )
            } else {
                MutableLiveData()
            }
        }

    // TODO
    fun setRepositoryName(repoName: String) {
        this.repositoryName.value = repoName
        setPage(1)
    }

    fun setPage(page: Int) {
        this.currentPage.value = page
    }

    fun setFilter(state: String) {
        this.filterState.value = state
        setPage(1)
    }

    fun listRepositoryIssues(
        page: Int = 1,
        state: String = DEFAULT_ISSUE_STATE,
        repositoryName: String = REPOSITORY_NAME

    ) {
        this.currentPage.value = page
        this.repositoryName.value = repositoryName
        this.filterState.value = state
    }

    //fun listRepositories() = repository.listRepositories()

    override fun onCleared() {
        repository.clearCompositeDisposable()
        super.onCleared()
    }
}
