package github.sachin2dehury.nitrmail.ui

import androidx.appcompat.widget.SearchView

interface ActivityExt {

    fun toggleActionBar(isEnabled: Boolean)

    fun showSnackbar(message: String)

    fun toggleDrawer(isEnabled: Boolean)

    fun hideKeyBoard(): Boolean

    fun setSearchView(searchView: SearchView)

    fun closeSearchView()

    fun startSync()

    fun stopSync()

    fun inAppReview()

    fun inAppUpdate()

    fun toggleTheme()
}