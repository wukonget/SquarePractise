package com.wk.squarepratice.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {

    var exitEnsureShow by mutableStateOf(false)

}