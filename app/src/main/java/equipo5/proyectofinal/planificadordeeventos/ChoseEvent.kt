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

class ChoseEvent : AppCompatActivity() {

    var adapter: EventOverviewAdapter? = null
    var eventsOverview = ArrayList<EventOverview>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chose_event)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_add_event = findViewById(R.id.btn_add_event) as com.google.android.material.floatingactionbutton.FloatingActionButton

        btn_add_event.setOnClickListener{
            val intent: Intent = Intent(this, SubtaskCreation::class.java)
            startActivity(intent)
        }

        var listView: ListView = findViewById(R.id.list_view) as ListView

        cargarEventos()
        adapter = EventOverviewAdapter(this, eventsOverview)
        listView.adapter = adapter

    }

    fun cargarEventos(){
        eventsOverview.add(EventOverview(R.drawable.lain, "Fiesta de bell", "10000", "Fiesta"))
        eventsOverview.add(EventOverview(R.drawable.lain, "Boda de bell", "100000", "Boda"))
        eventsOverview.add(EventOverview(R.drawable.lain, "Boda de gomez", "1000", "Boda"))
    }
}

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
            intent.putExtra("eventName", eventOverview.event_name_overview)
            intent.putExtra("eventBudget", eventOverview.event_budget_overview)
            intent.putExtra("eventCategory", eventOverview.event_category_overview)
            context!!.startActivity(intent)
        }

        return view
    }

}
