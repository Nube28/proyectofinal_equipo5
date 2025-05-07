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

        val eventoId = intent.getStringExtra("eventoId")
        val tareaId = intent.getStringExtra("tareaId")
        val subtareaId = intent.getStringExtra("subtareaId")

        val listView = findViewById<ListView>(R.id.list_view)
        adapter = SupplierOverviewAdapter(this, supplierOverview)
        listView.adapter = adapter

        cargarProveedoresDesdeSubtarea(eventoId.toString(), tareaId.toString(), subtareaId.toString())

        val btn_register_supplier: Button = findViewById(R.id.btn_register_supplier)
        btn_register_supplier.setOnClickListener {
            startActivity(Intent(this, AddSupplier::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun cargarProveedoresDesdeSubtarea(eventoId: String, tareaId: String, subtareaId: String) {
        val subTareaProveedorRef = db.collection("Eventos")
            .document(eventoId)
            .collection("Tareas")
            .document(tareaId)
            .collection("Subtareas")
            .document(subtareaId)
            .collection("Proveedor")

        subTareaProveedorRef.get()
            .addOnSuccessListener { result ->
                supplierOverview.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val precio = document.getDouble("precio") ?: 0.0
                    val seleccionado = document.getBoolean("seleccionado") ?: false
                    val id = document.id

                    supplierOverview.add(SupplierOverview(nombre, seleccionado, id, precio))
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar proveedores: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    inner class SupplierOverviewAdapter(private val context: Context, private val supplierList: ArrayList<SupplierOverview>) : BaseAdapter() {

        override fun getCount(): Int {
            return if (supplierList.isEmpty()) 0 else supplierList.size
        }

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

            checkBox.setOnClickListener {
                val eventoId = intent.getStringExtra("eventoId")
                val tareaId = intent.getStringExtra("tareaId")
                val subtareaId = intent.getStringExtra("subtareaId")

                val proveedorSeleccionado = supplierList[position]
                val subTareaProveedorRef = db.collection("Eventos")
                    .document(eventoId.toString())
                    .collection("Tareas")
                    .document(tareaId.toString())
                    .collection("Subtareas")
                    .document(subtareaId.toString())
                    .collection("Proveedor")

                for (proveedor in supplierList) {
                    val updateSeleccionado = proveedor.id == proveedorSeleccionado.id
                    db.collection("Eventos")
                        .document(eventoId.toString())
                        .collection("Tareas")
                        .document(tareaId.toString())
                        .collection("Subtareas")
                        .document(subtareaId.toString())
                        .collection("Proveedor")
                        .document(proveedor.id)
                        .update("seleccionado", updateSeleccionado)
                }

                for (i in supplierList.indices) {
                    supplierList[i].isSelected = supplierList[i].id == proveedorSeleccionado.id
                }
                //aaqui
                finish()
            }

            return view
        }
    }
}
