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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SelectSupplier : AppCompatActivity() {

    var adapter: SupplierOverviewAdapter? = null
    var supplierOverview = ArrayList<SupplierOverview>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_supplier)

        // Ajuste para la vista con barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val uid = currentUser.uid // ID del usuario autenticado

        // Configura el ListView
        val listView = findViewById<ListView>(R.id.list_view)
        adapter = SupplierOverviewAdapter(this, supplierOverview)
        listView.adapter = adapter

        cargarProveedores(uid) // Cargar proveedores desde Firestore

        // Botón para ir a agregar proveedor
        val btn_register_supplier: Button = findViewById(R.id.btn_register_supplier)
        btn_register_supplier.setOnClickListener {
            startActivity(Intent(this, AddSupplier::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        auth.currentUser?.uid?.let { cargarProveedores(it) } // Recarga la lista al volver
    }

    // Cargar proveedores de la subcolección del usuario
    private fun cargarProveedores(uid: String) {
        db.collection("users").document(uid).collection("proveedores")
            .get()
            .addOnSuccessListener { result ->
                supplierOverview.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val precio = document.getDouble("precio") ?: 0.0
                    val id = document.id
                    supplierOverview.add(SupplierOverview(nombre, false, id, precio))
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar proveedores: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Adaptador para mostrar la lista de proveedores
    inner class SupplierOverviewAdapter(private val context: Context, private val supplierList: ArrayList<SupplierOverview>) : BaseAdapter() {

        override fun getCount(): Int = supplierList.size

        override fun getItem(position: Int): Any = supplierList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflator.inflate(R.layout.supplier_overview, null)
            val supplier = getItem(position) as SupplierOverview

            val checkBox = view.findViewById<CheckBox>(R.id.check_supplier)
            val textView = view.findViewById<TextView>(R.id.text_name_supplier)

            textView.text = supplier.Supplier_name
            checkBox.isChecked = supplier.isSelected

            // Maneja clic en el checkbox
            checkBox.setOnClickListener {
                supplierList[position].isSelected = checkBox.isChecked
            }

            // Mostrar info al hacer clic en el item
            view.setOnClickListener {
                Toast.makeText(context, "Proveedor: ${supplier.Supplier_name}, Precio: $${supplier.price}", Toast.LENGTH_SHORT).show()
            }

            return view
        }
    }
}
