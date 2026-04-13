package com.example.memomind.di

import android.content.Context
import androidx.room.Room
import com.example.memomind.data.local.MemoMindDatabase
import com.example.memomind.data.local.dao.CardDao
import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.remote.ApiService
import com.example.memomind.data.remote.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Emulator: 10.0.2.2 | Thiết bị thật: 192.168.1.15
    private const val BASE_URL = "http://172.21.2.162:3000/"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MemoMindDatabase {
        return Room.databaseBuilder(
            context,
            MemoMindDatabase::class.java,
            "memomind.db"
        ).build()
    }

    @Provides
    fun provideDeckDao(db: MemoMindDatabase): DeckDao = db.deckDao()

    @Provides
    fun provideCardDao(db: MemoMindDatabase): CardDao = db.cardDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}