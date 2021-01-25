package github.sachin2dehury.owlmail.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()
//
//    @Singleton
//    @Provides
//    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor) = OkHttpClient.Builder()
//        .addInterceptor(basicAuthInterceptor)
//        .build()
//
//    @Singleton
//    @Provides
//    fun provideMailApi(
//        okHttpClient: OkHttpClient
//    ): MailApi = Retrofit.Builder()
//        .baseUrl(Constants.BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(okHttpClient)
//        .build()
//        .create(MailApi::class.java)
//
//    @Singleton
//    @Provides
//    fun provideDataStore(@ApplicationContext context: Context) = DataStoreExt(context)
}