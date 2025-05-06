package equipo5.proyectofinal.planificadordeeventos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SalePoint : AppCompatActivity() {
    var adapter: SalePointOverviewAdapter? = null
    var salePointOverview = ArrayList<SalePointOverview>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sale_point)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.list_view)


        adapter = SalePointOverviewAdapter(this, salePointOverview)
        listView.adapter = adapter
        CrearPuntosDeVenta()

    }
             //no jala AUN
    fun CrearPuntosDeVenta(){

        val eventId = intent.getStringExtra("eventoId") ?: return
        val taskId = intent.getStringExtra("tareaId") ?: return
        val subtaskId = intent.getStringExtra("subtareaId") ?: return

        db.collection("Eventos").document(eventId).collection("Tareas").document(taskId).collection("Subtareas").document(subtaskId)
            .collection("Proveedor").get().addOnSuccessListener { documents ->
                salePointOverview.clear()
                for (document in documents) {

                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val precio = document.getDouble("precio")?.toInt() ?: 0

                    val proveedor = SalePointOverview(
                        sale_point_name = nombre,
                        sale_point_cost = precio,
                        subtaskId = document.id

                    )
                    salePointOverview.add(proveedor)
                }
                adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
    }


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
