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

class AddSupplier : AppCompatActivity() {

    // Referencias a Firestore y Auth
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_supplier)

        // Ajuste para que la vista considere las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        val uid = currentUser.uid // ID del usuario autenticado

        // Referencias a elementos de la interfaz
        val etProviderName = findViewById<EditText>(R.id.et_provider_name)
        val etProductPrice = findViewById<EditText>(R.id.et_product_price)
        val tvProviderNameSpace = findViewById<TextView>(R.id.et_provider_name_space)
        val btnRegisterSupplier = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_register_supplier)

        // Evento del botón
        btnRegisterSupplier.setOnClickListener {
            val nombreProveedor = etProviderName.text.toString().trim()
            val precioProductoText = etProductPrice.text.toString().trim()

            // Validaciones
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

            // Actualiza el TextView con el nombre
            tvProviderNameSpace.text = nombreProveedor

            // Crea el objeto proveedor
            val proveedor = hashMapOf(
                "nombre" to nombreProveedor,
                "precio" to precioProducto,
                "fecha" to Timestamp.now()
            )

            // Muestra mensaje mientras guarda
            val cargandoToast = Toast.makeText(this, "Guardando proveedor...", Toast.LENGTH_SHORT)
            cargandoToast.show()

            // Guarda en la subcolección del usuario
            db.collection("users").document(uid).collection("proveedores")
                .add(proveedor)
                .addOnSuccessListener {
                    cargandoToast.cancel()
                    Toast.makeText(this, "Proveedor guardado con éxito", Toast.LENGTH_SHORT).show()
                    limpiarCampos(etProviderName, etProductPrice)
                    finish() // Cierra la actividad
                }
                .addOnFailureListener { e ->
                    cargandoToast.cancel()
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Limpia los campos de texto
    private fun limpiarCampos(vararg campos: EditText) {
        for (campo in campos) {
            campo.text.clear()
        }
    }
}
