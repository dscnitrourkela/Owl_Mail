package github.sachin2dehury.owlmail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdapterModule {

    @Singleton
    @Provides
    fun provideMailBoxAdapter(colors: IntArray) = MailBoxAdapter(colors)

    @Singleton
    @Provides
    fun provideMailItemsAdapter(colors: IntArray, mailViewClient: MailViewClient) =
        MailItemsAdapter(colors, mailViewClient)

    @Singleton
    @Provides
    fun provideMailViewClient() = MailViewClient()

    @Singleton
    @Provides
    fun provideColorList(@ApplicationContext context: Context) =
        context.resources.getIntArray(R.array.colors)
}