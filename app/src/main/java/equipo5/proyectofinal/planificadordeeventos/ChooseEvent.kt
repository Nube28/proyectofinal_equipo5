package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad que permite al usuario visualizar una lista de eventos disponibles
 * y seleccionar uno para ver su detalle o crear uno nuevo.
 */
class ChoseEvent : AppCompatActivity() {

    var adapter: EventOverviewAdapter? = null
    var eventsOverview = ArrayList<EventOverview>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_event)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_add_event = findViewById(R.id.btn_add_event) as com.google.android.material.floatingactionbutton.FloatingActionButton
        btn_add_event.setOnClickListener{
            val intent: Intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        var listView: ListView = findViewById(R.id.list_view) as ListView

        cargarEventos()
        adapter = EventOverviewAdapter(this, eventsOverview)
        listView.adapter = adapter

    }

    /**
     * Recupera los eventos del usuario actual desde la base de datos y los carga en la lista.
     */
    fun cargarEventos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("Eventos")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { documents ->
                //si no hay eventos mandalo a add event
                if (documents.isEmpty) {
                    val intent = Intent(this, AddEventActivity::class.java)
                    startActivity(intent)
                }
                //recupera todos los eventos, los guarda en la lista eventsOverview y luego el adaptador actualiza
                for (document in documents) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val presupuesto = document.get("presupuesto")?.toString() ?: "0"
                    val tipo = document.getString("tipo") ?: "Sin tipo"
                    var imagen = R.drawable.lain

                    when (tipo) {
                        "Boda" -> imagen = R.drawable.boda
                        "Social" -> imagen = R.drawable.social
                        "Fiesta" -> imagen = R.drawable.fiesta
                        "Quinceañera" -> imagen = R.drawable.quinceanera
                    }

                    eventsOverview.add(
                        EventOverview(
                            imagen,
                            nombre,
                            presupuesto,
                            tipo,
                            document.id
                        )
                    )
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

}

/**
 * Adaptador personalizado para mostrar la lista de eventos.
 *
 * @property context Contexto de la aplicación.
 * @property eventsOverview Lista de objetos [EventOverview] a mostrar.
 */
class EventOverviewAdapter: BaseAdapter {
    var eventsOverview = ArrayList<EventOverview>()
    var context: Context? = null

    constructor(context: Context, eventsOverview: ArrayList<EventOverview>){
        this.context = context
        this.eventsOverview = eventsOverview
    }

    override fun getCount(): Int {
        return eventsOverview.size
    }

    override fun getItem(position: Int): Any {
        return eventsOverview[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var eventOverview = eventsOverview[position]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflator.inflate(R.layout.event_overview, null)

        val event_iv_overview = view.findViewById(R.id.event_iv_overview) as ImageView
        val event_name_overview = view.findViewById(R.id.event_name_overview) as TextView
        val event_budget_overview = view.findViewById(R.id.event_budget_overview) as TextView
        val event_category_overview = view.findViewById(R.id.event_category_overview) as TextView

        event_iv_overview.setImageResource(eventOverview.event_iv_overview)
        event_name_overview.setText(eventOverview.event_name_overview)
        event_budget_overview.setText(eventOverview.event_budget_overview)
        event_category_overview.setText(eventOverview.event_category_overview)

        view.setOnClickListener {
            val intent = Intent(context, EventDetail::class.java)
            intent.putExtra("eventId", eventOverview.id)
            context!!.startActivity(intent)
        }

        return view
    }

}
