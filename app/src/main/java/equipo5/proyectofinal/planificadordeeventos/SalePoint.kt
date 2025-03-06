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
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import equipo5.proyectofinal.planificadordeeventos.SelectSupplier.SupplierOverviewAdapter

class SalePoint : AppCompatActivity() {
    var adapter: SalePointOverviewAdapter? = null
    var salePointOverview = ArrayList<SalePointOverview>()

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

        CrearPuntosDeVenta()
        adapter = SalePointOverviewAdapter(this, salePointOverview)
        listView.adapter = adapter

    }

    fun CrearPuntosDeVenta(){
        salePointOverview.add(SalePointOverview("Tortillas Juan", "Tan bien buenotas las tortillas"))
        salePointOverview.add(SalePointOverview("Globitos MariCarmen", "Crea tu propio Globo"))
        salePointOverview.add(SalePointOverview("Tacos el pepe", "El pepe, ete sech"))
    }
    class SalePointOverviewAdapter(private val context: Context, private val salePointList: ArrayList<SalePointOverview>) : BaseAdapter() {

        override fun getCount(): Int {
            return salePointList.size
        }

        override fun getItem(position: Int): Any {
            return salePointList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var view = inflator.inflate(R.layout.sale_point_overview, null)
            val salePoint = getItem(position) as SalePointOverview

            val nameTextView = view.findViewById<TextView>(R.id.sale_point_name)
            val descriptionTextView = view.findViewById<TextView>(R.id.sale_point_description)


            nameTextView.text = salePoint.sale_point_name
            descriptionTextView.text = salePoint.sale_point_description

            return view
        }
    }
}
