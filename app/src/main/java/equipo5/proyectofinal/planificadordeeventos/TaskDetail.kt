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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

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

        val btnAddEvent = findViewById<FloatingActionButton>(R.id.btn_add_event)
        val listView: ListView = findViewById(R.id.list_view)

        btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddSubtask::class.java))
        }

        adapter = SubtaskOverviewAdapter(this, subtasksOverview)
        listView.adapter = adapter

        cargarSubtareasDesdeFirestore()
    }

    private fun cargarSubtareasDesdeFirestore() {
        db.collection("Subtareas").get()
            .addOnSuccessListener { documents ->
                subtasksOverview.clear()
                for (document in documents) {
                    val nombre = document.getString("nombre") ?: ""
                    val presupuesto = document.getDouble("presupuesto")?.toInt() ?: 0
                    val descripcion = document.getString("descripcion") ?: ""
                    val subtask = SubtaskOverview(
                        tv_subtask_name = nombre,
                        tv_subtask_budget = presupuesto,
                        tv_subtask_description = descripcion
                    )
                    subtasksOverview.add(subtask)
                }
                adapter?.notifyDataSetChanged()
            }
    }

    class SubtaskOverviewAdapter(
        private val context: Context,
        private val subtasksOverview: ArrayList<SubtaskOverview>
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
                    putExtra("nombre", subtaskOverview.tv_subtask_name)
                    putExtra("descripcion", subtaskOverview.tv_subtask_description)
                    putExtra("presupuesto", subtaskOverview.tv_subtask_budget)
                }
                context.startActivity(intent)
            }

            return view
        }
    }
}
