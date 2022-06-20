package com.wk.squarepratice

import android.app.Application
import android.content.Context
import android.util.Log
import com.pgyer.pgyersdk.PgyerSDKManager
import com.wk.squarepratice.util.TAG

class WkApplication : Application() {
    companion object {
        var instance: WkApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initSdks(this)
    }

    private fun initSdks(wkApplication: WkApplication) {
        PgyerSDKManager.Init().setContext(wkApplication).start()
        PgyerSDKManager.setUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "$thread --- $throwable")
        }
    }
}