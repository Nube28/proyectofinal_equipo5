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
import android.widget.Toast
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

        val btn_add_subtask = findViewById<FloatingActionButton>(R.id.btn_add_subtask)
        val listView: ListView = findViewById(R.id.list_view)

        val eventId = intent.getStringExtra("eventoId")
        val taskId = intent.getStringExtra("tareaId")

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
                //OLA KAT ESTO NO DEBERIA DE MANDAR LOS PIMEROS 3 PUTEXTRA, debe de jalarlos de la base de datos en la siguiente pagina salu2
                val intent = Intent(context, SubtaskDetail::class.java).apply {
                    putExtra("nombre", subtaskOverview.tv_subtask_name)
                    putExtra("descripcion", subtaskOverview.tv_subtask_description)
                    putExtra("presupuesto", subtaskOverview.tv_subtask_budget)

                    //ESTOS NO LOS TOQUES KAT
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
