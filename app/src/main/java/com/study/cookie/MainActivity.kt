package com.study.cookie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.squareup.moshi.Json
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
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cookieList.collect {
                    it.cookieList.forEach {
                        Log.d("MainActivity", "$it")
                    }
                }
            }
        }
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    testRepository: TestRepository
) : ViewModel() {
    private val _cookieList = MutableStateFlow<CookieInfoList>(
        CookieInfoList(
            cookieList = emptyList(),
            last = 0,
            next = true
        )
    )
    val cookieList = _cookieList.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                while (true) {
                    delay(1000L)
                    cookieList.value.last?.let {
                        val fetchCookieList = testRepository.fetchCookieList(
                            it,
                            length = 4
                        )

                        _cookieList.value = fetchCookieList
                    }
                }
            }
        }
    }
}

@Singleton
class TestRepository @Inject constructor(
    private val cookieService: CookieService
) {
    suspend fun fetchCookieList(start: Int, length: Int): CookieInfoList {
        return cookieService.getCookieInfoList(start, length)
    }
}

@Module
@InstallIn(SingletonComponent::class)
class TestModule {

    @Singleton
    @Provides
    fun provideTestRepository(cookieService: CookieService): TestRepository {
        return TestRepository(cookieService)
    }

    @Singleton
    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String {
        return "http://172.27.176.1:8000"
    }

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        moshiConverterFactory: MoshiConverterFactory,
        @Named("baseUrl") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(moshiConverterFactory)
            .baseUrl(baseUrl)
            .build()
    }

    @Singleton
    @Provides
    fun provideCookieService(retrofit: Retrofit): CookieService {
        return retrofit.create(CookieService::class.java)
    }
}

data class CookieInfoList(
    @field:Json(name = "list")
    val cookieList: List<CookieInfo>,
    @field:Json(name = "last")
    val last: Int?,
    @field:Json(name = "next")
    val next: Boolean
)

data class CookieInfo(
    @field:Json(name = "cookie_id")
    val cookieId: Int,
    @field:Json(name = "cookie_name")
    val cookieName: String,
    @field:Json(name = "cookie_image")
    val cookieImage: String
)

interface CookieService {
    @GET("/cookie/v1/cookie_id/list")
    suspend fun getCookieInfoList(
        @Query("start") start: Int,
        @Query("length") length: Int
    ): CookieInfoList
}