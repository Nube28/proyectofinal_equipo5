package equipo5.proyectofinal.planificadordeeventos

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class AddSupplier : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_supplier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance()

        // elementos de la interfaz
        val etProviderName = findViewById<EditText>(R.id.et_provider_name)
        val etProductPrice = findViewById<EditText>(R.id.et_product_price)
        val tvProviderNameSpace = findViewById<TextView>(R.id.et_provider_name_space)
        val btnRegisterSupplier = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_register_supplier)

        // Evento del botón
        btnRegisterSupplier.setOnClickListener {
            val nombreProveedor = etProviderName.text.toString().trim()
            val precioProducto = etProductPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (nombreProveedor.isEmpty()) {
                Toast.makeText(this, "El nombre del proveedor es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar el TextView con el nombre del proveedor
            tvProviderNameSpace.text = nombreProveedor

            // Crear objeto de proveedor
            val proveedor = hashMapOf(
                "nombre" to nombreProveedor,
                "precio" to precioProducto,
                "fecha" to Timestamp.now()
            )

            // Guardar en Firestore
            db.collection("Proveedores").add(proveedor)
                .addOnSuccessListener {
                    Toast.makeText(this, "Proveedor guardado con éxito", Toast.LENGTH_SHORT).show()
                    limpiarCampos(etProviderName, etProductPrice)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Función para limpiar los campos después de guardar
    private fun limpiarCampos(vararg campos: EditText) {
        for (campo in campos) {
            campo.text.clear()
        }
    }
}