package com.cuneytayyildiz.githubissueexplorer.data.api

import com.cuneytayyildiz.githubissueexplorer.data.model.Issue
import com.cuneytayyildiz.githubissueexplorer.data.model.IssueComment
import com.cuneytayyildiz.githubissueexplorer.data.model.Repo
import com.cuneytayyildiz.githubissueexplorer.utils.DEFAULT_ISSUE_STATE
import com.cuneytayyildiz.githubissueexplorer.utils.ITEM_FETCH_PER_PAGE
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_FETCH_PER_PAGE
import com.cuneytayyildiz.githubissueexplorer.utils.REPOSITORY_OWNER
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("repos/{owner}/{repository}/issues") 
    fun listRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repository") repository: String,
        @Query("page") page: Int,
        @Query("state") state: String = DEFAULT_ISSUE_STATE,
        @Query("per_page") perPage: Int = ITEM_FETCH_PER_PAGE

    ): Observable<Response<MutableList<Issue>>>


    @GET("users/{owner}/repos")
    fun listRepositories(
        @Path("owner") owner: String = REPOSITORY_OWNER,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = REPOSITORY_FETCH_PER_PAGE
    ): Observable<Response<MutableList<Repo>>>


    @GET("repos/{owner}/{repository}/issues/{number}/comments")
    fun listIssueComments(
        @Path("owner") owner: String,
        @Path("repository") repository: String,
        @Path("number") number: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = REPOSITORY_FETCH_PER_PAGE
    ): Observable<Response<MutableList<IssueComment>>>
}