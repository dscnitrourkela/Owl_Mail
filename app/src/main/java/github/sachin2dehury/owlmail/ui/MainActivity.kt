package github.sachin2dehury.owlmail.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.ActivityMainBinding
import github.sachin2dehury.owlmail.others.Constants

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerOptionMenu()

        inAppReview()

        inAppUpdate()

        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }

    private fun drawerOptionMenu() {

        navController = findNavController(R.id.navHostFragment)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.apply {
            setCheckedItem(R.id.inboxFragment)
            setNavigationItemSelectedListener {
                navController.navigate(getNavGraphDirections(it.itemId))
                setCheckedItem(it)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getNavGraphDirections(itemId: Int) = when (itemId) {
        R.id.inboxFragment -> NavGraphDirections.actionToMailBoxFragment(Constants.INBOX_URL)
        R.id.sentFragment -> NavGraphDirections.actionToMailBoxFragment(Constants.SENT_URL)
        R.id.draftFragment -> NavGraphDirections.actionToMailBoxFragment(Constants.DRAFT_URL)
        R.id.junkFragment -> NavGraphDirections.actionToMailBoxFragment(Constants.JUNK_URL)
        R.id.trashFragment -> NavGraphDirections.actionToMailBoxFragment(Constants.TRASH_URL)
        R.id.settingsFragment -> NavGraphDirections.actionToSettingsFragment()
        R.id.aboutFragment -> NavGraphDirections.actionToWebViewFragment(Constants.PRIVACY_POLICY)
        R.id.newFeaturesFragment -> NavGraphDirections.actionToWebViewFragment(Constants.NEW_FEATURES)
        R.id.privacyPolicyFragment -> NavGraphDirections.actionToWebViewFragment(
            Constants.PRIVACY_POLICY
        )
        R.id.termsAndConditionsFragment -> NavGraphDirections.actionToWebViewFragment(
            Constants.TERMS_AND_CONDITIONS
        )
        else -> NavGraphDirections.actionToAuthFragment()
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
        if (binding.root.hideKeyBoard()) {
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