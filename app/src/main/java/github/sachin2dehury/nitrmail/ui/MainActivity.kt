package github.sachin2dehury.nitrmail.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.ActivityMainBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.services.SyncBroadcastReceiver
import github.sachin2dehury.nitrmail.ui.viewmodels.MailBoxViewModel
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: MailBoxViewModel

    @Inject
    lateinit var syncBroadcastReceiver: SyncBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MailBoxViewModel::class.java)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setCheckedItem(R.id.inbox)

        drawerOptionMenu()

    }

    private fun drawerOptionMenu() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.inbox -> viewModel.setRequest(Constants.INBOX_URL)
                R.id.sent -> viewModel.setRequest(Constants.SENT_URL)
                R.id.draft -> viewModel.setRequest(Constants.DRAFT_URL)
                R.id.junk -> viewModel.setRequest(Constants.JUNK_URL)
                R.id.trash -> viewModel.setRequest(Constants.TRASH_URL)
            }
            binding.navView.setCheckedItem(it)
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
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

    override fun unregisterSync() {
        unregisterReceiver(syncBroadcastReceiver)
    }

    override fun onDestroy() {
        _binding = null
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(syncBroadcastReceiver, intentFilter)
        super.onDestroy()
    }
}