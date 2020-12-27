package github.sachin2dehury.nitrmail.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.ActivityMainBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.ui.viewmodels.MainViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerExt {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle

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

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
//        val intent = Intent(this, SyncService::class.java).apply {
//            putExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
//        }
//        startService(intent)
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        binding.drawerLayout.setDrawerLockMode(lockMode)
        toggle.isDrawerIndicatorEnabled = enabled
    }
}