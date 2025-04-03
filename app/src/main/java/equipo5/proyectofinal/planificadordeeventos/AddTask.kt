package equipo5.proyectofinal.planificadordeeventos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class AddTask : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos de la interfaz
        val etTaskName = findViewById<EditText>(R.id.et_task_name)
        val etTaskDescription = findViewById<EditText>(R.id.et_task_description)
        val etTaskBudget = findViewById<EditText>(R.id.et_task_budget)
        val btnRegisterTask = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_add_task)

        // Evento del botón
        btnRegisterTask.setOnClickListener {
            val nombreTarea = etTaskName.text.toString().trim()
            val descripcionTarea = etTaskDescription.text.toString().trim()
            val presupuestoTarea = etTaskBudget.text.toString().toDoubleOrNull() ?: 0.0

            if (nombreTarea.isEmpty() || descripcionTarea.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto de tarea
            val tarea = hashMapOf(
                "nombre" to nombreTarea,
                "descripcion" to descripcionTarea,
                "presupuesto" to presupuestoTarea,
                "fecha" to Timestamp.now()
            )

            // Guardar en Firestore
            db.collection("Tareas").add(tarea)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tarea guardada con éxito", Toast.LENGTH_SHORT).show()
                    limpiarCampos(etTaskName, etTaskDescription, etTaskBudget)
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
