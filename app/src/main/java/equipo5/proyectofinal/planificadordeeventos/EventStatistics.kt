package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilities.CustomCircleDrawable
import utilities.Graphics


class EventStatistics : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_event_statistics)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }


        val localBar = findViewById<View>(R.id.Local)
        val localRBar = findViewById<View>(R.id.LocalR)
        val mesasBar = findViewById<View>(R.id.Mesas)
        val mesasRBar = findViewById<View>(R.id.MesasR)
        val comidaBar = findViewById<View>(R.id.Comida)
        val comidaRBar = findViewById<View>(R.id.ComidaR)
        val musicaBar = findViewById<View>(R.id.Musica)
        val musicaRBar = findViewById<View>(R.id.MusicaR)
        val totalBar = findViewById<View>(R.id.Total)
        val totalRBar = findViewById<View>(R.id.TotalR)
        val graph = findViewById<ConstraintLayout>(R.id.graph)


        val totalEstimado = Graphics(70f, R.color.blue_graphic)
        val totalReal = Graphics(30f, R.color.orange_graphic)

        val graphics = ArrayList<Graphics>()
        graphics.add(totalEstimado)
        graphics.add(totalReal)

        val fondo = CustomCircleDrawable(this, graphics)
        graph.background = fondo

        val Blue = ContextCompat.getColor(this, R.color.blue_graphic)
        val Orange = ContextCompat.getColor(this, R.color.orange_graphic)


        fun setBarWidth(bar: View, percentage: Int, color: Int) {
            val params = bar.layoutParams
            params.width = (300 * percentage / 100)
            bar.layoutParams = params
            bar.setBackgroundColor(color)
        }

        setBarWidth(localBar, 80, Orange)
        setBarWidth(mesasBar, 60, Orange)
        setBarWidth(comidaBar, 90, Orange)
        setBarWidth(musicaBar, 50, Orange)
        setBarWidth(totalBar, 100, Orange)



        setBarWidth(localRBar, 60, Blue)
        setBarWidth(mesasRBar, 70, Blue)
        setBarWidth(comidaRBar, 100, Blue)
        setBarWidth(musicaRBar, 60, Blue)
        setBarWidth(totalRBar, 120, Blue)

        val btn_sale_point = findViewById(R.id.btn_sale_point) as Button
        btn_sale_point.setOnClickListener{
            val intent: Intent = Intent(this, SalePoint::class.java)
            startActivity(intent)
        }
    }
}