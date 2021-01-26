package github.sachin2dehury.owlmail.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.api.database.MailDatabase
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreExt
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @ActivityScoped
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @ActivityScoped
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor) = OkHttpClient.Builder()
        .addInterceptor(basicAuthInterceptor)
        .build()

    @ActivityScoped
    @Provides
    fun provideMailApi(
        okHttpClient: OkHttpClient
    ): MailApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(MailApi::class.java)

    @ActivityScoped
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = DataStoreExt(context)

    @ActivityScoped
    @Provides
    fun provideMailViewClient() = MailViewClient()

    @ActivityScoped
    @Provides
    fun provideMailBoxAdapter(@ApplicationContext context: Context) = MailBoxAdapter(context)

    @ActivityScoped
    @Provides
    fun provideMailItemsAdapter(@ApplicationContext context: Context) = MailItemsAdapter(context)

    @ActivityScoped
    @Provides
    fun provideMailDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, MailDatabase::class.java, Constants.DATABASE_NAME)
        .fallbackToDestructiveMigration().build()

    @ActivityScoped
    @Provides
    fun provideMailDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()

    @ActivityScoped
    @Provides
    fun provideMailRepository(
        @ApplicationContext context: Context,
        basicAuthInterceptor: BasicAuthInterceptor,
        mailApi: MailApi,
        mailDao: MailDao,
    ) = MailRepository(basicAuthInterceptor, context, mailApi, mailDao)

    @ActivityScoped
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context,
        dataStore: DataStoreExt,
    ) = DataStoreRepository(context, dataStore)
}