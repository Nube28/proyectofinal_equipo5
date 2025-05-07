package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Actividad que muestra los detalles de una subtarea.
 * Se encarga de mostrar nombre, descripción y presupuesto, además de permitir registrar proveedores.
 */
class SubtaskDetail : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtask_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        val nombre = intent.getStringExtra("nombre") ?: "Sin nombre"
        val descripcion = intent.getStringExtra("descripcion") ?: "Sin descripción"
        val presupuesto = intent.getIntExtra("presupuesto", 0)

        findViewById<TextView>(R.id.task_name_detail).text = nombre

        val rootView = findViewById<View>(R.id.main)
        findAndReplaceLoremIpsum(rootView, descripcion, presupuesto)

        saveSubtaskToFirestore(nombre, descripcion, presupuesto)

        val eventId = intent.getStringExtra("eventoId")
        val taskId = intent.getStringExtra("tareaId")
        val subtaskId = intent.getStringExtra("subtareaId")

        findViewById<AppCompatButton>(R.id.btn_add_supplier).setOnClickListener {
            val intent = Intent(this, AddSupplier::class.java)
            intent.putExtra("eventoId", eventId)
            intent.putExtra("tareaId", taskId)
            intent.putExtra("subtareaId", subtaskId)
            startActivity(intent)
        }
    }

    /**
     * Busca y reemplaza un TextView de ejemplo con la descripción y presupuesto reales.
     *
     * @param view Vista raíz desde donde se comienza a buscar.
     * @param descripcion Descripción de la subtarea.
     * @param presupuesto Presupuesto asignado.
     */
    private fun findAndReplaceLoremIpsum(view: View, descripcion: String, presupuesto: Int) {
        if (view is TextView) {
            val loremText = getString(R.string.eg_text_lorem)
            if (view.text.toString() == loremText) {
                view.text = "$descripcion\n\nPresupuesto: $$presupuesto"
                return
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findAndReplaceLoremIpsum(view.getChildAt(i), descripcion, presupuesto)
            }
        }
    }

    /**
     * Guarda la subtarea en la colección "subtasks" de Firestore.
     * Nota: este guardado es genérico y no está vinculado directamente a un evento/tarea/subtarea.
     *
     * @param nombre Nombre de la subtarea.
     * @param descripcion Descripción de la subtarea.
     * @param presupuesto Presupuesto asignado.
     */
    private fun saveSubtaskToFirestore(nombre: String, descripcion: String, presupuesto: Int) {
        val subtaskData = hashMapOf(
            "nombre" to nombre,
            "descripcion" to descripcion,
            "presupuesto" to presupuesto
        )

        db.collection("subtasks")
            .add(subtaskData)
            .addOnSuccessListener { documentReference ->
                println("Subtarea añadida con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error al añadir subtarea: $e")
            }
    }
}
