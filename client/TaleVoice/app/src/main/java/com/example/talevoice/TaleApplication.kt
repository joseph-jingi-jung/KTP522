package com.example.talevoice

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.talevoice.data.DefaultTaleRepository
import com.example.talevoice.data.TaleRepository
import com.example.talevoice.data.source.local.TaleDatabase
import com.example.talevoice.data.source.server.RetryInterceptor
import com.example.talevoice.data.source.server.TTSApiService
import com.example.talevoice.data.source.server.TaleApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

class TaleApplication : Application() {

    private val database: TaleDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaleDatabase::class.java,
            "tale_database"
        ).build()
    }

    lateinit var taleRepository: TaleRepository
        private set

    lateinit var  ttsApiService: TTSApiService
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d("TaleApplication","tale application on create")


        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 시간 초과 설정
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 시간 초과 설정
            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 시간 초과 설정
            .addInterceptor(loggingInterceptor)
            .addInterceptor(RetryInterceptor(3))
            .build()

        val apiService: TaleApiService = Retrofit.Builder()
            .baseUrl(BuildConfig.WAS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TaleApiService::class.java)

        // Repository 초기화
        taleRepository = DefaultTaleRepository(
            localDataSource = database.taleDao(),
            networkApiService = apiService,
            dispatcher = Dispatchers.IO
        )

        ttsApiService = Retrofit.Builder()
            .baseUrl("https://koreacentral.tts.speech.microsoft.com")
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(TTSApiService::class.java)
    }



}