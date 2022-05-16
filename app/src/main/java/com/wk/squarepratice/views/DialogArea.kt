package com.wk.squarepratice.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wk.squarepratice.vm.DialogViewModel

@Composable
fun DialogArea(navController: NavController, dialogVm: DialogViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        AnimatedVisibility(visible = dialogVm.exitEnsureShow.value) {
            Dialog(onDismissRequest = { dialogVm.exitEnsureShow.value = false }) {
                Column() {
                    Text(text = "是否确认退出？")
                    Row {
                        Button(onClick = { dialogVm.exitEnsureShow.value = false }) {
                            Text(text = "取消")
                        }
                        Button(onClick = {
                            dialogVm.exitEnsureShow.value = false
                            navController.popBackStack()
                        }) {
                            Text(text = "确定")
                        }
                    }
                }

            }
        }


    }
}