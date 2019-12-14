package ru.groshevdg.utilityhelper.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.groshevdg.utilityhelper.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }
}