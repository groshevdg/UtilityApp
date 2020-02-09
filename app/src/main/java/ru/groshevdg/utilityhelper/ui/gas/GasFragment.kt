package ru.groshevdg.utilityhelper.ui.gas

import android.app.AlertDialog
import android.content.SharedPreferences
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
import ru.groshevdg.utilityhelper.EditGasDialog
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import java.text.DecimalFormat
import ru.groshevdg.utilityhelper.selected_object

class GasFragment : Fragment(), TextWatcher {

    private lateinit var lastGasValueTextView: TextView
    private lateinit var gasToPayTextView: TextView

    private lateinit var gasEditText: EditText

    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: GasViewModel

    private var lastGasValue: Double = 0.0
    private var costOfGas: Double = 0.0
    private var currentValue: Double = 0.0
    private var sum: Double = 0.0

    private lateinit var saveGasButton: Button
    private lateinit var editGasButton: Button

    private lateinit var yearsSpinner: Spinner
    private lateinit var monthSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gas, null)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        costOfGas = preferences.getString("cost_gas", "0.0")!!.toDouble()

        viewModel = GasViewModel()

        lastGasValue = viewModel.getLastGasValue(context!!).toDouble()

        if (lastGasValue != 0.0) {
            costOfGas = preferences.getString("cost_gas", "0.0")!!.toDouble()
        }

        val costOfGasTV = view.findViewById<TextView>(R.id.cost_of_gas)
        costOfGasTV.text = costOfGas.toString()

        lastGasValueTextView = view.findViewById(R.id.last_gas_text_view)
        gasToPayTextView = view.findViewById(R.id.gas_to_pay_text_view)

        gasEditText = view.findViewById(R.id.gas_edit_text)
        gasEditText.addTextChangedListener(this)

        monthSpinner = view.findViewById(R.id.months_gas_spinner)
        yearsSpinner = view.findViewById(R.id.years_gas_spinner)

        saveGasButton = view.findViewById(R.id.save_gas_btn)
        saveGasButton.setOnClickListener()
        { v ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.correct_data_title))
            builder.setMessage(resources.getText(R.string.correct_data_text))
            builder.setPositiveButton(resources.getText(R.string.yes_answer))
            {dialog, which ->

                val currentMonth = monthSpinner.selectedItem.toString()
                val currentYear = yearsSpinner.selectedItem.toString()

                //Валидация

                val db = DBHelper(context).readableDatabase
                val cursor = db.query(UtilityContract.GasData.TABLE_NAME, arrayOf(UtilityContract.GasData.MONTH),
                    "${UtilityContract.GasData.MONTH} = ? AND ${UtilityContract.GasData.YEAR} = ? AND " +
                            "${UtilityContract.GasData.OBJECT} = ?",
                            arrayOf(currentMonth, currentYear, selected_object), null, null, null)

                if (cursor.count == 0) {
                    viewModel.saveGasData(context!!, selected_object, currentValue,
                        currentMonth, currentYear, sum)
                    Snackbar.make(view, resources.getText(R.string.data_saved), Snackbar.LENGTH_SHORT).show()
                    saveGasButton.isEnabled = false
                }
                else {
                    Snackbar.make(view, resources.getText(R.string.error_data_saved), Snackbar.LENGTH_LONG).show()
                }
                cursor.close()
            }

            builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }
            builder.create().show() }

        editGasButton = view.findViewById(R.id.edit_gas_btn)
        if (lastGasValue == 0.0) editGasButton.isEnabled = false
        editGasButton.setOnClickListener()
        {
            val dialog = EditGasDialog(view, editGasButton)
            dialog.isCancelable = false
            dialog.show(fragmentManager!!, "EditGasDialog")
        }

        lastGasValueTextView.text = lastGasValue.toString()

        return view
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (gasEditText.text.toString().isNotEmpty()) {
            currentValue = gasEditText.text.toString().toDouble()
        }
        else {
            currentValue = 0.0
        }

        sum = (currentValue - lastGasValue) * costOfGas

        if (sum > 0 && lastGasValue != 0.0) {
            gasToPayTextView.text = DecimalFormat("##.##").format(sum)
        }
        else if (currentValue == lastGasValue) {
            gasToPayTextView.text = "Идентичные значения"
        }
        else gasToPayTextView.text = resources.getString(R.string.value_less)

        if (lastGasValue == 0.0 && selected_object.isNotEmpty()) {
            gasToPayTextView.text = "Стартовая установка"
            sum = 0.0
        }

        if (selected_object.isEmpty()) {
            gasToPayTextView.text = resources.getText(R.string.object_isnot_selected)
        }

        if (currentValue > lastGasValue && selected_object.isNotEmpty()) {
            saveGasButton.isEnabled = true
            saveGasButton.text = resources.getText(R.string.save)
        }
        else {
            saveGasButton.isEnabled = false
            saveGasButton.text = resources.getText(R.string.unaccessible)
        }
    }
}