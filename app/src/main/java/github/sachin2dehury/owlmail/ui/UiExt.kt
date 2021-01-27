package github.sachin2dehury.owlmail.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.services.SyncService

fun AppCompatActivity.enableActionBar(shouldEnable: Boolean) = when (shouldEnable) {
    true -> this.supportActionBar?.show()
    false -> this.supportActionBar?.hide()
}

fun AppCompatActivity.enableSyncService(shouldEnable: Boolean) {
    val syncIntent = Intent(this, SyncService::class.java)
    when (shouldEnable) {
        true -> startService(syncIntent)
        else -> stopService(syncIntent)
    }
}

fun AppCompatActivity.showToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.inAppReview() {
    val reviewManager = ReviewManagerFactory.create(this)
    val request = reviewManager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(this, reviewInfo)
            flow.addOnCompleteListener {
                debugLog("Successful Review")
            }
        } else {
            debugLog(task.exception.toString())
        }
    }
}

fun AppCompatActivity.inAppUpdate() {
    val appUpdateManager = AppUpdateManagerFactory.create(this)
    val appUpdateInfo = appUpdateManager.appUpdateInfo
    appUpdateInfo.addOnSuccessListener {
        this.doUpdate(appUpdateManager, appUpdateInfo)
    }
}

fun AppCompatActivity.doUpdate(
    appUpdateManager: AppUpdateManager,
    task: Task<AppUpdateInfo>
) {
    if ((task.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || task.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) && task.result.isUpdateTypeAllowed(
            AppUpdateType.IMMEDIATE
        )
    ) {
        appUpdateManager.startUpdateFlowForResult(
            task.result,
            AppUpdateType.IMMEDIATE,
            this,
            1000
        )
    }
}

fun DrawerLayout.enableDrawer(shouldEnable: Boolean) {
    val lockMode =
        if (shouldEnable) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    this.setDrawerLockMode(lockMode)
}

fun ActionBarDrawerToggle.enableDrawer(shouldEnable: Boolean) {
    this.isDrawerIndicatorEnabled = false
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.hideKeyBoard(context: Context) =
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        this.windowToken,
        0
    )

fun enableDarkTheme(shouldEnable: Boolean) = when (shouldEnable) {
    true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
}