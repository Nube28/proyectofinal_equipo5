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
        val listView = findViewById<ListView>(R.id.list_view)

        val tasks = mutableListOf(
            TaskItem("Comida", "$800", subTasks = listOf(
                SubTaskItem("Platos", "$50"),
                SubTaskItem("Lechugas", "$50")
            )),
            TaskItem("Sillas", "$1800", subTasks = listOf()),
            TaskItem("Mesas", "$2800", subTasks = listOf())
        )

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
        }

        textMainTask.setOnClickListener {
            val intent: Intent = Intent(context, TaskDetail::class.java)
            //aqui seria q mandamos el id de la tarea para q se acedda ahi, o mandarlo en dtos, ya veremos
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
            }

            subTaskContainer.addView(subTaskView)
        }

        return view
    }
}