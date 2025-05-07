package equipo5.proyectofinal.planificadordeeventos

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad que permite al usuario agregar un nuevo proveedor.
 */
class AddSupplier : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_supplier)

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
        val uid = currentUser.uid

        val etProviderName = findViewById<EditText>(R.id.et_provider_name)
        val etProductPrice = findViewById<EditText>(R.id.et_product_price)
        val tvProviderNameSpace = findViewById<TextView>(R.id.et_provider_name_space)
        val btnRegisterSupplier = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_register_supplier)

        btnRegisterSupplier.setOnClickListener {
            val nombreProveedor = etProviderName.text.toString().trim()
            val precioProductoText = etProductPrice.text.toString().trim()

            if (nombreProveedor.isEmpty()) {
                Toast.makeText(this, "El nombre del proveedor es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (precioProductoText.isEmpty()) {
                Toast.makeText(this, "El precio del producto es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precioProducto = precioProductoText.toDoubleOrNull()
            if (precioProducto == null) {
                Toast.makeText(this, "El precio debe ser un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvProviderNameSpace.text = nombreProveedor

            val proveedor = hashMapOf(
                "nombre" to nombreProveedor,
                "precio" to precioProducto,
                "fecha" to Timestamp.now(),
                "seleccionado" to false
            )

            val eventoId = intent.getStringExtra("eventoId")
            val tareaId = intent.getStringExtra("tareaId")
            val subTareaId = intent.getStringExtra("subtareaId")


            db.collection("Eventos").document(eventoId.toString())
                .collection("Tareas").document(tareaId.toString())
                .collection("Subtareas").document(subTareaId.toString())
                .collection("Proveedor")
                .add(proveedor)
                .addOnSuccessListener {
                    Toast.makeText(this, "Proveedor guardado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
