package ru.groshevdg.utilityhelper.ui.gas

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.ViewModel
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.GasData
import ru.groshevdg.utilityhelper.selected_object

class GasViewModel : ViewModel() {

    fun getLastGasValue(context: Context) : String {

        val db = DBHelper(context).readableDatabase

        val cursor = db.query(GasData.TABLE_NAME, arrayOf(GasData.VALUE),
            "${GasData.OBJECT} = ?", arrayOf(selected_object), null, null, null)
        val valueIndex = cursor.getColumnIndex(GasData.VALUE)
        val value: String

        cursor.moveToLast()

        if (cursor.count == 0) {
            value = "0.0"
        }
        else {
            value = cursor.getString(valueIndex)
        }

        cursor.close()
        return value
    }

    fun saveGasData(context: Context, currentObject: String, value: Double,
                    month: String, year: String, sum: Double) {

        val db = DBHelper(context).writableDatabase
        val contentValues = ContentValues()

        contentValues.put(GasData.VALUE, value)
        contentValues.put(GasData.OBJECT, currentObject)
        contentValues.put(GasData.MONTH, month)
        contentValues.put(GasData.YEAR, year)
        contentValues.put(GasData.SUM, sum)

        db.insert(GasData.TABLE_NAME, null, contentValues)
    }
}