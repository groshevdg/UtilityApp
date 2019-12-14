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
import ru.groshevdg.utilityhelper.EditWaterDialog
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.ui.select_object.selected_object
import java.text.DecimalFormat

class WaterFragment : Fragment(), TextWatcher {

    private lateinit var viewModel: WaterViewModel

    private var lastColdWater: Double = 0.0
    private var lastWarmWater: Double = 0.0
    private var lastSewerage: Double = 0.0

    private var costOfCold: Double? = 0.0
    private var costOfWarm: Double? = 0.0
    private var costOfSewerage: Double? = 0.0

    private lateinit var preferenceManager: SharedPreferences
    private lateinit var listOfLastValues: List<String>

    private lateinit var coldWaterEditText: EditText
    private lateinit var sewerageEditText: EditText

    private var currentColdWater: String = ""
    private var currentWarmWater: String? = ""
    private var currentSewerage: String = ""

    private var sumOfWater: Double = 0.0
    private var sumOfCold: Double = 0.0
    private var sumOfWarm: Double = 0.0
    private var sumOfSewerage: Double = 0.0
    private var sumOfCheque: Double = 0.0

    private lateinit var monthsSpinner: Spinner
    private lateinit var yearsSpinner: Spinner

    private lateinit var saveButton: Button
    private lateinit var editButton: Button

    private lateinit var lastColdWaterTextView: TextView
    private lateinit var lastSewerageTextView: TextView
    private lateinit var sumOfWaterTextView: TextView
    private lateinit var sumOfSewerageTextView: TextView
    private lateinit var sumOfChequeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

        costOfCold = preferenceManager.getString("cost_water_cold", "0")?.toDouble()
        costOfWarm = preferenceManager.getString("cost_water_warm", "0")?.toDouble()
        costOfSewerage = preferenceManager.getString("cost_sewerage", "0")?.toDouble()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        viewModel = WaterViewModel()

        listOfLastValues = viewModel.getLastValues(context!!)

        lastColdWater = listOfLastValues.get(0).toDouble()
        lastWarmWater = listOfLastValues.get(1).toDouble()
        lastSewerage = listOfLastValues.get(2).toDouble()

        val fm = PreferenceManager.getDefaultSharedPreferences(context)
        val isWaterSplitted = fm.getBoolean("water_splitted", false)
        val view: View

        if (isWaterSplitted) {
            view = inflater.inflate(R.layout.fragment_water_data_splited, null)
        }
        else {
            view = inflater.inflate(R.layout.fragment_water_data_no_splited, null)
        }

        monthsSpinner = view.findViewById(R.id.months)
        yearsSpinner = view.findViewById(R.id.years)

        editButton = view.findViewById(R.id.edit_btn)
        if (lastColdWater == 0.0) editButton.isEnabled = false
        editButton.setOnClickListener()
        {v ->
            editButton.isEnabled = false
            val dialog = EditWaterDialog(isWaterSplitted)
            dialog.isCancelable = false
            dialog.show(fragmentManager!!, "EditWaterDialog")}

        saveButton = view.findViewById(R.id.save_btn)
        saveButton.setOnClickListener()
        {v ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Корректность данных")
            builder.setMessage("Проверьте корректность введенных данных. Все верно?")
            builder.setPositiveButton("Да") {dialog, which ->
                    val currentMonth = monthsSpinner.selectedItem.toString()
                    val currentYear = yearsSpinner.selectedItem.toString()
                    viewModel.saveWaterData(context!!, selected_object,
                        currentColdWater, currentWarmWater, currentSewerage,
                    currentMonth, currentYear, sumOfCheque)
                }
            builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }
            builder.create().show()
            }

        coldWaterEditText = view.findViewById(R.id.cold_water_et)
        coldWaterEditText.addTextChangedListener(this)
        sewerageEditText = view.findViewById(R.id.sewerage_et)
        sewerageEditText.addTextChangedListener(this)

        val warmWaterEditText: EditText? = view.findViewById(R.id.warm_water_et)
        warmWaterEditText?.addTextChangedListener(this)

        val lastWarmWaterTextView: TextView? = view.findViewById(R.id.last_warm_water_value)
        lastWarmWaterTextView?.text = lastWarmWater.toString()

        lastColdWaterTextView = view.findViewById(R.id.last_cold_water_value)
        lastSewerageTextView = view.findViewById(R.id.last_sewerage_value)

        lastColdWaterTextView.text = lastColdWater.toString()
        lastSewerageTextView.text = lastSewerage.toString()

        sumOfWaterTextView = view.findViewById(R.id.water_sum)
        sumOfSewerageTextView = view.findViewById(R.id.sewerage_sum)
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
        currentColdWater = coldWaterEditText.text.toString()
        currentWarmWater = warmWaterEditText?.text.toString()
        currentSewerage = sewerageEditText.text.toString()


        if (currentColdWater != "") {
           sumOfCold = (currentColdWater.toDouble() - lastColdWater) * costOfCold as Double
        }

        if (currentWarmWater != "" && warmWaterEditText != null) {
            sumOfWarm = (currentWarmWater!!.toDouble() - lastWarmWater) * costOfWarm as Double
        }

        if (currentSewerage != "") {
            sumOfSewerage = (currentSewerage.toDouble() - lastSewerage) * costOfSewerage as Double
        }

        sumOfWater = sumOfCold + sumOfWarm
        sumOfCheque = sumOfWater + sumOfSewerage

        sumOfWaterTextView.text = DecimalFormat("##.##").format(sumOfWater)
        sumOfSewerageTextView.text = DecimalFormat("##.##").format(sumOfSewerage)
        sumOfChequeTextView.text =  DecimalFormat("##.##").format(sumOfCheque)

        if (currentColdWater.isNotEmpty() && currentSewerage.isNotEmpty() && selected_object.isNotBlank()) {
            saveButton.isEnabled = true
            saveButton.text = resources.getString(R.string.save)

        }
    }
}