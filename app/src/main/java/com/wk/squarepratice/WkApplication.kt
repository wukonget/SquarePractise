package com.wk.squarepratice

import android.app.Application
import android.content.Context
import android.util.Log
import com.pgyer.pgyersdk.PgyerSDKManager
import com.wk.squarepratice.util.TAG

class WkApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initPgyerSdk(this)
    }

    private fun initPgyerSdk(wkApplication: WkApplication) {
        PgyerSDKManager.Init().setContext(wkApplication).start()
        PgyerSDKManager.setUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "${thread.toString()} --- ${throwable.toString()}")
        }
    }
}