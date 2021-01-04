package github.sachin2dehury.nitrmail.others

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task

class PlayCoreExt(private val context: Context) {

    fun inAppReview() {
        val reviewManagerFactory = ReviewManagerFactory.create(context)
        val request = reviewManagerFactory.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                debugLog("Successful Review")
            } else {
                debugLog(task.exception.toString())
            }
        }
    }

    fun inAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(context)
        val appUpdateInfo = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener {

        }
    }

    private fun doUpdate(
        appUpdateManager: AppUpdateManager,
        task: Task<AppUpdateInfo>
    ) {
        if ((task.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || task.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) && task.result.isUpdateTypeAllowed(
                AppUpdateType.IMMEDIATE
            )
        ) {
//            appUpdateManager.startUpdateFlow(task.result, AppUpdateType.IMMEDIATE,)
        }
    }
}