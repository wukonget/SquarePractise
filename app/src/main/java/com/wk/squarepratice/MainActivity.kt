package com.wk.squarepratice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.azhon.appupdate.manager.DownloadManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pgyer.pgyersdk.PgyerSDKManager
import com.pgyer.pgyersdk.callback.CheckoutVersionCallBack
import com.pgyer.pgyersdk.model.CheckSoftModel
import com.wk.squarepratice.ui.theme.SquarePraticeTheme
import com.wk.squarepratice.views.ExitDialog
import com.wk.squarepratice.views.PlayArea
import com.wk.squarepratice.views.PlayRecordList
import com.wk.squarepratice.vm.DialogViewModel
import com.wk.squarepratice.vm.MainViewModel

class MainActivity : ComponentActivity() {

    private val dialogVm: DialogViewModel by viewModels()
    private val mainVm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SquarePraticeTheme {

                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setStatusBarColor(Color.Transparent, false)
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = SquarePraticeTheme.colors.background,
                    ) {
                        MainPage{
                            this@MainActivity.finish()
                        }
                    }

            }
        }
        checkUpdate()
    }


    private fun checkUpdate() {
        PgyerSDKManager.checkSoftwareUpdate(this, object : CheckoutVersionCallBack {
            override fun onSuccess(p0: CheckSoftModel?) {
                p0?.let {
                    if (it.isBuildHaveNewVersion) {
//                        ToastUtils.showShort("有新版本${it.buildVersion},${it.buildUpdateDescription}")
                        downloadApk(p0)
                    } else {
                        ToastUtils.showShort("已经是最新版了")
                    }
                }
            }

            override fun onFail(p0: String?) {
                ToastUtils.showShort("获取更新失败!${p0 ?: ""}")
            }

        })
    }

    private fun downloadApk(info: CheckSoftModel) {
        val manager = DownloadManager.Builder(this).run {
            apkUrl(info.downloadURL)
            apkName("update-${info.buildVersion}.apk")
            smallIcon(R.drawable.ic_launcher_foreground)
//            //设置了此参数，那么内部会自动判断是否需要显示更新对话框，否则需要自己判断是否需要更新
            apkVersionCode(AppUtils.getAppVersionCode() + 1)
            //同时下面三个参数也必须要设置
            apkVersionName(info.buildVersion)
            apkSize("")
            apkDescription(info.buildUpdateDescription)
            //省略一些非必须参数...
            build()
        }
        manager.download()
    }

    override fun onBackPressed() {
        if (mainVm.showScoreList) {
            mainVm.showScoreList(false)
        } else {
            dialogVm.exitEnsureShow = true
        }
    }
}


@Composable
fun MainPage(finish:()->Unit) {
    val dialogVm : DialogViewModel = viewModel()
    Column() {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(SquarePraticeTheme.colors.background)
        ) {
            PlayArea()

            PlayRecordList()

            if(dialogVm.exitEnsureShow){
                ExitDialog ("确定退出么？",finish){
                    dialogVm.exitEnsureShow = false
                }
            }
        }
    }

}
