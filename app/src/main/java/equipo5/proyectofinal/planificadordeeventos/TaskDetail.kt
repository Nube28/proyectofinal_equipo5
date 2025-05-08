package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad que muestra los detalles de una tarea, incluyendo su lista de subtareas.
 * Las subtareas se obtienen dinámicamente desde Firestore.
 */
class TaskDetail : AppCompatActivity() {

    private var adapter: SubtaskOverviewAdapter? = null
    private val subtasksOverview = ArrayList<SubtaskOverview>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_add_subtask = findViewById<FloatingActionButton>(R.id.btn_add_subtask)
        val listView: ListView = findViewById(R.id.list_view)

        val eventId = intent.getStringExtra("eventoId")
        val taskId = intent.getStringExtra("tareaId")

        cargarNombreTarea()

        btn_add_subtask.setOnClickListener {
            val intent: Intent = Intent(this, AddSubtask::class.java)
            intent.putExtra("eventoId", eventId)
            intent.putExtra("tareaId", taskId)
            startActivity(intent)
        }

        adapter = SubtaskOverviewAdapter(this, subtasksOverview, eventId, taskId)
        listView.adapter = adapter

        cargarSubtareasDesdeFirestore()
    }

    override fun onResume() {
        super.onResume()
        cargarSubtareasDesdeFirestore()
    }

    /**
     * Carga todas las subtareas asociadas a una tarea específica desde Firestore.
     */
    private fun cargarSubtareasDesdeFirestore() {
        val eventId = intent.getStringExtra("eventoId") ?: return
        val taskId = intent.getStringExtra("tareaId") ?: return

        db.collection("Eventos")
            .document(eventId)
            .collection("Tareas")
            .document(taskId)
            .collection("Subtareas")
            .get()
            .addOnSuccessListener { documents ->
                subtasksOverview.clear()
                for (document in documents) {
                    val nombre = document.getString("nombre") ?: ""
                    val presupuesto = document.getDouble("presupuesto")?.toInt() ?: 0
                    val descripcion = document.getString("descripcion") ?: ""
                    val subtask = SubtaskOverview(
                        tv_subtask_name = nombre,
                        tv_subtask_budget = presupuesto,
                        tv_subtask_description = descripcion,
                        subtaskId = document.id
                    )
                    subtasksOverview.add(subtask)
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar subtareas", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarNombreTarea() {
        val eventId = intent.getStringExtra("eventoId") ?: return
        val taskId = intent.getStringExtra("tareaId") ?: return
        val taskNameTextView = findViewById<TextView>(R.id.task_name_detail)

        db.collection("Eventos")
            .document(eventId)
            .collection("Tareas")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombreTarea = document.getString("nombre") ?: "Tarea"
                    taskNameTextView.text = nombreTarea
                } else {
                    taskNameTextView.text = "Tarea no encontrada"
                }
            }
            .addOnFailureListener {
                taskNameTextView.text = "Error al cargar nombre"
                Toast.makeText(this, "No se pudo cargar el nombre de la tarea", Toast.LENGTH_SHORT).show()
            }
    }


    /**
     * Adaptador para mostrar elementos de subtareas en un ListView.
     *
     * @param context Contexto de la actividad.
     * @param subtasksOverview Lista de modelos SubtaskOverview.
     * @param eventId ID del evento al que pertenece la tarea.
     * @param taskId ID de la tarea a la que pertenecen las subtareas.
     */
    class SubtaskOverviewAdapter(
        private val context: Context,
        private val subtasksOverview: ArrayList<SubtaskOverview>,
        private val eventId: String?,
        private val taskId: String?
    ) : BaseAdapter() {

        override fun getCount(): Int = subtasksOverview.size

        override fun getItem(position: Int): Any = subtasksOverview[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val subtaskOverview = subtasksOverview[position]
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.subtask_overview, parent, false)

            val tvSubtaskName = view.findViewById<TextView>(R.id.tv_subtask_name)
            val tvSubtaskBudget = view.findViewById<TextView>(R.id.tv_subtask_budget)

            tvSubtaskName.text = subtaskOverview.tv_subtask_name
            tvSubtaskBudget.text = "${subtaskOverview.tv_subtask_budget} Pesos"

            view.setOnClickListener {
                val intent = Intent(context, SubtaskDetail::class.java).apply {
                    putExtra("eventoId", eventId)
                    putExtra("tareaId", taskId)
                    putExtra("subtareaId", subtaskOverview.subtaskId)
                }
                context.startActivity(intent)
            }

            return view
        }
    }
}
