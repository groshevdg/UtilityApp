package ru.groshevdg.utilityhelper.ui.water

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.EditWaterDialog
import ru.groshevdg.utilityhelper.MainActivity
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import ru.groshevdg.utilityhelper.selected_object
import java.text.DecimalFormat

class WaterFragment : Fragment(), TextWatcher {

    private lateinit var viewModel: WaterViewModel

    private var lastColdWater: Double = 0.0
    private var lastWarmWater: Double = 0.0

    private var costOfCold: Double? = 0.0
    private var costOfWarm: Double? = 0.0
    private var costOfSewerage: Double? = 0.0

    private lateinit var preferenceManager: SharedPreferences
    private lateinit var listOfLastValues: List<String>

    private lateinit var coldWaterEditText: EditText

    private var currentColdWater: String = ""
    private var currentWarmWater: String? = ""

    private var isSewerageInclude = false

    private var sumOfWater: Double = 0.0
    private var sumOfCold: Double = 0.0
    private var sumOfWarm: Double = 0.0
    private var sumOfCheque: Double = 0.0

    private lateinit var monthsSpinner: Spinner
    private lateinit var yearsSpinner: Spinner

    private lateinit var saveButton: Button
    private lateinit var editButton: Button

    private lateinit var lastColdWaterTextView: TextView
    private lateinit var sumOfWaterTextView: TextView
    private lateinit var sumOfChequeTextView: TextView
    private lateinit var costOfColdWaterTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        viewModel = WaterViewModel()

        listOfLastValues = viewModel.getLastValues(context!!)

        lastColdWater = listOfLastValues.get(0).toDouble()
        lastWarmWater = listOfLastValues.get(1).toDouble()

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

        costOfCold = preferenceManager.getString("cost_water_cold", "0")?.toDouble()
        costOfWarm = preferenceManager.getString("cost_water_warm", "0")?.toDouble()

        val fm = PreferenceManager.getDefaultSharedPreferences(context)
        val isWaterSplitted = fm.getBoolean("water_splitted", false)
        val view: View

        isSewerageInclude = preferenceManager.getBoolean("sewerage_key", false)

        if (isSewerageInclude) {
            costOfSewerage = preferenceManager.getString("cost_sewerage", "0")?.toDouble()
        }

        if (isWaterSplitted) {
            view = inflater.inflate(R.layout.fragment_water_data_splited, null)
        }
        else {
            view = inflater.inflate(R.layout.fragment_water_data_no_splited, null)
        }

        val costOfSewerageTextView: TextView? = view.findViewById<TextView?>(R.id.cost_of_sewerage)
        val costOfWarmWaterTextView: TextView? = view.findViewById<TextView?>(R.id.cost_of_warm_water)
        val lastWarmWaterTextView: TextView? = view.findViewById(R.id.last_warm_water_value)
        val warmEditText: EditText? = view.findViewById(R.id.warm_water_et)

        costOfColdWaterTextView = view.findViewById(R.id.cost_of_cold_water)

        if (isSewerageInclude) {
            costOfSewerageTextView?.text = costOfSewerage.toString()
        }
        else {
            costOfSewerageTextView?.text = resources.getString(R.string.not_include)
        }

        costOfColdWaterTextView.text = costOfCold.toString()
        costOfWarmWaterTextView?.text = costOfWarm.toString()

        monthsSpinner = view.findViewById(R.id.months)
        yearsSpinner = view.findViewById(R.id.years)

        editButton = view.findViewById(R.id.edit_btn)
        if (lastColdWater == 0.0) editButton.isEnabled = false
        editButton.setOnClickListener()
        {v ->
            val dialog = EditWaterDialog(isWaterSplitted, editButton, view)
            dialog.isCancelable = false
            dialog.show(fragmentManager!!, "EditWaterDialog")
            }

        saveButton = view.findViewById(R.id.save_btn)
        saveButton.setOnClickListener()
        {v ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.correct_data_title))
            builder.setMessage(resources.getText(R.string.correct_data_text))
            builder.setPositiveButton(resources.getText(R.string.yes_answer))
            {dialog, which ->

                val currentMonth = monthsSpinner.selectedItem.toString()
                val currentYear = yearsSpinner.selectedItem.toString()

                //Валидация
                val db = DBHelper(context).readableDatabase
                val cursor = db.query(UtilityContract.WaterData.TABLE_NAME, arrayOf(UtilityContract.WaterData.MONTH),
                    "${UtilityContract.WaterData.MONTH} = ? AND ${UtilityContract.WaterData.YEAR} = ? AND " +
                            "${UtilityContract.WaterData.OBJECT} = ?",
                    arrayOf(currentMonth, currentYear, selected_object), null, null, null)

                if (cursor.count == 0) {
                    viewModel.saveWaterData(context!!, selected_object,
                        currentColdWater, currentWarmWater, currentMonth, currentYear, sumOfCheque)
                    Snackbar.make(view, resources.getString(R.string.data_saved), Snackbar.LENGTH_SHORT).show()
                    saveButton.isEnabled = false
                }
                else {
                    Snackbar.make(view, resources.getString(R.string.error_data_saved), Snackbar.LENGTH_LONG).show()
                }
                cursor.close()
            }

            builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }
            builder.create().show()
            }

        coldWaterEditText = view.findViewById(R.id.cold_water_et)
        coldWaterEditText.addTextChangedListener(this)
        warmEditText?.addTextChangedListener(this)

        val warmWaterEditText: EditText? = view.findViewById(R.id.warm_water_et)
        warmWaterEditText?.addTextChangedListener(this)

        lastColdWaterTextView = view.findViewById(R.id.last_cold_water_value)

        lastColdWaterTextView.text = lastColdWater.toString()
        lastWarmWaterTextView?.text = lastWarmWater?.toString()

        sumOfWaterTextView = view.findViewById(R.id.water_sum)
        sumOfChequeTextView = view.findViewById(R.id.sum_of_cheque)

        return view
    }


    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val warmWaterEditText = view!!.findViewById<EditText>(R.id.warm_water_et)
        val saveButton = view!!.findViewById<Button>(R.id.save_btn)
        val costOfSewerageTV = view!!.findViewById<TextView>(R.id.cost_of_sewerage)
        val sumOfSewerageTV = view!!.findViewById<TextView>(R.id.sum_of_sewerage)

        if (coldWaterEditText.text.toString().isNotEmpty()) {
            currentColdWater = coldWaterEditText.text.toString()
            sumOfCold = (currentColdWater.toDouble() - lastColdWater) * costOfCold as Double
        }
        else {
            currentColdWater = "0.0"
        }

        if (warmWaterEditText?.text.toString().isNotEmpty() && warmWaterEditText != null) {
            currentWarmWater = warmWaterEditText.text.toString()
            sumOfWarm = (currentWarmWater!!.toDouble() - lastWarmWater) * costOfWarm as Double
        }
        else {
            currentWarmWater = "0.0"
        }

        sumOfWater = sumOfCold + sumOfWarm

        var sumOfSewerage = (currentColdWater.toDouble() - lastColdWater
                + currentWarmWater!!.toDouble() - lastWarmWater) * costOfSewerage!!

        if (isSewerageInclude) {
            costOfSewerageTV.text = DecimalFormat("##.##").format(costOfSewerage!!)
            sumOfSewerageTV.text = sumOfSewerage.toString()
        }
        else if (isSewerageInclude && sumOfSewerage < 0.0) {
            sumOfSewerageTV.text = resources.getString(R.string.value_less)
        }
        else {
            costOfSewerageTV.text = resources.getString(R.string.not_include)
            sumOfSewerageTV.text = resources.getString(R.string.not_include)
        }

        sumOfCheque = if (isSewerageInclude) {
            sumOfWater + (currentColdWater.toDouble() - lastColdWater
                    + currentWarmWater!!.toDouble() - lastWarmWater) * costOfSewerage!!
        }
        else {
            sumOfWater
        }

        if (coldWaterEditText.text.isEmpty() || warmWaterEditText != null && warmWaterEditText.text.isEmpty()) {
            sumOfWaterTextView.text = "Заполните все поля"
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
        }
        else if (lastColdWater == 0.0 && lastWarmWater == 0.0) {
            sumOfWaterTextView.text = "Стартовая установка"
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
            sumOfWater = 0.0
            sumOfCheque = 0.0
            sumOfSewerage = 0.0
        }
        else if (currentColdWater.toDouble() == lastColdWater ||
            (currentWarmWater!!.toDouble() == lastWarmWater && warmWaterEditText != null)) {
            sumOfWaterTextView.text = "Идентичные значения"
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
        }
        else if (currentColdWater.toDouble() < lastColdWater && currentWarmWater!!.toDouble() > lastWarmWater
            && warmWaterEditText != null) {
            sumOfWaterTextView.text = "Некорректный набор\nпоказаний"
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
        }
        else if (currentColdWater.toDouble() > lastColdWater && currentWarmWater!!.toDouble() < lastWarmWater
            && warmWaterEditText != null) {
            sumOfWaterTextView.text = "Некорректный набор\nпоказаний"
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
        }
        else if (sumOfWater < 0) {
            sumOfWaterTextView.text = resources.getString(R.string.value_less)
            sumOfChequeTextView.text = ""
            sumOfSewerageTV.text = ""
        }
        else {
            sumOfWaterTextView.text = DecimalFormat("##.##").format(sumOfWater)
            sumOfChequeTextView.text = DecimalFormat("##.##").format(sumOfCheque)
            saveButton.isEnabled = true
            saveButton.text = resources.getText(R.string.unaccessible)

        }

        if (currentColdWater.toDouble() > lastColdWater && selected_object.isNotBlank()
            && (currentWarmWater!!.toDouble() >= lastWarmWater && warmWaterEditText != null)) {
            saveButton.isEnabled = true
            saveButton.text = resources.getString(R.string.save)
        }
        else if (currentColdWater.toDouble() > lastColdWater && selected_object.isNotBlank()) {
            saveButton.isEnabled = true
            saveButton.text = resources.getString(R.string.save)
        }
        else {
            saveButton.isEnabled = false
            saveButton.text = resources.getText(R.string.unaccessible)
        }

        if (lastColdWater == 0.0 && lastWarmWater == 0.0) {
            saveButton.isEnabled = true
            saveButton.text = resources.getString(R.string.save)
        }
    }
}