package com.cuneytayyildiz.githubissueexplorer.di.components

import android.app.Application
import com.cuneytayyildiz.githubissueexplorer.IssueExplorerApp
import com.cuneytayyildiz.githubissueexplorer.data.repository.GithubRepository
import com.cuneytayyildiz.githubissueexplorer.di.modules.AppModule
import com.cuneytayyildiz.githubissueexplorer.di.modules.NetModule
import com.cuneytayyildiz.githubissueexplorer.di.modules.RepositoryModule
import com.cuneytayyildiz.githubissueexplorer.ui.details.IssueDetailsActivity
import com.cuneytayyildiz.githubissueexplorer.ui.main.MainActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
        modules = [AppModule::class, NetModule::class, RepositoryModule::class]
)
interface AppComponent {
    fun inject(mainActivity: MainActivity)

    fun inject(detailsActivity: IssueDetailsActivity)

    fun provideGithubRepository(): GithubRepository

    companion object Factory{
        fun create(app: Application): AppComponent {
            val appComponent = DaggerAppComponent.builder().
                    appModule(AppModule(app as IssueExplorerApp)).
                    netModule(NetModule()).
                    repositoryModule(RepositoryModule()).
                    build();
            return appComponent
        }
    }
}