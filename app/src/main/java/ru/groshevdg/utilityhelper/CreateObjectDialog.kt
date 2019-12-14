package ru.groshevdg.utilitypayhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.ui.select_object.SelectObjectFragment
import ru.groshevdg.utilityhelper.ui.select_object.SelectObjectViewModel
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract

class CreateObjectDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.select_name_for_object))
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_layout, null)
        val editedText = view?.findViewById<EditText>(R.id.newName)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.create)) {dialog, which -> saveNewObjectIntoDB(editedText,
            activity!!.applicationContext)}
        builder.setNegativeButton(getString(R.string.cancel)) {dialog, which ->  }
        return builder.create()
    }

    private fun saveNewObjectIntoDB(editedText : EditText?, context: Context) {

        val db = DBHelper(context).writableDatabase
        val values = ContentValues()
        values.put(UtilityContract.AllObjects.CURRENT_OBJECT, editedText?.text.toString())
        db.insert(UtilityContract.AllObjects.TABLE_NAME, null, values)
        SelectObjectFragment.adapter.clear()
        SelectObjectFragment.adapter = SelectObjectViewModel.fillAdapterFromDB(context)
    }
}