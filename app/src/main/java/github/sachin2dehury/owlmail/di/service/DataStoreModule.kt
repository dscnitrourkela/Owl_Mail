package github.sachin2dehury.owlmail.di.service

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import github.sachin2dehury.owlmail.repository.DataStoreExt

@Module
@InstallIn(ServiceComponent::class)
object DataStoreModule {

    @ServiceScoped
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = DataStoreExt(context)

}