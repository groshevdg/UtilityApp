package ru.groshevdg.utilityhelper.ui.settings

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        val pref = findPreference<Preference>("delete_data")
        pref?.setOnPreferenceClickListener(this)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        val key = preference?.key
        if (key == "delete_data") {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Удаление базы данных")
            builder.setMessage("Вы действительно хотите удалить базу данных?")
            builder.setPositiveButton("Да") {dialog, which ->
                val db = DBHelper(context).writableDatabase
                db.delete("objects", null, null)
                db.delete("water", null, null)
                db.delete("gas", null, null)
                db.delete("light", null, null)
                Snackbar.make(view!!, "База данных удалена", Snackbar.LENGTH_SHORT).show() }

            builder.setNegativeButton("Отмена") {dialog, which ->  }
            builder.create().show()

        }
        return true
    }
}