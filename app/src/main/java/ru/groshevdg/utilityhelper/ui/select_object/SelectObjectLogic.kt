package ru.groshevdg.utilityhelper.ui.select_object

import android.content.Context
import android.database.Cursor
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilitypayhelper.CreateObjectDialog
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import ru.groshevdg.utilityhelper.selected_object

class SelectObjectLogic {

    fun showDialogForCreatingNewObject(manager: FragmentManager?, view: View) {
        val alertDialog = CreateObjectDialog(view)
        alertDialog.isCancelable = false
        alertDialog.show(manager!!, "DialogForCreatingNewObject")
    }

    companion object {
        val mapPositionAndIdToDeleteObject: MutableMap<Int, Int> = mutableMapOf()

        val savedNamesOfObjects: MutableList<String> = arrayListOf()

        private lateinit var cursor: Cursor

        fun fillAdapterFromDB(context: Context?): ArrayAdapter<String> {

            savedNamesOfObjects.clear()
            mapPositionAndIdToDeleteObject.clear()

            val db = DBHelper(context).readableDatabase

            try {
                cursor = db.query(
                    UtilityContract.AllObjects.TABLE_NAME,
                    arrayOf(UtilityContract.AllObjects.CURRENT_OBJECT, UtilityContract.AllObjects._ID), null,
                    null, null, null, null)

                val currentId = cursor.getColumnIndex(UtilityContract.AllObjects._ID)
                val currentNameIndex =
                    cursor.getColumnIndex(UtilityContract.AllObjects.CURRENT_OBJECT)
                var position = 0

                while (cursor.moveToNext()) {
                    val currentName = cursor.getString(currentNameIndex)
                    savedNamesOfObjects.add(currentName)
                    mapPositionAndIdToDeleteObject.put(position, cursor.getInt(currentId))
                    position++
                }
            }
            finally {
                cursor.close()
            }

            val adapter = ArrayAdapter<String>(context!!, R.layout.list_item,
                R.id.textOfListItem, savedNamesOfObjects)
            adapter.notifyDataSetChanged()

            return adapter
        }


        fun deleteObjectFromDB(context: Context?, id: Int) {
            val db = DBHelper(context).writableDatabase
            db.delete(UtilityContract.AllObjects.TABLE_NAME, UtilityContract.AllObjects._ID + "= ?", arrayOf(id.toString()))
            db.delete(UtilityContract.LightData.TABLE_NAME, UtilityContract.LightData.OBJECT + "= ?", arrayOf(
                selected_object))
            db.delete(UtilityContract.GasData.TABLE_NAME, UtilityContract.GasData.OBJECT + "= ?", arrayOf(selected_object))
            db.delete(UtilityContract.WaterData.TABLE_NAME, UtilityContract.WaterData.OBJECT + "= ?", arrayOf(selected_object))
            SelectObjectFragment.adapter.clear()
            SelectObjectFragment.adapter = fillAdapterFromDB(context)
            SelectObjectFragment.adapter.notifyDataSetChanged()
        }
    }
}
