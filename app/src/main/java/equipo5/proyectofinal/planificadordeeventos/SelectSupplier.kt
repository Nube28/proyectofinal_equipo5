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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class SelectSupplier : AppCompatActivity() {
    var adapter: SupplierOverviewAdapter? = null
    var supplierOverview = ArrayList<SupplierOverview>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_supplier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance()

        val listView = findViewById<ListView>(R.id.list_view)

        // Inicializa el adaptador con la lista vacía
        adapter = SupplierOverviewAdapter(this, supplierOverview)
        listView.adapter = adapter

        // Cargar proveedores desde Firebase
        cargarProveedores()

        val btn_register_supplier: Button = findViewById(R.id.btn_register_supplier)
        btn_register_supplier.setOnClickListener{
            val intent: Intent = Intent(this, AddSupplier::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la lista
        cargarProveedores()
    }

    private fun cargarProveedores() {
        db.collection("Proveedores")
            .get()
            .addOnSuccessListener { result ->
                supplierOverview.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val precio = document.getDouble("precio") ?: 0.0
                    val id = document.id // Obtener el ID del documento
                    supplierOverview.add(SupplierOverview(nombre, false, id, precio))
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar proveedores: ${exception.message}",
                    Toast.LENGTH_LONG).show()
            }
    }

    // adaptador
    inner class SupplierOverviewAdapter(private val context: Context, private val supplierList: ArrayList<SupplierOverview>) : BaseAdapter() {

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
            val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflator.inflate(R.layout.supplier_overview, null)
            val supplier = getItem(position) as SupplierOverview

            val checkBox = view.findViewById<CheckBox>(R.id.check_supplier)
            val textView = view.findViewById<TextView>(R.id.text_name_supplier)

            textView.text = supplier.Supplier_name
            checkBox.isChecked = supplier.isSelected

            // click en el checkbox
            checkBox.setOnClickListener {
                supplierList[position].isSelected = checkBox.isChecked
            }

            view.setOnClickListener {
                // detalles del proveedor
                Toast.makeText(context, "Proveedor: ${supplier.Supplier_name}, Precio: $${supplier.price}",
                    Toast.LENGTH_SHORT).show()
            }

            return view
        }
    }
}