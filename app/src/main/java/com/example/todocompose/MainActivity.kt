package com.example.todocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todocompose.data.model.network.TodoApiService
import com.example.todocompose.repository.TodoRepository
import com.example.todocompose.ui.theme.composables.TodoScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://68163b7232debfe95dbdd500.mockapi.io/academic/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TodoApiService::class.java)
        val repository = TodoRepository(apiService)
        val viewModel = TodoViewModel(repository)

        setContent {
            TodoScreen(viewModel)
        }
    }
}
