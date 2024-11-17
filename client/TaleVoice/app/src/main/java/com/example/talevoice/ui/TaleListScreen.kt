package com.example.talevoice.ui

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.talevoice.TaleApplication
import com.example.talevoice.viewmodel.TaleListViewModel
import com.example.talevoice.viewmodel.TaleListViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TaleListScreen(){
    val repository = (LocalContext.current.applicationContext as TaleApplication).taleRepository
    val viewModel: TaleListViewModel = viewModel(
        factory = TaleListViewModelFactory(repository)
    )

    val taleList by viewModel.taleList.collectAsState()

    LaunchedEffect(Unit) {
        try {
            val detail = viewModel.getTaleDetail("1")
            Log.d("TaleListScreen", detail.toString())
        } catch (e: Exception) {
            println("Failed to fetch tale detail: ${e.message}")
        }
    }

    LazyColumn {
        items(taleList) { tale ->
            Text(text = tale.title)
        }
    }

}