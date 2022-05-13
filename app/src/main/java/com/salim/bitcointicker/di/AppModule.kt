package com.salim.bitcointicker.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.salim.bitcointicker.R
import com.salim.bitcointicker.db.CoinDao
import com.salim.bitcointicker.db.CoinDatabase
import com.salim.bitcointicker.repository.firebase.BaseAuthenticator
import com.salim.bitcointicker.repository.firebase.FirebaseAuthenticator
import com.salim.bitcointicker.repository.AuthRepository
import com.salim.bitcointicker.repository.BaseAuthRepository
import com.salim.bitcointicker.repository.CoinRepository
import com.salim.bitcointicker.repository.CoinRepositoryInterface
import com.salim.bitcointicker.network.api.CoinApiService
import com.salim.bitcointicker.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCollectionReference(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideAuthenticator() : BaseAuthenticator {
        return FirebaseAuthenticator()
    }

    @Singleton
    @Provides
    fun provideRepository(authenticator : BaseAuthenticator) : BaseAuthRepository {
        return AuthRepository(authenticator)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS) // connect timeout
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinService(retrofit: Retrofit): CoinApiService {
        return retrofit.create(CoinApiService::class.java)
    }

    @Singleton
    @Provides
    fun injectRoomDatabase(
        @ApplicationContext context: Context) = Room.databaseBuilder(
        context, CoinDatabase::class.java, "coinsDB"
    ).build()

    @Singleton
    @Provides
    fun injectDao(database: CoinDatabase) = database.coinDao()

    @Singleton
    @Provides
    fun injectGlide(@ApplicationContext context: Context) = Glide.with(context)
        .setDefaultRequestOptions(
            RequestOptions().placeholder(R.drawable.ic_currency_bitcoin)
                .error(R.drawable.ic_currency_bitcoin)
        )

    @Singleton
    @Provides
    fun injectNormalRepo(api: CoinApiService, dao: CoinDao) = CoinRepository(api, dao) as CoinRepositoryInterface
}
