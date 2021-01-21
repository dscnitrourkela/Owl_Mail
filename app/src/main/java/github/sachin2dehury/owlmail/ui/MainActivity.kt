package github.sachin2dehury.owlmail.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.ActivityMainBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.services.AlarmBroadcast
import github.sachin2dehury.owlmail.ui.viewmodels.MailBoxViewModel
import github.sachin2dehury.owlmail.ui.viewmodels.SettingsViewModel
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var searchView: SearchView
    private lateinit var navController: NavController

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val mailBoxViewModel: MailBoxViewModel by viewModels()

    @Inject
    lateinit var alarmBroadcast: AlarmBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        drawerOptionMenu()

        inAppReview()

        inAppUpdate()

        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }

    private fun subscribeToObservers() {
        settingsViewModel.isDarkThemeEnabled.observe(this, { themeState ->
            themeState?.let {
                debugLog("Theme Changed $it")
                when (it) {
                    true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        })
        settingsViewModel.shouldSync.observe(this, { syncState ->
            syncState?.let {
                debugLog("Sync Changed $it")
                when (it) {
                    true -> alarmBroadcast.startBroadcast()
                    else -> alarmBroadcast.stopBroadcast()
                }
            }
        })
    }

    private fun drawerOptionMenu() {

        navController = findNavController(R.id.navHostFragment)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.apply {
            setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.inboxFragment -> mailBoxViewModel.setRequest(Constants.INBOX_URL)
                    R.id.sentFragment -> mailBoxViewModel.setRequest(Constants.SENT_URL)
                    R.id.draftFragment -> mailBoxViewModel.setRequest(Constants.DRAFT_URL)
                    R.id.junkFragment -> mailBoxViewModel.setRequest(Constants.JUNK_URL)
                    R.id.trashFragment -> mailBoxViewModel.setRequest(Constants.TRASH_URL)
                    R.id.privacyPolicy -> {
                        Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_POLICY_URL))
                        startActivity(intent)
                    }
                    R.id.newFeatures -> {
                        Intent(Intent.ACTION_VIEW, Uri.parse(Constants.NEW_FEATURES_URL))
                        startActivity(intent)
                    }
                    else -> navController.navigate(it.itemId) // R.id.settingsFragment, R.id.aboutFragment
                }
                setCheckedItem(it)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    override fun toggleDrawer(isEnabled: Boolean) {
        val lockMode =
            if (isEnabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
        toggle.isDrawerIndicatorEnabled = isEnabled
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun toggleActionBar(isEnabled: Boolean) {
        if (isEnabled) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
    }

    override fun hideKeyBoard() =
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )

    override fun setSearchView(searchView: SearchView) {
        this.searchView = searchView
    }

    override fun closeSearchView() {
        if (::searchView.isInitialized) {
            searchView.isIconified = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            debugLog(item.toString())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        closeSearchView()
        if (hideKeyBoard()) {
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun inAppReview() {
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

    override fun inAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfo = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener {
            doUpdate(appUpdateManager, appUpdateInfo)
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
            appUpdateManager.startUpdateFlowForResult(
                task.result,
                AppUpdateType.IMMEDIATE,
                this,
                1000
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}