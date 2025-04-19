package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SubtaskDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtask_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nombre = intent.getStringExtra("nombre") ?: "Sin nombre"
        val descripcion = intent.getStringExtra("descripcion") ?: "Sin descripción"
        val presupuesto = intent.getIntExtra("presupuesto", 0)

        // Establecer el nombre de la subtarea
        findViewById<TextView>(R.id.task_name_detail).text = nombre


        val rootView = findViewById<View>(R.id.main)
        findAndReplaceLoremIpsum(rootView, descripcion, presupuesto)

        findViewById<AppCompatButton>(R.id.btn_add_supplier).setOnClickListener {
            val intent = Intent(this, AddSupplier::class.java)
            startActivity(intent)
        }
    }

    private fun findAndReplaceLoremIpsum(view: View, descripcion: String, presupuesto: Int) {
        if (view is TextView) {
            val loremText = getString(R.string.eg_text_lorem)
            if (view.text.toString() == loremText) {

                view.text = "$descripcion\n\nPresupuesto: $$presupuesto"
                return
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findAndReplaceLoremIpsum(view.getChildAt(i), descripcion, presupuesto)
            }
        }
    }
}