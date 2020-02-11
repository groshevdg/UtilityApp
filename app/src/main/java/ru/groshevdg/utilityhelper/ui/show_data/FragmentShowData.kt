package ru.groshevdg.utilityhelper.ui.show_data

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.*
import ru.groshevdg.utilityhelper.selected_object
import java.lang.StringBuilder


class FragmentShowData : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var preferences: SharedPreferences
    private var internet = 0.0
    private var rubbish = 0.0
    private var isInternetInclude = false
    private var isRubbishInclude = false

    private lateinit var monthSpinner: Spinner
    private lateinit var  yearSpinner: Spinner
    private lateinit var textView: TextView

    private lateinit var cursorWater: Cursor
    private lateinit var cursorGas: Cursor
    private lateinit var cursorLight: Cursor

    private var gasIndex = 0
    private var gasSumIndex = 0
    private var gasMonthIndex = 0
    private var gasYearIndex = 0

    private var dayIndex = 0
    private var nightIndex = 0
    private var lightSumIndex = 0
    private var lightMonthIndex = 0
    private var lightYearIndex = 0

    private var coldIndex = 0
    private var warmIndex = 0
    private var waterSumIndex = 0
    private var waterMonthIndex = 0
    private var waterYearIndex = 0

    private var summaryString: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_data, null)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        monthSpinner = view.findViewById(R.id.select_month)
        monthSpinner.onItemSelectedListener = this
        yearSpinner = view.findViewById(R.id.select_year)
        yearSpinner.onItemSelectedListener = this

        isInternetInclude = preferences.getBoolean("internet_key", false)
        isRubbishInclude = preferences.getBoolean("rubbish_key", false)

        if (isInternetInclude) {
            internet = preferences.getString("cost_internet", "0.0")!!.toDouble()
        }

        if (isRubbishInclude) {
            rubbish = preferences.getString("cost_rubbish", "0.0")!!.toDouble()
        }

        textView = view.findViewById(R.id.data_text_view)
        val text = showDatabase(context!!, monthSpinner.selectedItemPosition, yearSpinner.selectedItem.toString())

        textView.text = text
        return view
    }

    private fun showDatabase(context: Context, month: Int, year: String): String {
        val db = DBHelper(context).readableDatabase

        val listOFColdWater: MutableList<String> = mutableListOf()
        val listOFWarmWater: MutableList<String> = mutableListOf()
        val listOFWaterSum: MutableList<String> = mutableListOf()
        val listOFWaterMonth: MutableList<String> = mutableListOf()
        val listOFWaterYear: MutableList<String> = mutableListOf()

        val listOFGasValues: MutableList<String> = mutableListOf()
        val listOFGasSum: MutableList<String> = mutableListOf()
        val listOFGasMonth: MutableList<String> = mutableListOf()
        val listOFGasYear: MutableList<String> = mutableListOf()

        val listOfDayLight: MutableList<String> = mutableListOf()
        val listOfNightLight: MutableList<String> = mutableListOf()
        val listOfLightSum: MutableList<String> = mutableListOf()
        val listOfLightMonth: MutableList<String> = mutableListOf()
        val listOfLightYear: MutableList<String> = mutableListOf()

        when(month) {
            0 -> {
                val allCursors = getCursorsForYear(db, year)

                cursorWater = allCursors.first
                cursorGas = allCursors.second
                cursorLight = allCursors.third

            }

            1 -> {
                val allCursors = getCursorsForFirstQuarter(db, year)

                cursorWater = allCursors.first
                cursorGas = allCursors.second
                cursorLight = allCursors.third

            }

            2 -> {
                val allCursors =  getCursorsForSecondQuarter(db, year)

                cursorWater = allCursors.first
                cursorGas = allCursors.second
                cursorLight = allCursors.third

            }

            3 -> {
                val allCursors = getCursorsForThirdQuarter(db, year)

                cursorWater = allCursors.first
                cursorGas = allCursors.second
                cursorLight = allCursors.third

            }

            4 -> {
                val allCursors = getCursorsForFourthQuarter(db, year)

                cursorWater = allCursors.first
                cursorGas = allCursors.second
                cursorLight = allCursors.third
            }
        }

        initWaterIndexes()
        initGasIndexes()
        initLightIndexes()

        while (cursorWater.moveToNext()) {
            listOFColdWater.add(cursorWater.getString(coldIndex))
            listOFWarmWater.add(cursorWater.getString(warmIndex))
            listOFWaterSum.add(cursorWater.getString(waterSumIndex))
            listOFWaterMonth.add(cursorWater.getString(waterMonthIndex))
            listOFWaterYear.add(cursorWater.getString(waterYearIndex))
        }

        while (cursorGas.moveToNext()) {
            listOFGasValues.add(cursorGas.getString(gasIndex))
            listOFGasSum.add(cursorGas.getString(gasSumIndex))
            listOFGasMonth.add(cursorGas.getString(gasMonthIndex))
            listOFGasYear.add(cursorGas.getString(gasYearIndex))
        }

        while (cursorLight.moveToNext()) {
            listOfDayLight.add(cursorLight.getString(dayIndex))
            listOfNightLight.add(cursorLight.getString(nightIndex))
            listOfLightSum.add(cursorLight.getString(lightSumIndex))
            listOfLightMonth.add(cursorLight.getString(lightMonthIndex))
            listOfLightYear.add(cursorLight.getString(lightYearIndex))
        }

        cursorLight.close()
        cursorGas.close()
        cursorWater.close()

        val builder = StringBuilder()

        listOFColdWater.forEachIndexed { index, s ->
            builder.append("Данные за ${listOFWaterMonth[index]} ${listOFWaterYear[index]}-го\n")
            builder.append("Вода:\n")
            builder.append("Показания холодной: \t ${listOFColdWater[index]} куб \nПоказания горячей:" +
                    "\t ${listOFWarmWater[index]} куб\n" +
                    "Сумма: \t ${listOFWaterSum[index]} руб\n")
            builder.append("Газ:\n")

            var equalStringsGas: Boolean
            var equalStringsLight: Boolean

            try {
                equalStringsGas = listOFWaterMonth[index] == listOFGasMonth[index]
            }
            catch (e: IndexOutOfBoundsException) {
                Log.d("ShowData", e.toString() + "Список не содержит значений!")
                equalStringsGas = false
            }
            if (equalStringsGas && listOFGasMonth.size != 0) {
                builder.append("Значение газа: \t ${listOFGasValues[index]} куб\n" +
                        "Сумма: \t ${listOFGasSum[index]} руб\n")
            }
            else {
                builder.append("Отсутствуют данные по газу за данный месяц.\n")
            }

            try {
                equalStringsLight = listOFWaterMonth[index] == listOfLightMonth[index]
            }
            catch (e: IndexOutOfBoundsException) {
                Log.d("ShowData", e.toString() + "Список не содержит значений!")
                equalStringsLight = false
            }

            builder.append("Свет:\n")

            if (equalStringsLight && listOfLightMonth.size != 0) {
                builder.append("Свет (день): \t ${listOfDayLight[index]} КВт\n" +
                        "Свет (ночь): \t ${listOfNightLight[index]} КВт\n" +
                        "Сумма: \t ${listOfLightSum[index]} руб\n")
            }
            else {
                builder.append("Отсутствуют данные по свету за данный месяц. \n")
            }

            var equalsStringSum: Boolean

            try {
                equalsStringSum = equalStringsGas && equalStringsLight
            }
            catch (e: IndexOutOfBoundsException) {
                Log.d("ShowData", "Ошибка в сравнении списков. Таблицы не равны.")
                equalsStringSum = false
            }

            if (equalsStringSum){
                builder.append("Итого (с учетом мусора и интернета): ")
                val sum = (listOFWaterSum[index].toDouble() +
                        listOFGasSum[index].toDouble() + listOfLightSum[index].toDouble() + internet + rubbish)
                builder.append(sum.toString())
                builder.append(" руб\n\n")
            }
            else {
                builder.append("Невозможно посчитать сумму. Возникла ошибка!\n\n")
            }

        }

        summaryString = builder.toString()

        return summaryString
    }

    private fun getCursorsForFourthQuarter(db: SQLiteDatabase, year: String): Triple<Cursor, Cursor, Cursor> {
        val cursorWater = db.rawQuery(
            "SELECT ${WaterData.COLD}, ${WaterData.WARM}, " +
                    "${WaterData.SUM}, ${WaterData.MONTH}, ${WaterData.YEAR} FROM ${WaterData.TABLE_NAME} WHERE ${WaterData.OBJECT} = ? AND " +
                    "${WaterData.YEAR} = ? AND ${WaterData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Сентябрь", "Октябрь", "Ноябрь"))

        val cursorGas = db.rawQuery(
            "SELECT ${GasData.VALUE}, ${GasData.SUM}, ${GasData.MONTH}, " +
                    "${GasData.YEAR} FROM ${GasData.TABLE_NAME} WHERE ${GasData.OBJECT} = ? AND ${GasData.YEAR} = ? " +
                    "AND ${GasData.MONTH} IN (?, ?, ?);",
            arrayOf(selected_object, year, "Сентябрь", "Октябрь", "Ноябрь"))

        val cursorLight = db.rawQuery(
            "SELECT ${LightData.DAY}, ${LightData.NIGHT}, ${LightData.SUM}, " +
                    "${LightData.MONTH}, ${LightData.YEAR} FROM ${LightData.TABLE_NAME} WHERE ${LightData.OBJECT} = ? AND " +
                    "${LightData.YEAR} = ? AND ${LightData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Сентябрь", "Октябрь", "Ноябрь"))

        return Triple(cursorWater, cursorGas, cursorLight)
    }

    private fun getCursorsForThirdQuarter(db: SQLiteDatabase, year: String): Triple<Cursor, Cursor, Cursor> {

        val cursorWater = db.rawQuery(
            "SELECT ${WaterData.COLD}, ${WaterData.WARM}, " +
                    "${WaterData.SUM}, ${WaterData.MONTH}, ${WaterData.YEAR} FROM ${WaterData.TABLE_NAME} WHERE ${WaterData.OBJECT} = ? AND " +
                    "${WaterData.YEAR} = ? AND ${WaterData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Июнь", "Июль", "Август"))


        val cursorGas = db.rawQuery(
            "SELECT ${GasData.VALUE}, ${GasData.SUM}, ${GasData.MONTH}, " +
                    "${GasData.YEAR} FROM ${GasData.TABLE_NAME} WHERE ${GasData.OBJECT} = ? AND ${GasData.YEAR} = ? " +
                    "AND ${GasData.MONTH} IN (?, ?, ?);",
            arrayOf(selected_object, year, "Июнь", "Июль", "Август"))

        val cursorLight = db.rawQuery(
            "SELECT ${LightData.DAY}, ${LightData.NIGHT}, ${LightData.SUM}, " +
                    "${LightData.MONTH}, ${LightData.YEAR} FROM ${LightData.TABLE_NAME} WHERE ${LightData.OBJECT} = ? AND " +
                    "${LightData.YEAR} = ? AND ${LightData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Июнь", "Июль", "Август"))
        return Triple(cursorWater, cursorGas, cursorLight)
    }

    private fun getCursorsForSecondQuarter(db: SQLiteDatabase, year: String): Triple<Cursor, Cursor, Cursor> {

        val cursorWater = db.rawQuery(
            "SELECT ${WaterData.COLD}, ${WaterData.WARM}, " +
                    "${WaterData.SUM}, ${WaterData.MONTH}, ${WaterData.YEAR} FROM ${WaterData.TABLE_NAME} WHERE ${WaterData.OBJECT} = ? AND " +
                    "${WaterData.YEAR} = ? AND ${WaterData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Март", "Апрель", "Май"))


        val cursorGas = db.rawQuery(
            "SELECT ${GasData.VALUE}, ${GasData.SUM}, ${GasData.MONTH}, " +
                    "${GasData.YEAR} FROM ${GasData.TABLE_NAME} WHERE ${GasData.OBJECT} = ? AND ${GasData.YEAR} = ? " +
                    "AND ${GasData.MONTH} IN (?, ?, ?);",
            arrayOf(selected_object, year, "Март", "Апрель", "Май"))

        val cursorLight = db.rawQuery(
            "SELECT ${LightData.DAY}, ${LightData.NIGHT}, ${LightData.SUM}, " +
                    "${LightData.MONTH}, ${LightData.YEAR} FROM ${LightData.TABLE_NAME} WHERE ${LightData.OBJECT} = ? AND " +
                    "${LightData.YEAR} = ? AND ${LightData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Март", "Апрель", "Май"))

        return Triple(cursorWater, cursorGas, cursorLight)
    }

    private fun getCursorsForFirstQuarter(db: SQLiteDatabase, year: String): Triple<Cursor, Cursor, Cursor> {
        val cursorWater = db.rawQuery(
            "SELECT ${WaterData.COLD}, ${WaterData.WARM}, " +
                    "${WaterData.SUM}, ${WaterData.MONTH}, ${WaterData.YEAR} FROM ${WaterData.TABLE_NAME} WHERE ${WaterData.OBJECT} = ? AND " +
                    "${WaterData.YEAR} = ? AND ${WaterData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Декабрь", "Январь", "Февраль"))


        val cursorGas = db.rawQuery(
            "SELECT ${GasData.VALUE}, ${GasData.SUM}, ${GasData.MONTH}, " +
                    "${GasData.YEAR} FROM ${GasData.TABLE_NAME} WHERE ${GasData.OBJECT} = ? AND ${GasData.YEAR} = ? " +
                    "AND ${GasData.MONTH} IN (?, ?, ?);",
            arrayOf(selected_object, year, "Декабрь", "Январь", "Февраль"))

        val cursorLight = db.rawQuery(
            "SELECT ${LightData.DAY}, ${LightData.NIGHT}, ${LightData.SUM}, " +
                    "${LightData.MONTH}, ${LightData.YEAR} FROM ${LightData.TABLE_NAME} WHERE ${LightData.OBJECT} = ? AND " +
                    "${LightData.YEAR} = ? AND ${LightData.MONTH} IN (?, ?, ?);", arrayOf(
                selected_object, year, "Декабрь", "Январь", "Февраль"))

        return Triple(cursorWater, cursorGas, cursorLight)
    }

    private fun initLightIndexes() {
        dayIndex = cursorLight.getColumnIndex(LightData.DAY)
        nightIndex = cursorLight.getColumnIndex(LightData.NIGHT)
        lightSumIndex = cursorLight.getColumnIndex(LightData.SUM)
        lightMonthIndex = cursorLight.getColumnIndex(LightData.MONTH)
        lightYearIndex = cursorLight.getColumnIndex(LightData.YEAR)
    }

    private fun initGasIndexes() {
        gasIndex = cursorGas.getColumnIndex(GasData.VALUE)
        gasSumIndex = cursorGas.getColumnIndex(GasData.SUM)
        gasMonthIndex = cursorGas.getColumnIndex(GasData.MONTH)
        gasYearIndex = cursorGas.getColumnIndex(GasData.YEAR)
    }

    private fun initWaterIndexes() {
        coldIndex = cursorWater.getColumnIndex(WaterData.COLD)
        warmIndex = cursorWater.getColumnIndex(WaterData.WARM)
        waterSumIndex = cursorWater.getColumnIndex(WaterData.SUM)
        waterMonthIndex = cursorWater.getColumnIndex(WaterData.MONTH)
        waterYearIndex = cursorWater.getColumnIndex(WaterData.YEAR)
    }

    private fun getCursorsForYear(db: SQLiteDatabase, year: String): Triple<Cursor, Cursor, Cursor> {
        val projectionWater: Array<String> = arrayOf(
            WaterData.COLD,
            WaterData.WARM, WaterData.SUM, WaterData.MONTH, WaterData.YEAR)

        val cursorWater = db.query(WaterData.TABLE_NAME, projectionWater,
            "${WaterData.OBJECT} = ? AND ${WaterData.YEAR} = ?",
            arrayOf(selected_object, year), null, null,null)

        val projectionGas = arrayOf(GasData.VALUE, GasData.SUM, GasData.MONTH, GasData.YEAR)
        val cursorGas = db.query(
            GasData.TABLE_NAME, projectionGas, "${GasData.OBJECT} = ? " +
                    "AND ${GasData.YEAR} = ?",
            arrayOf(selected_object, year), null, null, null)

        val projectionLight =
            arrayOf(LightData.DAY, LightData.NIGHT, LightData.SUM, LightData.MONTH, LightData.YEAR)
        val cursorLight = db.query(
            LightData.TABLE_NAME, projectionLight, "${LightData.OBJECT} = ? " +
                    "AND ${LightData.YEAR} = ?",
            arrayOf(selected_object, year), null, null, null)

        return Triple(cursorWater, cursorGas, cursorLight)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val month = monthSpinner.selectedItemPosition
        val year = yearSpinner.selectedItem

        textView.text = showDatabase(context!!, month, year.toString())

    }
}