package com.cuneytayyildiz.githubissueexplorer.di.modules

import com.cuneytayyildiz.githubissueexplorer.IssueExplorerApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton



@Module
class AppModule(val app: IssueExplorerApp) {

    @Provides
    @Singleton
    fun provideApplication(): IssueExplorerApp = app


}