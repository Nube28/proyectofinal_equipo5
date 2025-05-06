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

        db = FirebaseFirestore.getInstance()

        val etTaskName = findViewById<EditText>(R.id.et_task_name)
        val etTaskDescription = findViewById<EditText>(R.id.et_task_description)
        val etTaskBudget = findViewById<EditText>(R.id.et_task_budget)
        val btnRegisterTask = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_add_task)

        val eventoId = intent.getStringExtra("eventoId")

        btnRegisterTask.setOnClickListener {
            val nombreTarea = etTaskName.text.toString().trim()
            val descripcionTarea = etTaskDescription.text.toString().trim()
            val presupuestoTarea = etTaskBudget.text.toString().toDoubleOrNull() ?: 0.0

            if (nombreTarea.isEmpty() || descripcionTarea.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tarea = hashMapOf(
                "nombre" to nombreTarea,
                "descripcion" to descripcionTarea,
                "presupuesto" to presupuestoTarea,
                "fecha" to Timestamp.now()
            )

            db.collection("Eventos").document(eventoId.toString())
                .collection("Tareas").add(tarea)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tarea guardada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
