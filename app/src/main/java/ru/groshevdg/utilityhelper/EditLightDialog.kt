package ru.groshevdg.utilityhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.LightData

class EditLightDialog(button: Button, view: View) : DialogFragment() {
    private val editButton = button
    private val activityView = view

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.edit_data))
        builder.setMessage(resources.getString(R.string.edit_light_data))

        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.edit_light_data, null)
        val editDayLight = view.findViewById<EditText>(R.id.update_day_value_edit_text)
        val editNightLight = view.findViewById<EditText>(R.id.update_night_value_edit_text)

        builder.setPositiveButton(resources.getString(R.string.apply))
        {dialog, which ->
            editButton.isEnabled = false
            Snackbar.make(activityView, resources.getString(R.string.data_changed), Snackbar.LENGTH_SHORT).show()
            editLightData(context!!, editDayLight, editNightLight)
        }

        builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which -> }
        builder.setView(view)
        return  builder.create()
    }

    private fun editLightData(context: Context, dayEditText: EditText, nightEditText: EditText) {
        val db = DBHelper(context).writableDatabase

        val cursor = db.query(LightData.TABLE_NAME, arrayOf(LightData._ID), "${LightData.OBJECT} = ?",
            arrayOf(selected_object), null, null, null)

        cursor.moveToLast()

        val idIndex = cursor.getColumnIndex(LightData._ID)
        val id = cursor.getString(idIndex)

        if (nightEditText.text.isNotEmpty() || dayEditText.text.isNotEmpty()) {
            val contentValues = ContentValues()

            if (dayEditText.text.isNotEmpty())
                contentValues.put(LightData.DAY, dayEditText.text.toString())

            if (nightEditText.text.isNotEmpty())
                contentValues.put(LightData.NIGHT, nightEditText.text.toString())

            db.update(LightData.TABLE_NAME, contentValues, "${LightData.OBJECT} = ? AND " +
                    "${LightData._ID} = ?", arrayOf(selected_object, id))
        }
        cursor.close()
    }
}