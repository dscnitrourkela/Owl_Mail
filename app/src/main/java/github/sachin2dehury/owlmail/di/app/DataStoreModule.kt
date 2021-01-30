package github.sachin2dehury.owlmail.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import github.sachin2dehury.owlmail.repository.DataStoreExt

@Module
@InstallIn(ActivityRetainedComponent::class)
object DataStoreModule {

    @ActivityRetainedScoped
    @Provides
    fun provideDataStore(@ApplicationContext context: Context) = DataStoreExt(context)

}