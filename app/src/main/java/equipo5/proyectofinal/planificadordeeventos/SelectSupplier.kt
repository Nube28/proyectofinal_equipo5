package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectSupplier : AppCompatActivity() {
    var adapter: SupplierOverviewAdapter? = null
    var supplierOverview = ArrayList<SupplierOverview>()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_supplier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.list_view)

        CrearProveedores()
        adapter = SupplierOverviewAdapter(this, supplierOverview)
        listView.adapter = adapter


        val btn_register_supplier: Button = findViewById(R.id.btn_register_supplier)
        btn_register_supplier.setOnClickListener{
            val intent: Intent = Intent(this, AddSupplier::class.java)
            startActivity(intent)
        }

    }

    fun CrearProveedores(){
        supplierOverview.add(SupplierOverview("Juanito peludito"))
        supplierOverview.add(SupplierOverview("Maria del Carmen"))
        supplierOverview.add(SupplierOverview("Manuelito el gaupito"))
    }

    class SupplierOverviewAdapter(private val context: Context, private val supplierList: ArrayList<SupplierOverview>) : BaseAdapter() {

        override fun getCount(): Int {
            return supplierList.size
        }

        override fun getItem(position: Int): Any {
            return supplierList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflator.inflate(R.layout.supplier_overview, null)
            val supplier = getItem(position) as SupplierOverview

            val checkBox = view.findViewById<CheckBox>(R.id.check_supplier)
            val textView = view.findViewById<TextView>(R.id.text_name_supplier)

            textView.text = supplier.Supplier_name

            return view
        }
    }
}