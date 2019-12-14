package ru.groshevdg.utilityhelper.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.*

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        val DATABASE_NAME = "Utility.db"
        val DATABASE_VERSION = 8
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //Таблица для хранения созданных объектов
        db?.execSQL("CREATE TABLE ${AllObjects.TABLE_NAME}( " +
                "${AllObjects._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${AllObjects.CURRENT_OBJECT} TEXT NOT NULL);")

        db?.execSQL("CREATE TABLE ${WaterData.TABLE_NAME}( " +
                "${WaterData._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${WaterData.OBJECT} TEXT NOT NULL, " +
                "${WaterData.COLD} REAL NOT NULL, " +
                "${WaterData.WARM} REAL NOT NULL DEFAULT 0, " +
                "${WaterData.SEWERAGE} REAL NOT NULL, " +
                "${WaterData.MONTH} TEXT NOT NULL, " +
                "${WaterData.YEAR} TEXT NOT NULL, " +
                "${WaterData.SUM} REAL NOT NULL);")

        db?.execSQL("CREATE TABLE ${GasData.TABLE_NAME}( " +
                "${GasData._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${GasData.OBJECT} TEXT NOT NULL, " +
                "${GasData.VALUE} REAL NOT NULL, " +
                "${GasData.MONTH} TEXT NOT NULL, " +
                "${GasData.YEAR} TEXT NOT NULL, " +
                "${GasData.SUM} REAL NOT NULL);")

        db?.execSQL("CREATE TABLE ${LightData.TABLE_NAME}( " +
                "${LightData._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${LightData.OBJECT} TEXT NOT NULL, " +
                "${LightData.DAY} REAL NOT NULL, " +
                "${LightData.NIGHT} REAL NOT NULL, " +
                "${LightData.MONTH} TEXT NOT NULL, " +
                "${LightData.YEAR} TEXT NOT NULL, " +
                "${LightData.SUM} REAL NOT NULL);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${AllObjects.TABLE_NAME};")
        db?.execSQL("DROP TABLE IF EXISTS ${WaterData.TABLE_NAME};")
        db?.execSQL("DROP TABLE IF EXISTS ${GasData.TABLE_NAME};")
        db?.execSQL("DROP TABLE IF EXISTS ${LightData.TABLE_NAME};")
        onCreate(db)
    }
}