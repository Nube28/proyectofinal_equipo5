package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
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
    private lateinit var nombre: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtask_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        val eventId = intent.getStringExtra("eventoId")
        val taskId = intent.getStringExtra("tareaId")
        val subtaskId = intent.getStringExtra("subtareaId")

        if (eventId != null && taskId != null && subtaskId != null) {
            fetchSubtaskFromFirestore(eventId, taskId, subtaskId)
            fetchSuppliers(eventId, taskId, subtaskId)
        }

        findViewById<AppCompatButton>(R.id.btn_add_supplier).setOnClickListener {
            val intent = Intent(this, AddSupplier::class.java)
            intent.putExtra("eventoId", eventId)
            intent.putExtra("tareaId", taskId)
            intent.putExtra("subtareaId", subtaskId)
            intent.putExtra("subtareaNombre", nombre)
            startActivity(intent)
        }
    }

    public override fun onResume(){
        super.onResume()
        val eventId = intent.getStringExtra("eventoId")
        val taskId = intent.getStringExtra("tareaId")
        val subtaskId = intent.getStringExtra("subtareaId")

        if (eventId != null && taskId != null && subtaskId != null) {
            fetchSubtaskFromFirestore(eventId, taskId, subtaskId)
            fetchSuppliers(eventId, taskId, subtaskId)
        }
    }

    private fun  fetchSubtaskFromFirestore(eventId: String, taskId: String, subtaskId: String) {
        val subtaskRef = db.collection("Eventos")
            .document(eventId)
            .collection("Tareas")
            .document(taskId)
            .collection("Subtareas")
            .document(subtaskId)

        subtaskRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    nombre = document.getString("nombre") ?: "Sin nombre"
                    val descripcion = document.getString("descripcion") ?: "Sin descripción"
                    val presupuesto = document.getLong("presupuesto")?.toInt() ?: 0

                    findViewById<TextView>(R.id.task_name_detail).text = nombre
                    findViewById<TextView>(R.id.task_description_detail).text =
                        buildString {
                            append(descripcion)
                            append(" con un presupuesto estimado de $")
                            append(presupuesto)
                        }
                } else {
                    println("No se encontró la subtarea.")
                }
            }
            .addOnFailureListener { e ->
                println("Error al obtener subtarea: $e")
            }
    }

    private fun fetchSuppliers(eventId: String, taskId: String, subtaskId: String) {
        val supplierRef = db.collection("Eventos")
            .document(eventId)
            .collection("Tareas")
            .document(taskId)
            .collection("Subtareas")
            .document(subtaskId)
            .collection("Proveedor")

        supplierRef.get()
            .addOnSuccessListener { result ->
                val tableLayout = findViewById<TableLayout>(R.id.table_suppliers)
                val suppliers = result.documents.take(3)

                for (doc in suppliers) {
                    val nombre = doc.getString("nombre") ?: "Sin nombre"
                    val precio = doc.getLong("precio")?.toString() ?: "0"

                    val tableRow = TableRow(this)

                    val nameView = TextView(this).apply {
                        text = nombre
                        setPadding(4, 4, 4, 4)
                        setTextColor(resources.getColor(R.color.black, null))
                        typeface = ResourcesCompat.getFont(this@SubtaskDetail, R.font.kanit_regular)
                    }

                    val budgetView = TextView(this).apply {
                        text = precio
                        setPadding(4, 4, 4, 4)
                        setTextColor(resources.getColor(R.color.black, null))
                        typeface = ResourcesCompat.getFont(this@SubtaskDetail, R.font.kanit_regular)
                    }

                    tableRow.addView(nameView)
                    tableRow.addView(budgetView)
                    tableLayout.addView(tableRow)
                }
            }
            .addOnFailureListener { e ->
                println("Error al obtener proveedores: $e")
            }
    }


}