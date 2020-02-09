package ru.groshevdg.utilityhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.GasData

class EditGasDialog(view: View, button: Button) : DialogFragment() {

    private val editGasButton = button
    private val activityView = view
    private lateinit var gasEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.edit_data))
        builder.setMessage(resources.getString(R.string.edit_gas_data))

        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.edit_gas_data, null)
        gasEditText = view!!.findViewById(R.id.new_gas_value_edit_text)

        builder.setView(view)
        builder.setPositiveButton(resources.getString(R.string.apply))
        {dialog, which ->
            editGasData(context!!, gasEditText)
            editGasButton.isEnabled = false
            Snackbar.make(activityView, resources.getString(R.string.data_changed), Snackbar.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }

        return builder.create()
    }

    private fun editGasData(context: Context, gasET: EditText) {
        val db = DBHelper(context).writableDatabase

        val cursor: Cursor = db.query(GasData.TABLE_NAME, arrayOf(GasData._ID),
            "${GasData.OBJECT} = ?", arrayOf(selected_object), null, null, null)

        cursor.moveToLast()

        val idIndex = cursor.getColumnIndex(GasData._ID)
        val id = cursor.getString(idIndex)

        if (gasET.text.toString().isNotEmpty()) {
            val contentValues = ContentValues()
            contentValues.put(GasData.VALUE, gasET.text.toString())

            db.update(GasData.TABLE_NAME, contentValues, "${GasData.OBJECT} = ? AND ${GasData._ID} = ?",
                arrayOf(selected_object, id))
        }
        cursor.close()
    }
}