package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

/**
 * `EventDetail` muestra los detalles de un evento seleccionado, incluyendo:
 * - Información general del evento (nombre, presupuesto)
 * - Lista de tareas con subtareas
 * - Posibilidad de marcar tareas y subtareas como terminadas
 *
 * Además permite al usuario:
 * - Agregar nuevas tareas
 * - Visualizar estadísticas del evento
 * - Seleccionar proveedores al completar subtareas
 */
class EventDetail : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var txtEventName: TextView
    private lateinit var txtEventBudget: TextView
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listView = findViewById(R.id.list_view)
        txtEventName = findViewById(R.id.event_name_detail)
        txtEventBudget = findViewById(R.id.event_budget_detail)
        eventId = intent.getStringExtra("eventId")

        val tasks = mutableListOf<TaskItem>()

        if (eventId != null) {
            loadDataEvent()

            val btn_event_estadistic: TextView = findViewById(R.id.btn_event_estadistic)
            btn_event_estadistic.setOnClickListener {
                val intent: Intent = Intent(this, EventStatistics::class.java)
                intent.putExtra("eventoId", eventId)
                startActivity(intent)
            }

            val btn_add_event = findViewById(R.id.btn_add_event) as com.google.android.material.floatingactionbutton.FloatingActionButton
            btn_add_event.setOnClickListener {
                val intent: Intent = Intent(this, AddTask::class.java)
                intent.putExtra("eventoId", eventId)
                startActivity(intent)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        eventId?.let {
            loadDataEvent()
        }
    }

    /**
     * Carga los datos del evento y sus tareas y subtareas desde Firestore.
     */
    private fun loadDataEvent(){
        val db = FirebaseFirestore.getInstance()

        db.collection("Eventos").document(eventId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nombreEvento = document.getString("nombre")
                    val presupuestoEvento = document.get("presupuesto")?.toString()

                    txtEventName.text = nombreEvento
                    txtEventBudget.text = presupuestoEvento

                    db.collection("Eventos").document(eventId.toString()).collection("Tareas")
                        .get()
                        .addOnSuccessListener { tareaDocuments ->
                            val tempTasks = mutableListOf<TaskItem>()
                            val tareasTotales = tareaDocuments.size()
                            var tareasCargadas = 0

                            for (tareaDoc in tareaDocuments) {
                                val tareaId = tareaDoc.id
                                val nombreTarea = tareaDoc.getString("nombre") ?: ""
                                val presupuestoTarea =
                                    tareaDoc.get("presupuesto")?.toString() ?: "0"
                                val terminadoTarea = tareaDoc.getBoolean("terminado") ?: false

                                db.collection("Eventos").document(eventId.toString())
                                    .collection("Tareas").document(tareaId)
                                    .collection("Subtareas")
                                    .get()
                                    .addOnSuccessListener { subtareaDocs ->
                                        val subtareas = subtareaDocs.map { subDoc ->
                                            SubTaskItem(
                                                id = subDoc.id,
                                                name = subDoc.getString("nombre") ?: "",
                                                cost = subDoc.get("presupuesto")?.toString()
                                                    ?: "0",
                                                isChecked = subDoc.getBoolean("terminado")
                                                    ?: false,
                                                taskId = tareaId
                                            )
                                        }

                                        tempTasks.add(
                                            TaskItem(
                                                id = tareaId,
                                                name = nombreTarea,
                                                cost = presupuestoTarea,
                                                isChecked = terminadoTarea,
                                                subTasks = subtareas
                                            )
                                        )

                                        tareasCargadas++
                                        if (tareasCargadas == tareasTotales) {
                                            val adapter = TaskAdapter(this, tempTasks, eventId.toString())
                                            listView.adapter = adapter
                                        }
                                    }
                            }
                        }
                }
            }
    }


    /**
     * Adaptador para representar la lista de tareas y sus subtareas.
     *
     * @property context El contexto de la actividad
     * @property taskList Lista de tareas con sus subtareas
     * @property eventId ID del evento al que pertenecen las tareas
     */
    class TaskAdapter(
        private val context: Context,
        private val taskList: MutableList<TaskItem>,
        private val eventId: String
    ) : BaseAdapter() {

        private val db = FirebaseFirestore.getInstance()

        override fun getCount(): Int = taskList.size

        override fun getItem(position: Int): Any = taskList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.tasks_list, parent, false)

            val checkBoxMain = view.findViewById<CheckBox>(R.id.check_main_task)
            val textMainTask = view.findViewById<TextView>(R.id.text_main_task)
            val arrowExpand = view.findViewById<ImageView>(R.id.arrow_expand)
            val subTaskContainer = view.findViewById<LinearLayout>(R.id.subtask_container)

            val task = taskList[position]
            textMainTask.text = "${task.name} - ${task.cost}"
            checkBoxMain.isChecked = task.isChecked
            subTaskContainer.visibility = if (task.isExpanded) View.VISIBLE else View.GONE
            arrowExpand.setImageResource(if (task.isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)

            checkBoxMain.setOnCheckedChangeListener { _, isChecked ->
                task.isChecked = isChecked
                db.collection("Eventos").document(eventId)
                    .collection("Tareas").document(task.id)
                    .update("terminado", isChecked)
            }

            textMainTask.setOnClickListener {
                val intent: Intent = Intent(context, TaskDetail::class.java)
                intent.putExtra("eventoId", eventId)
                intent.putExtra("tareaId", task.id)
                context.startActivity(intent)

            }

            arrowExpand.setOnClickListener {
                task.isExpanded = !task.isExpanded
                notifyDataSetChanged()
            }

            subTaskContainer.removeAllViews()

            for (subTask in task.subTasks) {
                val subTaskView = LayoutInflater.from(context)
                    .inflate(R.layout.subtask_list, subTaskContainer, false)
                val checkBoxSubTask = subTaskView.findViewById<CheckBox>(R.id.check_sub_task)
                val textSubTask = subTaskView.findViewById<TextView>(R.id.text_sub_task)

                textSubTask.text = "${subTask.name} - ${subTask.cost}"
                checkBoxSubTask.isChecked = subTask.isChecked

                checkBoxSubTask.setOnCheckedChangeListener(null)
                checkBoxSubTask.isChecked = subTask.isChecked

                checkBoxSubTask.setOnCheckedChangeListener { _, isChecked ->
                    subTask.isChecked = isChecked
                    db.collection("Eventos").document(eventId)
                        .collection("Tareas").document(subTask.taskId)
                        .collection("Subtareas").document(subTask.id)
                        .update("terminado", isChecked)

                    if (isChecked) {
                        val intent = Intent(context, SelectSupplier::class.java)
                        intent.putExtra("eventoId", eventId)
                        intent.putExtra("tareaId", subTask.taskId)
                        intent.putExtra("subtareaId", subTask.id)
                        context.startActivity(intent)
                    } else {
                        val proveedorRef = db.collection("Eventos").document(eventId)
                            .collection("Tareas").document(subTask.taskId)
                            .collection("Subtareas").document(subTask.id)
                            .collection("Proveedor")

                        proveedorRef.whereEqualTo("seleccionado", true)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (doc in documents) {
                                    proveedorRef.document(doc.id)
                                        .update("seleccionado", false)
                                }
                            }
                    }
                }

                subTaskContainer.addView(subTaskView)
            }

            return view
        }
    }
}
