package github.sachin2dehury.nitrmail.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.ActivityMainBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.debugLog
import github.sachin2dehury.nitrmail.services.AlarmBroadcast
import github.sachin2dehury.nitrmail.ui.viewmodels.ThemeViewModel
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var alarmBroadcast: AlarmBroadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.readThemeState()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerOptionMenu()

        inAppUpdate()

        Firebase.analytics.setAnalyticsCollectionEnabled(true)

        startSync()
    }

    private fun subscribeToObservers() {
        viewModel.themeState.observe(this, { themeState ->
            themeState?.let {
                when (it) {
                    Constants.LIGHT_THEME -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            viewModel.saveThemeState()
        })
    }

    private fun drawerOptionMenu() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val navController = findNavController(R.id.navHostFragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.inboxFragment,
                R.id.sentFragment,
                R.id.draftFragment,
                R.id.junkFragment,
                R.id.trashFragment,
                R.id.settingsFragment,
                R.id.aboutFragment,
                R.id.aboutFragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.theme_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.theme -> toggleTheme()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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

    override fun hideKeyBoard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    override fun startSync() {
        alarmBroadcast.broadcastSync()
        debugLog("startSync Main Activity")
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

    override fun toggleTheme() {
        when (viewModel.themeState.value) {
            Constants.DARK_THEME -> viewModel.setThemeState(Constants.LIGHT_THEME)
            else -> viewModel.setThemeState(Constants.DARK_THEME)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}