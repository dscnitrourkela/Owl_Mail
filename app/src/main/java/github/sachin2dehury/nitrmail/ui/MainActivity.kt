package github.sachin2dehury.nitrmail.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.ActivityMainBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.services.SyncService
import github.sachin2dehury.nitrmail.ui.viewmodels.MailBoxViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ActivityExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: MailBoxViewModel

    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setCheckedItem(R.id.inbox)

        viewModel = ViewModelProvider(this).get(MailBoxViewModel::class.java)

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
//                R.id.all -> viewModel.setRequest("")
            }
            binding.navView.setCheckedItem(it)
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.logOut -> {
                viewModel.logOut()
                showSnackbar("Successfully logged out.")
                binding.root.findNavController().navigate(R.id.globalActionToAuthFragment)
                val intent = Intent(this, SyncService::class.java).apply {
                    putExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
                }
                stopService(intent)
            }
            R.id.darkMode -> {
                showSnackbar("Will be done. XD")
            }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}