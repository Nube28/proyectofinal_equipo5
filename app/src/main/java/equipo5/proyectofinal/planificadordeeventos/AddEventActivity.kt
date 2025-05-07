package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad que permite al usuario agregar un nuevo evento.
 */
class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val spinner: Spinner = findViewById(R.id.list_type_event)
        val opciones = arrayOf("Social", "Quinceañera", "Boda", "Fiesta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val etNombre = findViewById<EditText>(R.id.et_name_event)
        val etPresupuesto = findViewById<EditText>(R.id.et_estimated_budget)
        val etDescripcion = findViewById<EditText>(R.id.et_event_description)
        val btnRegister = findViewById<Button>(R.id.btn_register_event)

        val db = FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener {
            val nombre = etNombre.text.toString()
            val presupuesto = etPresupuesto.text.toString().toIntOrNull() ?: 0
            val descripcion = etDescripcion.text.toString()
            val tipo = spinner.selectedItem.toString()

            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val evento = hashMapOf(
                "nombre" to nombre,
                "presupuesto" to presupuesto,
                "descripcion" to descripcion,
                "tipo" to tipo,
                "usuarioId" to userId,
                "gasto" to 0,
                "terminado" to false
            )

            db.collection("Eventos")
                .add(evento)
                .addOnSuccessListener {
                    Toast.makeText(this, "Evento guardado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ChoseEvent::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar evento", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
