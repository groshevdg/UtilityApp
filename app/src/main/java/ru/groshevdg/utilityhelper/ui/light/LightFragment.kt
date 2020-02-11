package ru.groshevdg.utilityhelper.ui.light

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
import ru.groshevdg.utilityhelper.EditLightDialog
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract
import ru.groshevdg.utilityhelper.selected_object
import java.text.DecimalFormat

class LightFragment : Fragment(), TextWatcher {

    private lateinit var lastDayLightTextView: TextView
    private lateinit var lastNightLightTextView: TextView
    private lateinit var lightToPayTextView: TextView

    private lateinit var saveLightButton: Button
    private lateinit var editLightButton: Button

    private lateinit var newDayLightEditText: EditText
    private lateinit var newNightLightEditText: EditText

    private lateinit var monthsSpinner: Spinner
    private lateinit var yearsSpinner: Spinner

    private lateinit var listOfLightData: List<String>

    private var lastDayLightValue: Double = 0.0
    private var lastNightLightValue: Double = 0.0
    private var costOfDayLight: Double? = 0.0
    private var costOfNightLight: Double? = 0.0

    private var newDayValue: Double = 0.0
    private var newNightValue: Double = 0.0
    private var sum: Double = 0.0

    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: LightLogic

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_light, null)
        viewModel = LightLogic()

        listOfLightData = viewModel.getLastDataOfLight(context!!)

        lastDayLightValue = listOfLightData.get(0).toDouble()
        lastNightLightValue = listOfLightData.get(1).toDouble()

        preferences = PreferenceManager.getDefaultSharedPreferences(context)


        costOfDayLight = preferences.getString("cost_light_day", "0.0")?.toDouble()
        costOfNightLight = preferences.getString("cost_light_night", "0.0")?.toDouble()


        val costDayTV = view.findViewById<TextView>(R.id.cost_day_light)
        val costNightTV = view.findViewById<TextView>(R.id.cost_night_light)

        costDayTV.text = costOfDayLight.toString()
        costNightTV.text = costOfNightLight.toString()

        lastDayLightTextView = view.findViewById(R.id.last_day_light_text_view)
        lastDayLightTextView.text = lastDayLightValue.toString()

        lastNightLightTextView = view.findViewById(R.id.last_night_light_text_view)
        lastNightLightTextView.text = lastNightLightValue.toString()

        lightToPayTextView = view.findViewById(R.id.light_to_pay_text_view)

        newDayLightEditText = view.findViewById(R.id.new_day_light_edit_text)
        newDayLightEditText.addTextChangedListener(this)
        newNightLightEditText = view.findViewById(R.id.new_night_light_edit_text)
        newNightLightEditText.addTextChangedListener(this)

        monthsSpinner = view.findViewById(R.id.light_months_spinner)
        yearsSpinner = view.findViewById(R.id.years_light_spinner)

        saveLightButton = view.findViewById(R.id.save_light_button)
        saveLightButton.setOnClickListener()
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
                val cursor = db.query(UtilityContract.LightData.TABLE_NAME, arrayOf(UtilityContract.LightData.MONTH),
                    "${UtilityContract.LightData.MONTH} = ? AND ${UtilityContract.LightData.YEAR} = ? AND " +
                            "${UtilityContract.LightData.OBJECT} = ?",
                    arrayOf(currentMonth, currentYear, selected_object), null, null, null)

                if (cursor.count == 0) {
                    viewModel.saveLightData(context!!, selected_object, newDayValue, newNightValue,
                        currentMonth, currentYear, sum)
                    Snackbar.make(view, resources.getText(R.string.data_saved), Snackbar.LENGTH_SHORT).show()
                    saveLightButton.isEnabled = false
                }
                else {
                    Snackbar.make(view, resources.getText(R.string.error_data_saved), Snackbar.LENGTH_LONG).show()
                }
                cursor.close()
            }

            builder.setNegativeButton(resources.getText(R.string.cancel)) {dialog, which ->  }
            builder.create().show()
        }

        editLightButton = view.findViewById(R.id.edit_light_button)
        if (lastDayLightValue == 0.0 && lastNightLightValue == 0.0) editLightButton.isEnabled = false
        editLightButton.setOnClickListener()
        {v -> val dialog = EditLightDialog(editLightButton, view)
            dialog.isCancelable = false
            dialog.show(fragmentManager!!, "EditLightData")
        }

        return view
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        if (newDayLightEditText.text.isNotEmpty()) {

            newDayValue = newDayLightEditText.text.toString().toDouble()
        }
        else {
            newDayValue = 0.0
        }

        if (newNightLightEditText.text.isNotEmpty()) {

            newNightValue = newNightLightEditText.text.toString().toDouble()
        }
        else {
            newNightValue = 0.0
        }

        sum = (newDayValue - lastDayLightValue) * costOfDayLight!! + (newNightValue - lastNightLightValue) * costOfNightLight!!

        if (sum > 0 && newDayValue > lastDayLightValue && newNightValue > lastNightLightValue) {
            lightToPayTextView.text = DecimalFormat("##.##").format(sum)
        }
        else if (newNightLightEditText.text.isEmpty() || newDayLightEditText.text.isEmpty()) {
            lightToPayTextView.text = "Заполните все поля"
        }
        else if (lastDayLightValue == newDayValue || lastNightLightValue == newNightValue) {
            lightToPayTextView.text = "Идентичные значения"
        }
        else {
            lightToPayTextView.text = "< 0"
        }

        if (lastNightLightValue == 0.0 && lastDayLightValue == 0.0 && selected_object.isNotEmpty()) {
            lightToPayTextView.text = "Стартовая установка"
            saveLightButton.isEnabled = true
            saveLightButton.text = resources.getText(R.string.save)
            sum = 0.0
        }

        if (selected_object.isEmpty()) {
            lightToPayTextView.text = resources.getText(R.string.object_isnot_selected)
        }

        if (newDayValue > lastDayLightValue && newNightValue > lastNightLightValue
            && selected_object.isNotEmpty()) {

            saveLightButton.isEnabled = true
            saveLightButton.text = resources.getText(R.string.save)
        }
        else {
            saveLightButton.isEnabled = false
            saveLightButton.text = resources.getText(R.string.unaccessible)
        }
    }
}