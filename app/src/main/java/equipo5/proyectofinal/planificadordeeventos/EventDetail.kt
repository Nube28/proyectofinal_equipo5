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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class EventDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = FirebaseFirestore.getInstance()
        val eventId = intent.getStringExtra("eventId")

        val listView = findViewById<ListView>(R.id.list_view)
        val txtEventName = findViewById<TextView>(R.id.event_name_detail)
        val txtEventBudget = findViewById<TextView>(R.id.event_budget_detail)

        val tasks = mutableListOf<TaskItem>()

        if (eventId != null) {
            db.collection("Eventos").document(eventId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombreEvento = document.getString("nombre") ?: "Sin nombre"
                        val presupuesto = document.get("presupuesto")?.toString() ?: "0"

                        txtEventName.text = nombreEvento
                        txtEventBudget.text = presupuesto

                        val tareaList = document.get("Tarea") as? List<Map<*, *>>

                        if (tareaList != null) {
                            for (tarea in tareaList) {
                                val nombreTarea = tarea["nombre"] as? String ?: ""
                                val presupuestoTarea = tarea["presupuesto"]?.toString() ?: "0"
                                val subtareasRaw = tarea["Subtarea"] as? List<Map<*, *>> ?: listOf()

                                val subTasks = subtareasRaw.map {
                                    SubTaskItem(
                                        name = it["nombre"] as? String ?: "",
                                        cost = it["presupuesto"]?.toString() ?: "0"
                                    )
                                }

                                tasks.add(TaskItem(nombreTarea, presupuestoTarea, subTasks = subTasks))
                            }
                        }

                        val adapter = TaskAdapter(this, tasks)
                        listView.adapter = adapter
                    }
                }
        }

        val btn_event_estadistic: TextView = findViewById(R.id.btn_event_estadistic)
        btn_event_estadistic.setOnClickListener {
            val intent: Intent = Intent(this, EventStatistics::class.java)
            startActivity(intent)
        }

        val adapter = TaskAdapter(this, tasks)
        listView.adapter = adapter

        val btn_add_event = findViewById(R.id.btn_add_event) as com.google.android.material.floatingactionbutton.FloatingActionButton

        btn_add_event.setOnClickListener{
            val intent: Intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }

    }
}

data class TaskItem(
    val name: String,
    val cost: String,
    var isChecked: Boolean = false,
    var isExpanded: Boolean = false,
    val subTasks: List<SubTaskItem>
)

data class SubTaskItem(
    val name: String,
    val cost: String,
    var isChecked: Boolean = false
)

class TaskAdapter(private val context: Context, private val taskList: MutableList<TaskItem>) : BaseAdapter() {

    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Any = taskList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.tasks_list, parent, false)

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
            val intent: Intent = Intent(context, SelectSupplier::class.java)
            context!!.startActivity(intent)
        }

        textMainTask.setOnClickListener {
            val intent: Intent = Intent(context, TaskDetail::class.java)
            intent.putExtra("taskName", task.name)
            context.startActivity(intent)

        }

        arrowExpand.setOnClickListener {
            task.isExpanded = !task.isExpanded
            notifyDataSetChanged()
        }

        subTaskContainer.removeAllViews()

        for (subTask in task.subTasks) {
            val subTaskView = LayoutInflater.from(context).inflate(R.layout.subtask_list, subTaskContainer, false)
            val checkBoxSubTask = subTaskView.findViewById<CheckBox>(R.id.check_sub_task)
            val textSubTask = subTaskView.findViewById<TextView>(R.id.text_sub_task)

            textSubTask.text = "${subTask.name} - ${subTask.cost}"
            checkBoxSubTask.isChecked = subTask.isChecked

            checkBoxSubTask.setOnCheckedChangeListener { _, isChecked ->
                subTask.isChecked = isChecked
                val intent: Intent = Intent(context, SelectSupplier::class.java)
                context!!.startActivity(intent)
            }

            subTaskContainer.addView(subTaskView)
        }

        return view
    }
}