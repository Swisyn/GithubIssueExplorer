package com.cuneytayyildiz.githubissueexplorer.data.repository

import androidx.lifecycle.MutableLiveData
import com.cuneytayyildiz.githubissueexplorer.data.api.GithubService
import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.data.model.IssueComment
import com.cuneytayyildiz.githubissueexplorer.data.model.Resource
import com.cuneytayyildiz.githubissueexplorer.utils.DEFAULT_ISSUE_STATE
import com.cuneytayyildiz.githubissueexplorer.utils.extensions.message
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GithubRepository @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val api: GithubService
) {
    private val issuesLiveData = MutableLiveData<Resource<MutableList<Issue>>>()
    private val issueCommentsLiveData = MutableLiveData<Resource<MutableList<IssueComment>>>()

    fun listRepositoryIssues(
        owner: String,
        repository: String,
        page: Int,
        state: String = DEFAULT_ISSUE_STATE
    ): MutableLiveData<Resource<MutableList<Issue>>> {
        compositeDisposable.add(
            api.listRepositoryIssues(owner, repository, page, state)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { issuesLiveData.value = Resource.loading() }
                .doOnError { issuesLiveData.value = Resource.error(it) }
                .subscribe {
                    try {
                        if (it.isSuccessful) {
                            issuesLiveData.value = Resource.success(it.body())
                        } else {
                            issuesLiveData.value =
                                    Resource.error(Throwable(it.errorBody()?.message()))
                        }
                    } catch (e: Exception) {
                        issuesLiveData.value =
                                Resource.error(Throwable(e.message ?: e.localizedMessage))
                    }
                }
        )

        return issuesLiveData
    }

    //<editor-fold desc="TODO">
    // TODO list repositories into spinner
//    fun listRepositories(owner: String, page: Int): MutableLiveData<Resource<MutableList<Repo>>> {
//        compositeDisposable.add(
//            api.listRepositories(owner, page)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { repositoriesLiveData.value = Resource.loading() }
//                .doOnError { repositoriesLiveData.value = Resource.error(it) }
//                .subscribe {
//                    try {
//                        if (it.isSuccessful) {
//                            repositoriesLiveData.value = Resource.success(it.body())
//                        } else {
//                            repositoriesLiveData.value =
//                                    Resource.error(Throwable(it.errorBody()?.message()))
//                        }
//                    } catch (e: Exception) {
//                        repositoriesLiveData.value =
//                                Resource.error(Throwable(e.message ?: e.localizedMessage))
//                    }
//                }
//        )
//
//        return repositoriesLiveData
//    }
    //</editor-fold>

    fun listIssueComments(
        owner: String,
        repository: String,
        issueNumber: Int,
        page: Int
    ): MutableLiveData<Resource<MutableList<IssueComment>>> {
        compositeDisposable.add(
            api.listIssueComments(owner, repository, issueNumber, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { issueCommentsLiveData.value = Resource.loading() }
                .doOnError { issueCommentsLiveData.value = Resource.error(it) }
                .subscribe {
                    try {
                        if (it.isSuccessful) {
                            issueCommentsLiveData.value = Resource.success(it.body())
                        } else {
                            issueCommentsLiveData.value =
                                    Resource.error(Throwable(it.errorBody()?.message()))
                        }
                    } catch (e: Exception) {
                        issueCommentsLiveData.value =
                                Resource.error(Throwable(e.message ?: e.localizedMessage))
                    }
                }
        )

        return issueCommentsLiveData
    }

    fun clearCompositeDisposable() {
        compositeDisposable.clear()
    }
}