package com.cuneytayyildiz.githubissueexplorer.di.modules

import com.cuneytayyildiz.githubissueexplorer.data.api.GithubService
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository
import com.cuneytayyildiz.githubissueexplorer.ui.details.vm.IssueDetailsViewModelFactory
import com.cuneytayyildiz.githubissueexplorer.ui.main.vm.MainViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideGithubRepository(
        compositeDisposable: CompositeDisposable,
        api: GithubService
    ): GithubRepository {
        return GithubRepository(compositeDisposable, api)
    }

    @Provides
    @Singleton
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Provides
    @Singleton
    fun provideMainViewModelFactory(repoRepository: GithubRepository) =
        MainViewModelFactory(repoRepository)


    @Provides
    @Singleton
    fun provideIssueDetailsViewModelFactory(repoRepository: GithubRepository) =
        IssueDetailsViewModelFactory(repoRepository)

}