package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TaskDetail : AppCompatActivity() {

    var adapter: SubtaskOverviewAdapter? = null
    var subtasksOverview = ArrayList<SubtaskOverview>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_add_event = findViewById(R.id.btn_add_event) as com.google.android.material.floatingactionbutton.FloatingActionButton

        btn_add_event.setOnClickListener{
            val intent: Intent = Intent(this, AddSubtask::class.java)
            startActivity(intent)
        }

        var listView: ListView = findViewById(R.id.list_view) as ListView

        cargarSubtareas()
        adapter = SubtaskOverviewAdapter(this, subtasksOverview)
        listView.adapter = adapter

    }

    fun cargarSubtareas(){
        subtasksOverview.add(SubtaskOverview("Platos", 50))
        subtasksOverview.add(SubtaskOverview("Lechugas", 150))
        subtasksOverview.add(SubtaskOverview("Platillos", 350))
    }
}

class SubtaskOverviewAdapter: BaseAdapter{
    var subtasksOverview = ArrayList<SubtaskOverview>()
    var context: Context? = null

    constructor(context: Context, subtasksOverview: ArrayList<SubtaskOverview>){
        this.context = context
        this.subtasksOverview = subtasksOverview
    }

    override fun getCount(): Int {
        return subtasksOverview.size
    }

    override fun getItem(position: Int): Any {
        return subtasksOverview[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var subtaskOverview = subtasksOverview[position]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflator.inflate(R.layout.subtask_overview, null)

        val tv_subtask_name = view.findViewById(R.id.tv_subtask_name) as TextView
        val tv_subtask_budget = view.findViewById(R.id.tv_subtask_budget) as TextView

        tv_subtask_name.setText(subtaskOverview.tv_subtask_name)
        tv_subtask_budget.setText(subtaskOverview.tv_subtask_budget)

        view.setOnClickListener{
            val intent = Intent(context, SubtaskDetail::class.java)
            context!!.startActivity(intent)
        }

        return view
    }
}