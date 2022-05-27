package com.wk.squarepratice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blankj.utilcode.util.ToastUtils
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pgyer.pgyersdk.PgyerSDKManager
import com.pgyer.pgyersdk.callback.CheckoutVersionCallBack
import com.pgyer.pgyersdk.model.CheckSoftModel
import com.wk.squarepratice.ui.theme.SquarePraticeTheme
import com.wk.squarepratice.views.*
import com.wk.squarepratice.vm.DialogViewModel

class MainActivity : ComponentActivity() {

    private val dialogVm: DialogViewModel by viewModels()

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
                        ToastUtils.showShort("有新版本${it.buildVersion},${it.buildUpdateDescription}")

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

    override fun onBackPressed() {
        dialogVm.exitEnsureShow = true
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
