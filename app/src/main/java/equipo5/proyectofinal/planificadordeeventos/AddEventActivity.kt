package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_event)

        val spinner: Spinner = findViewById(R.id.list_type_event)
        val opciones = arrayOf("Social", "Quinceañera", "Boda", "Fiesta")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val btn_register_event: Button = findViewById(R.id.btn_register_event) as Button
        btn_register_event.setOnClickListener{
            val intent: Intent = Intent(this, ChoseEvent::class.java)
            startActivity(intent)
        }

    }
}