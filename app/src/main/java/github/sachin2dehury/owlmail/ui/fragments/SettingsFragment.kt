package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.showSnackbar
import github.sachin2dehury.owlmail.ui.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.refreshStates()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        (requireActivity() as AppCompatActivity).enableActionBar(true)
        (requireActivity() as ActivityExt).enableDrawer(false)

        preferenceManager.findPreference<Preference>("Logout")?.apply {
            setOnPreferenceClickListener {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.settingsFragment, true)
                    .build()
                NavGraphDirections.actionToAuthFragment()
                findNavController().navigate(NavGraphDirections.actionToAuthFragment(), navOptions)
                true
            }
        }


        preferenceManager.findPreference<SwitchPreferenceCompat>("Theme")?.apply {
            setDefaultValue(viewModel.isDarkThemeEnabled.value ?: true)
            setOnPreferenceChangeListener { _, value ->
                viewModel.saveThemeState(value as Boolean)
                view?.showSnackbar("Theme will be applied on exit.")
                true
            }
        }


        preferenceManager.findPreference<SwitchPreferenceCompat>("Sync")?.apply {
            setDefaultValue(viewModel.shouldSync.value ?: false)
            setOnPreferenceChangeListener { _, value ->
                viewModel.saveSyncState(value as Boolean)
                viewModel.refreshStates()
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.refreshStates()
    }
}