package ru.groshevdg.utilityhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract

class EditWaterDialog(isWaterSplited: Boolean, button: Button, view: View) : DialogFragment() {

    private val activityView = view
    private val enable = isWaterSplited
    private val editButton = button
    private lateinit var builder: AlertDialog.Builder
    private lateinit var warmWaterEditText: EditText
    private lateinit var coldWaterEditText: EditText
    private lateinit var sewerageEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.edit_data))
        builder.setMessage(resources.getString(R.string.edit_water_data))

        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.edit_water_data, null)
        warmWaterEditText = view!!.findViewById(R.id.warm_water_new_value)
        coldWaterEditText = view.findViewById(R.id.cold_water_new_value)

        warmWaterEditText.isEnabled = enable

        builder.setView(view)
        builder.setPositiveButton(resources.getString(R.string.apply))
        {dialog, which ->  editWaterData(context!!, warmWaterEditText,
            coldWaterEditText)
            editButton.isEnabled = false
            Snackbar.make(activityView, resources.getString(R.string.data_changed), Snackbar.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }

        return builder.create()
    }

    private fun editWaterData(context: Context, warmET : EditText?, coldET: EditText?) {

        val db = DBHelper(context).writableDatabase
        val cursor: Cursor
        val id: String

        cursor = db.query(UtilityContract.WaterData.TABLE_NAME, arrayOf(UtilityContract.WaterData._ID),
            "${UtilityContract.WaterData.OBJECT} = ?", arrayOf(selected_object),
            null, null, null)

        val idIndex = cursor.getColumnIndex(UtilityContract.WaterData._ID)
        cursor.moveToLast()
        id = cursor.getString(idIndex)
        cursor.close()

        val newValueOfWarmWater: String
        if (warmET?.text.toString().isNotBlank()) {
            newValueOfWarmWater = warmET?.text.toString()
        }
        else {
            newValueOfWarmWater = "0.0"
        }

        val newValueOfColdWater = coldET?.text.toString()

        val contentValues = ContentValues()

        if (warmWaterEditText.text.isNotEmpty())
            contentValues.put(UtilityContract.WaterData.WARM, newValueOfWarmWater)

        if (coldWaterEditText.text.isNotEmpty())
            contentValues.put(UtilityContract.WaterData.COLD, newValueOfColdWater)

        if (warmWaterEditText.text.isNotEmpty() && coldWaterEditText.text.isNotEmpty() &&
            sewerageEditText.text.isNotEmpty()) {
            db.update(UtilityContract.WaterData.TABLE_NAME, contentValues,
                "${UtilityContract.WaterData._ID} = ? AND ${UtilityContract.WaterData.OBJECT} = ?",
                arrayOf(id, selected_object))
        }
    }
}