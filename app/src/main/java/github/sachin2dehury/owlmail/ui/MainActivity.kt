package github.sachin2dehury.owlmail.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.ActivityMainBinding
import github.sachin2dehury.owlmail.ui.viewmodels.SettingsViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: SettingsViewModel by viewModels()

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
        viewModel.isDarkThemeEnabled.observe(this, { themeState ->
            themeState?.let { enableDarkTheme(it) }
        })
        viewModel.shouldSync.observe(this, { syncState ->
            syncState?.let { enableSyncService(it) }
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
                setCheckedItem(it)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.inboxFragment,
                R.id.sentFragment,
                R.id.draftFragment,
                R.id.junkFragment,
                R.id.trashFragment,
                R.id.settingsFragment,
                R.id.aboutFragment,
                R.id.privacyPolicyFragment,
                R.id.termsAndConditionsFragment,
                R.id.newFeaturesFragment,
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun enableDrawer(shouldEnable: Boolean) {
        binding.drawerLayout.enableDrawer(shouldEnable)
        toggle.enableDrawer(shouldEnable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.root.hideKeyBoard(this)) {
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}