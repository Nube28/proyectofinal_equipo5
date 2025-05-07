package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
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
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Actividad que muestra un resumen de los proveedores seleccionados para las subtareas de un evento.
 * Recupera los datos desde Firestore y los presenta en una lista.
 */
class SalePoint : AppCompatActivity() {
    var adapter: SalePointOverviewAdapter? = null
    var salePointOverview = ArrayList<SalePointOverview>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sale_point)
        ViewCompat. setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.list_view)

        val eventId = intent.getStringExtra("eventoId")

        adapter = SalePointOverviewAdapter(this, salePointOverview)
        listView.adapter = adapter

        if (eventId != null) {
            obtenerProveedoresSeleccionados(eventId = eventId) { lista ->
                salePointOverview.clear()
                salePointOverview.addAll(lista)
                adapter?.notifyDataSetChanged()
            }
        }

    }

    /**
     * Obtiene de Firestore todos los proveedores que han sido seleccionados en las subtareas de un evento.
     *
     * @param eventId ID del evento.
     * @param onResult Callback con la lista de objetos [SalePointOverview] obtenidos.
     */
    fun obtenerProveedoresSeleccionados(eventId: String, onResult: (List<SalePointOverview>) -> Unit) {
        val proveedoresSeleccionados = mutableListOf<SalePointOverview>()

        db.collection("Eventos").document(eventId)
            .collection("Tareas")
            .get()
            .addOnSuccessListener { tareas ->
                var tareasRestantes = tareas.size()
                if (tareasRestantes == 0) onResult(proveedoresSeleccionados)

                for (tarea in tareas) {
                    tarea.reference.collection("Subtareas")
                        .get()
                        .addOnSuccessListener { subtareas ->
                            var subtareasRestantes = subtareas.size()
                            if (subtareasRestantes == 0 && --tareasRestantes == 0) {
                                onResult(proveedoresSeleccionados)
                            }

                            for (subtarea in subtareas) {
                                subtarea.reference.collection("Proveedor")
                                    .whereEqualTo("seleccionado", true)
                                    .get()
                                    .addOnSuccessListener { proveedores ->
                                        for (proveedor in proveedores) {
                                            val nombre = proveedor.getString("nombre") ?: "Sin nombre"
                                            val precio = proveedor.getDouble("precio")?.toInt() ?: 0

                                            val sp = SalePointOverview(
                                                sale_point_name = nombre,
                                                sale_point_cost = precio,
                                                subtaskId = subtarea.id
                                            )
                                            proveedoresSeleccionados.add(sp)
                                        }

                                        if (--subtareasRestantes == 0 && --tareasRestantes == 0) {
                                            onResult(proveedoresSeleccionados)
                                        }
                                    }
                                    .addOnFailureListener {
                                        if (--subtareasRestantes == 0 && --tareasRestantes == 0) {
                                            onResult(proveedoresSeleccionados)
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener {
                            if (--tareasRestantes == 0) {
                                onResult(proveedoresSeleccionados)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}

/**
 * Adaptador para mostrar objetos [SalePointOverview] en una lista personalizada.
 *
 * @param context Contexto de la actividad o fragmento.
 * @param salePointOverview Lista de puntos de venta seleccionados.
 */
class SalePointOverviewAdapter : BaseAdapter {
    var salePointOverview = ArrayList<SalePointOverview>()
    var context: Context? = null

    constructor(context: Context, SalePointOverview: ArrayList<SalePointOverview>){
        this.context = context
        this.salePointOverview = SalePointOverview
    }

    override fun getCount(): Int {
        return salePointOverview.size
    }

    override fun getItem(position: Int): Any {
        return salePointOverview[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var salePointOverview = salePointOverview[position]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflator.inflate(R.layout.sale_point_overview, null)

        val sale_point_name = view.findViewById(R.id.sale_point_name) as TextView
        val sale_point_cost = view.findViewById(R.id.sale_point_cost) as TextView

        sale_point_name.setText(salePointOverview.sale_point_name)
        sale_point_cost.text = salePointOverview.sale_point_cost.toString()



        return view
    }
}