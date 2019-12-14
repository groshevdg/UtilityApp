package ru.groshevdg.utilityhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import ru.groshevdg.utilityhelper.ui.select_object.selected_object

class EditWaterDialog(isWaterSplited: Boolean) : DialogFragment() {

    private val enable = isWaterSplited
    private lateinit var builder: AlertDialog.Builder
    private lateinit var warmWaterEditText: EditText
    private lateinit var coldWaterEditText: EditText
    private lateinit var sewerageEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        builder = AlertDialog.Builder(context)
        builder.setTitle("Edit data")
        builder.setMessage("Edit the last data water")

        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.edit_water_data, null)
        warmWaterEditText = view!!.findViewById(R.id.warm_water_new_value)
        coldWaterEditText = view.findViewById(R.id.cold_water_new_value)
        sewerageEditText = view.findViewById(R.id.sewerage_new_value)

        warmWaterEditText.isEnabled = enable

        builder.setView(view)
        builder.setPositiveButton("Apply")
        {dialog, which ->  editWaterData(context!!, warmWaterEditText,
            coldWaterEditText, sewerageEditText)}

        builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }

        return builder.create()
    }

    private fun editWaterData(context: Context, warmET : EditText?, coldET: EditText?,
                              sewerageET : EditText?) {

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
        val newValueOfSewerage = sewerageET?.text.toString()

        val contentValues = ContentValues()

        if (warmWaterEditText.text.isNotEmpty())
            contentValues.put(UtilityContract.WaterData.WARM, newValueOfWarmWater)

        if (coldWaterEditText.text.isNotEmpty())
            contentValues.put(UtilityContract.WaterData.COLD, newValueOfColdWater)

        if (sewerageEditText.text.isNotEmpty())
            contentValues.put(UtilityContract.WaterData.SEWERAGE, newValueOfSewerage)

        if (warmWaterEditText.text.isNotEmpty() && coldWaterEditText.text.isNotEmpty() &&
            sewerageEditText.text.isNotEmpty()) {
            db.update(UtilityContract.WaterData.TABLE_NAME, contentValues,
                "${UtilityContract.WaterData._ID} = ? AND ${UtilityContract.WaterData.OBJECT} = ?",
                arrayOf(id, selected_object))
        }
    }
}