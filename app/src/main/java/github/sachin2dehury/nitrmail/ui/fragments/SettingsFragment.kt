package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.DropDownPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.ui.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        preferenceManager.findPreference<DropDownPreference>("Sync")?.apply {
            summary = "Sync every hour"
            setOnPreferenceChangeListener { _, _ ->
                true
            }
        }

        preferenceManager.findPreference<DropDownPreference>("Logout")?.apply {
            setOnPreferenceClickListener {
//                onDisplayPreferenceDialog(it)
//                viewModel.logout()
//                NavGraphDirections.actionToAuthFragment()
                true
            }
        }

        preferenceManager.findPreference<SwitchPreferenceCompat>("Theme")?.apply {
            when (viewModel.themeState.value) {
                Constants.DARK_THEME -> setDefaultValue(true)
                Constants.LIGHT_THEME -> setDefaultValue(false)
            }
            setOnPreferenceChangeListener { _, value ->
                if (value as Boolean) {
                    viewModel.setThemeState(Constants.LIGHT_THEME)
                } else {
                    viewModel.setThemeState(Constants.DARK_THEME)
                }
                true
            }
        }
    }
}