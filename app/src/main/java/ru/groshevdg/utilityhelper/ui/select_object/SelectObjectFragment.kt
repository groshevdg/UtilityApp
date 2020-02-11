package ru.groshevdg.utilityhelper.ui.select_object

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import ru.groshevdg.utilityhelper.R
import ru.groshevdg.utilityhelper.selected_object

class SelectObjectFragment : Fragment(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private lateinit var switchObjectViewModel: SelectObjectLogic
    private lateinit var createNewObject: Button
    private lateinit var listOfSavedObjects: ListView

    companion object {
        lateinit var adapter: ArrayAdapter<String>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        switchObjectViewModel =
            ViewModelProviders.of(this).get(SelectObjectLogic::class.java)

        val root = inflater.inflate(R.layout.fragment_select_object, container, false)

        adapter = SelectObjectLogic.fillAdapterFromDB(context)

        listOfSavedObjects = root.findViewById(R.id.list_of_all_objects) as ListView
        listOfSavedObjects.adapter = adapter
        listOfSavedObjects.invalidateViews()

        listOfSavedObjects.setOnItemClickListener(this)
        listOfSavedObjects.setOnItemLongClickListener(this)

        createNewObject = root.findViewById(R.id.create_object_btn)
        createNewObject.setOnClickListener()
        { y -> switchObjectViewModel.showDialogForCreatingNewObject(fragmentManager, root) }

        return root
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.title_accept_data_deleting))
        builder.setNegativeButton(R.string.cancel, null)
        builder.setPositiveButton(R.string.yes_answer) { dialog, which ->
            val id = SelectObjectLogic.mapPositionAndIdToDeleteObject.getValue(position)
            SelectObjectLogic.deleteObjectFromDB(activity, id)
            adapter.clear()
            adapter = SelectObjectLogic.fillAdapterFromDB(context)
            listOfSavedObjects.invalidateViews()
            selected_object = ""
            val navView = activity?.findViewById<NavigationView>(R.id.nav_view)
            val mView = navView?.getHeaderView(0)
            val selectedObjectTextView = mView?.findViewById<TextView>(R.id.selected_object)
            selectedObjectTextView?.text = resources.getText(R.string.object_isnot_selected)
        }
        builder.create().show()
        return true
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected_object = parent?.getItemAtPosition(position).toString()
        val navView = activity?.findViewById<NavigationView>(R.id.nav_view)
        val mView = navView?.getHeaderView(0)
        val selectedObjectTextView = mView?.findViewById<TextView>(R.id.selected_object)

        val selected: String  = resources.getString(R.string.selected_obj_is)

        Snackbar.make(view!!, "$selected $selected_object", Snackbar.LENGTH_SHORT).show()
        selectedObjectTextView?.text = "$selected\n$selected_object"
    }
}