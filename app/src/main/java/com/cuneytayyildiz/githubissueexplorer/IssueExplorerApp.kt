package com.cuneytayyildiz.githubissueexplorer

import android.app.Application
import com.cuneytayyildiz.githubissueexplorer.di.components.AppComponent


class IssueExplorerApp : Application() {

    companion object {
        //JvmStatic allow access it from java code
        @JvmStatic
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent.create(this)

//        Optional:
//        3. Implement a way to select the git repository TODO
    }
}