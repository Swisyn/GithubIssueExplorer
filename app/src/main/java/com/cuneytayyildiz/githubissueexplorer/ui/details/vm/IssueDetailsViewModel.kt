package com.cuneytayyildiz.githubissueexplorer.ui.details.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.cuneytayyildiz.githubissueexplorer.data.model.IssueComment
import com.cuneytayyildiz.githubissueexplorer.data.model.Resource
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_OWNER
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueDetailsViewModel @Inject constructor(private val repository: GithubRepository) : ViewModel() {

    private val repositoryName: MutableLiveData<String> = MutableLiveData()
    private val issueNumber: MutableLiveData<Int> = MutableLiveData()
    private val currentPage: MutableLiveData<Int> = MutableLiveData()

    val issueCommentsLiveData: LiveData<Resource<MutableList<IssueComment>>> =
        Transformations.switchMap(currentPage) {
            repositoryName.value?.let { repositoryName ->
                issueNumber.value?.let { issueNumber ->
                    repository.listIssueComments(REPOSITORY_OWNER, repositoryName, issueNumber, it)
                }
            }

        }

    fun setRepositoryName(repositoryName: String) {
        this.repositoryName.value = repositoryName
    }

    fun setIssueNumber(issueNumber: Int) {
        this.issueNumber.value = issueNumber
    }

    fun setPage(page: Int) {
        this.currentPage.value = page
    }

    override fun onCleared() {
        repository.clearCompositeDisposable()
        super.onCleared()
    }
}
