package github.sachin2dehury.nitrmail.ui

interface ActivityExt {

    fun toggleActionBar(isEnabled: Boolean)

    fun showSnackbar(message: String)

    fun toggleDrawer(isEnabled: Boolean)

    fun hideKeyBoard()

    fun startSync()

//    fun stopSync()

    fun inAppReview()

    fun inAppUpdate()

    fun toggleTheme()
}