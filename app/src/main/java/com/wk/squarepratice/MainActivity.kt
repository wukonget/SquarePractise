package com.wk.squarepratice

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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

    val dialogVm: DialogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            SquarePraticeTheme {
                // A surface container using the 'background' color from the theme
                ProvideWindowInsets {

                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setStatusBarColor(Color.Transparent, false)
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background,
                    ) {
                        Nav()
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
        dialogVm.exitEnsureShow.value = true
        Log.e("peng", "onback::${dialogVm.exitEnsureShow.value}")
    }
}


@Composable
fun Nav() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainPage(navController) }
    }
}

@Composable
fun MainPage(navController: NavController) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )

            LifeArea()

            LevelSelect()

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            )


            TimeSeconds()

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
            )

            SquareGrid(modifier = Modifier.fillMaxWidth(0.9f))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .weight(1.0f)
            )

            Text(
                text = "v ${BuildConfig.VERSION_NAME}",
                style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }

        DialogArea(navController)
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SquarePraticeTheme {
        Nav()
    }
}