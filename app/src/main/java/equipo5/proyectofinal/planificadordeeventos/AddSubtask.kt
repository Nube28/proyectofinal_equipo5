package equipo5.proyectofinal.planificadordeeventos

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddSubtask : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subtask)

        // Ajuste de ventanas
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val etNombre = findViewById<EditText>(R.id.et_subtask_name)
        val etDescripcion = findViewById<EditText>(R.id.et_task_description)
        val etPresupuesto = findViewById<EditText>(R.id.et_task_budget)
        val btnGuardar = findViewById<AppCompatButton>(R.id.btn_add_subtask)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val presupuestoTexto = etPresupuesto.text.toString().trim()
            val presupuesto = presupuestoTexto.toIntOrNull()

            // Validación
            if (nombre.isEmpty() || descripcion.isEmpty() || presupuesto == null) {
                Toast.makeText(this, "Todos los campos son obligatorios y el presupuesto debe ser válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val subtask = hashMapOf(
                "nombre" to nombre,
                "descripcion" to descripcion,
                "presupuesto" to presupuesto,
                "fecha" to Timestamp.now()
            )


            val eventoId = intent.getStringExtra("eventoId")
            val tareaId = intent.getStringExtra("tareaId")

            db.collection("Eventos").document(eventoId.toString())
                .collection("Tareas").document(tareaId.toString())
                .collection("Subtareas")
                .add(subtask)
                .addOnSuccessListener {
                    Toast.makeText(this, "Subtarea guardada con éxito", Toast.LENGTH_SHORT).show()
                    etNombre.text.clear()
                    etDescripcion.text.clear()
                    etPresupuesto.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
