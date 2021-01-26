package github.sachin2dehury.owlmail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreExt
import github.sachin2dehury.owlmail.repository.SyncRepository
import github.sachin2dehury.owlmail.services.AlarmBroadcast
import github.sachin2dehury.owlmail.services.NotificationExt
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()

    @ServiceScoped
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor) = OkHttpClient.Builder()
        .addInterceptor(basicAuthInterceptor)
        .build()

    @ServiceScoped
    @Provides
    fun provideMailApi(
        okHttpClient: OkHttpClient
    ): MailApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(MailApi::class.java)

    @ServiceScoped
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = DataStoreExt(context)

    @ServiceScoped
    @Provides
    fun provideNotificationExt(@ApplicationContext context: Context) = NotificationExt(context)

    @ServiceScoped
    @Provides
    fun provideAlarmBroadCast(@ApplicationContext context: Context) = AlarmBroadcast(context)

    @ServiceScoped
    @Provides
    fun provideSyncRepository(
        @ApplicationContext context: Context,
        basicAuthInterceptor: BasicAuthInterceptor,
        dataStore: DataStoreExt,
        mailApi: MailApi,
    ) = SyncRepository(basicAuthInterceptor, context, dataStore, mailApi)
}