package github.sachin2dehury.owlmail.di.app

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter

@Module
@InstallIn(ActivityRetainedComponent::class)
object AdapterModule {

    @ActivityRetainedScoped
    @Provides
    fun provideMailBoxAdapter(@ApplicationContext context: Context) = MailBoxAdapter(context)

    @ActivityRetainedScoped
    @Provides
    fun provideMailItemsAdapter(@ApplicationContext context: Context) = MailItemsAdapter(context)

}