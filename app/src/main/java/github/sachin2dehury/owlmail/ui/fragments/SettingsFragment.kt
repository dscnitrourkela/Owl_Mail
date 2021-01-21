package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        (activity as ActivityExt).apply {
            toggleDrawer(false)
            toggleActionBar(true)
        }

        preferenceManager.findPreference<Preference>("Logout")?.apply {
            setOnPreferenceClickListener {
                viewModel.logout()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.settingsFragment, true)
                    .build()
                NavGraphDirections.actionToAuthFragment()
                findNavController().navigate(NavGraphDirections.actionToAuthFragment(), navOptions)
                true
            }
        }


        preferenceManager.findPreference<SwitchPreferenceCompat>("Theme")?.apply {
            when (viewModel.themeState.value) {
                Constants.DARK_THEME -> setDefaultValue(true)
                Constants.LIGHT_THEME -> setDefaultValue(false)
            }
            setOnPreferenceChangeListener { _, _ ->
                when (viewModel.themeState.value) {
                    Constants.DARK_THEME -> viewModel.setThemeState(Constants.LIGHT_THEME)
                    else -> viewModel.setThemeState(Constants.DARK_THEME)
                }
                true
            }
        }


        preferenceManager.findPreference<SwitchPreferenceCompat>("Sync")?.apply {
            setOnPreferenceChangeListener { _, value ->
                when (value as Boolean) {
                    true -> (requireActivity() as ActivityExt).startSync()
                    else -> (requireActivity() as ActivityExt).stopSync()
                }
                true
            }
        }
    }
}