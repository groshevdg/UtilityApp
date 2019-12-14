package ru.groshevdg.utilityhelper.ui.water

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.lifecycle.ViewModel
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import ru.groshevdg.utilityhelper.ui.select_object.selected_object

class WaterViewModel : ViewModel() {
    private lateinit var cursor: Cursor

    fun saveWaterData(context: Context, currentObject: String, coldWater: String, warmWater: String?,
                      sewerage: String, month: String, year: String, sum: Double) {
        val database = DBHelper(context).writableDatabase
        val contentValues = ContentValues()

        contentValues.put(UtilityContract.WaterData.COLD, coldWater)

        if (warmWater != "null")
            contentValues.put(UtilityContract.WaterData.WARM, warmWater)
        else
            contentValues.put(UtilityContract.WaterData.WARM, 0)

        contentValues.put(UtilityContract.WaterData.SEWERAGE, sewerage)
        contentValues.put(UtilityContract.WaterData.OBJECT, currentObject)
        contentValues.put(UtilityContract.WaterData.MONTH, month)
        contentValues.put(UtilityContract.WaterData.YEAR, year)
        contentValues.put(UtilityContract.WaterData.SUM, sum)

        database.insert(UtilityContract.WaterData.TABLE_NAME, null, contentValues)

    }

    fun getLastValues(context: Context) : List<String> {
        val db = DBHelper(context).readableDatabase
        val list: List<String>

        cursor = db.query(UtilityContract.WaterData.TABLE_NAME,
            arrayOf(UtilityContract.WaterData.COLD, UtilityContract.WaterData.WARM,
                UtilityContract.WaterData.SEWERAGE, UtilityContract.WaterData._ID),
            "${UtilityContract.WaterData.OBJECT} =?",
            arrayOf(selected_object), null, null, null)

        try {

            val columnColdWater = cursor.getColumnIndex(UtilityContract.WaterData.COLD)
            val columnWarmWater = cursor.getColumnIndex(UtilityContract.WaterData.WARM)
            val columnSewerage = cursor.getColumnIndex(UtilityContract.WaterData.SEWERAGE)
            val columnID = cursor.getColumnIndex(UtilityContract.WaterData._ID)

            cursor.moveToLast()

            if (cursor.count == 0) {
                list = listOf("0.0", "0.0", "0.0", "0.0")
            }
            else {
                val valueOfColdWater = cursor.getString(columnColdWater)
                val valueOfWarmWater = cursor.getString(columnWarmWater)
                val valueOfSewerage = cursor.getString(columnSewerage)
                val valueOfId = cursor.getString(columnID)

                list = listOf(valueOfColdWater, valueOfWarmWater, valueOfSewerage,
                    valueOfId)
            }
        }
        finally {
            cursor.close()
        }
        return list
    }
}