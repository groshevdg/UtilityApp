package ru.groshevdg.utilityhelper.ui.light

import android.content.ContentValues
import android.content.Context
import androidx.lifecycle.ViewModel
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.LightData
import ru.groshevdg.utilityhelper.selected_object

class LightLogic {

    fun getLastDataOfLight(context: Context) : List<String> {

        val db = DBHelper(context).readableDatabase

        val cursor = db.query(LightData.TABLE_NAME, arrayOf(LightData.DAY, LightData.NIGHT),
            "${LightData.OBJECT} = ?", arrayOf(selected_object), null, null, null)

        val dayIndex = cursor.getColumnIndex(LightData.DAY)
        val nightIndex = cursor.getColumnIndex(LightData.NIGHT)

        cursor.moveToLast()

        val listOfData: List<String>

        if (cursor.count == 0) {
            listOfData = listOf("0.0", "0.0")
        }
        else {
            listOfData = listOf(cursor.getString(dayIndex), cursor.getString(nightIndex))
        }

        cursor.close()

        return listOfData
    }

    fun saveLightData(context: Context, currentObject: String, dayValue: Double, nightValue: Double,
                      month: String, year: String, sum: Double) {
        val db = DBHelper(context).writableDatabase

        val contentValues = ContentValues()

        contentValues.put(LightData.OBJECT, currentObject)
        contentValues.put(LightData.DAY, dayValue)
        contentValues.put(LightData.NIGHT, nightValue)
        contentValues.put(LightData.MONTH, month)
        contentValues.put(LightData.YEAR, year)
        contentValues.put(LightData.SUM, sum)

        db.insert(LightData.TABLE_NAME, null, contentValues)
    }

}