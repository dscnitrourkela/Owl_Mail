package github.sachin2dehury.nitrmail.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
//    @ServiceScoped
//    @Provides
//    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()
//
//    @ServiceScoped
//    @Provides
//    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor) = OkHttpClient.Builder()
//        .addInterceptor(basicAuthInterceptor)
//        .build()
//
//    @ServiceScoped
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
//    @ServiceScoped
//    @Provides
//    fun provideMailDatabase(
//        @ApplicationContext context: Context
//    ) = Room.databaseBuilder(context, MailDatabase::class.java, Constants.DATABASE_NAME)
//        .fallbackToDestructiveMigration().build()
//
//    @ServiceScoped
//    @Provides
//    fun provideMailDao(mailDatabase: MailDatabase) = mailDatabase.getMailDao()
//
//    @ServiceScoped
//    @Provides
//    fun provideDataStore(
//        @ApplicationContext context: Context
//    ) = DataStoreExt(context)
//
//    @ServiceScoped
//    @Provides
//    fun provideNetworkBoundResource() = NetworkBoundResource()
//
//    @ServiceScoped
//    @Provides
//    fun provideInternetChecker() = InternetChecker()
}