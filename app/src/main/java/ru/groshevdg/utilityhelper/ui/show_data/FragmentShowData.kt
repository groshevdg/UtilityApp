package ru.groshevdg.utilityhelper.ui.show_data

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.data.DBHelper
import ru.groshevdg.utilityhelper.data.UtilityContract.*
import java.lang.StringBuilder

class FragmentShowData : Fragment() {

    private lateinit var cursor: Cursor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_data, null)

        val textView = view.findViewById<TextView>(R.id.data_text_view)
        val text = showDatabase(context!!)

        textView.text = text
        return view
    }

    private fun showDatabase(context: Context) : String{
        val db = DBHelper(context).readableDatabase

        val projection: Array<String> = arrayOf(WaterData._ID, WaterData.OBJECT, WaterData.COLD,
            WaterData.WARM, WaterData.SEWERAGE, WaterData.MONTH, WaterData.YEAR, WaterData.SUM)
        var string: String

        try {
            cursor = db.query(WaterData.TABLE_NAME, projection, null,
                null, null, null, null)

            val idIndex = cursor.getColumnIndex(WaterData._ID)
            val objIndex = cursor.getColumnIndex(WaterData.OBJECT)
            val coldIndex = cursor.getColumnIndex(WaterData.COLD)
            val warmIndex = cursor.getColumnIndex(WaterData.WARM)
            val sewIndex = cursor.getColumnIndex(WaterData.SEWERAGE)
            val monthIndex = cursor.getColumnIndex(WaterData.MONTH)
            val yearIndex = cursor.getColumnIndex(WaterData.YEAR)
            val sumIndex = cursor.getColumnIndex(WaterData.SUM)

            val builder = StringBuilder()
            builder.append("Id ._. Object ._. Cold ._. Warm ._. Sewer ._. Month ._. Year ._. Sum\n")

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val obj = cursor.getString(objIndex)
                val cold = cursor.getString(coldIndex)
                val warm = cursor.getString(warmIndex)
                val sewerage = cursor.getString(sewIndex)
                val month = cursor.getString(monthIndex)
                val year = cursor.getString(yearIndex)
                val sum = cursor.getString(sumIndex)

                builder.append("${id} ${obj} ${cold} ${warm} ${sewerage} ${month} ${year} ${sum}\n")
            }
            string = builder.toString()
        }
        finally {
            cursor.close()
        }
        return string
    }
}