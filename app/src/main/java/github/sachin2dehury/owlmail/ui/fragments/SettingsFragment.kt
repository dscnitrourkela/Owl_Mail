package github.sachin2dehury.owlmail.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.ui.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by activityViewModels()

//    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        setPreferencesFromResource(R.xml.fragment_settings, rootKey)
//
//        preferenceManager.findPreference<DropDownPreference>("Logout")?.apply {
////            summary = viewModel.getUserRoll()
//            setOnPreferenceClickListener {
//                viewModel.logout()
//                NavGraphDirections.actionToAuthFragment()
//                true
//            }
//        }
//
//
//        preferenceManager.findPreference<SwitchPreferenceCompat>("Theme")?.apply {
//            when (viewModel.themeState.value) {
//                Constants.DARK_THEME -> setDefaultValue(true)
//                Constants.LIGHT_THEME -> setDefaultValue(false)
//            }
//            setOnPreferenceChangeListener { _, value ->
//                if (value as Boolean) {
//                    viewModel.setThemeState(Constants.LIGHT_THEME)
//                } else {
//                    viewModel.setThemeState(Constants.DARK_THEME)
//                }
//                true
//            }
//        }
//
//
//        preferenceManager.findPreference<SwitchPreferenceCompat>("Sync")?.apply {
//            setOnPreferenceChangeListener { _, value ->
//                if (!(value as Boolean)) {
//                    (requireActivity() as ActivityExt).stopSync()
//                }
//                true
//            }
//        }
//
//    }
}