package github.sachin2dehury.owlmail.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.AttachmentAdapter
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.calls.MailViewClient

@Module
@InstallIn(ActivityRetainedComponent::class)
object AdapterModule {

    @ActivityRetainedScoped
    @Provides
    fun provideMailBoxAdapter(colors: IntArray) = MailBoxAdapter(colors)

    @ActivityRetainedScoped
    @Provides
    fun provideMailItemsAdapter(colors: IntArray, attachmentAdapter: AttachmentAdapter) =
        MailItemsAdapter(colors, attachmentAdapter)

    @ActivityRetainedScoped
    @Provides
    fun providesAttachmentAdapter() = AttachmentAdapter()

    @ActivityRetainedScoped
    @Provides
    fun provideMailViewClient() = MailViewClient()

    @ActivityRetainedScoped
    @Provides
    fun provideColorList(@ApplicationContext context: Context) =
        context.resources.getIntArray(R.array.colors)

}