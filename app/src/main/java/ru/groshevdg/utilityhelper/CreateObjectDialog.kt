package ru.groshevdg.utilitypayhelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.ui.select_object.SelectObjectFragment
import ru.groshevdg.utilityhelper.ui.select_object.SelectObjectLogic
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract

class CreateObjectDialog(view: View) : DialogFragment() {

    private val appView = view

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.select_name_for_object))
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.dialog_layout, null)
        val editedText = view?.findViewById<EditText>(R.id.newName)
        builder.setView(view)
        builder.setPositiveButton(getString(R.string.create))
        {dialog, which ->
            val db = DBHelper(context).readableDatabase
            val cursor = db.query(UtilityContract.AllObjects.TABLE_NAME, arrayOf(UtilityContract.AllObjects.CURRENT_OBJECT),
                "${UtilityContract.AllObjects.CURRENT_OBJECT} = ?", arrayOf(editedText?.text.toString()),
                null, null, null)


            if (cursor.count != 0) {
                Snackbar.make(appView, "Такой объект уже создан", Snackbar.LENGTH_SHORT).show()
            }
            else if (editedText!!.text.isEmpty()){
                Snackbar.make(appView, "Объект не может быть без имени", Snackbar.LENGTH_SHORT).show()
            }
            else {
                saveNewObjectIntoDB(editedText,
                    activity!!.applicationContext)
            }
            cursor.close()

        }
        builder.setNegativeButton(getString(R.string.cancel)) {dialog, which ->  }
        return builder.create()
    }

    private fun saveNewObjectIntoDB(editedText : EditText?, context: Context) {

        val db = DBHelper(context).writableDatabase
        val values = ContentValues()
        values.put(UtilityContract.AllObjects.CURRENT_OBJECT, editedText?.text.toString())
        db.insert(UtilityContract.AllObjects.TABLE_NAME, null, values)
        SelectObjectFragment.adapter.clear()
        SelectObjectFragment.adapter = SelectObjectLogic.fillAdapterFromDB(context)
    }
}