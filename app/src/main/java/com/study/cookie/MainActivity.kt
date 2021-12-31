package com.study.cookie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.count.collect {
                    Log.d("MainActivity", "$it")
                }
            }
        }
    }
}



@HiltViewModel
class MainViewModel @Inject constructor(
    testRepository: TestRepository
): ViewModel() {
    private val _count = MutableStateFlow<Int>(0)
    val count = _count.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                val number = testRepository.fetchCount()
                _count.value = number
            }
            launch {
                while (true) {
                    delay(500L)
                    _count.value++
                }
            }
        }
    }
}

class TestRepository {
    suspend fun fetchCount(): Int {
        delay(2000L)
        return 1000
    }
}

@Module
@InstallIn(SingletonComponent::class)
class TestModule {

    @Singleton
    @Provides
    fun provideTestRepository(): TestRepository {
        return TestRepository()
    }
}