package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SubtaskDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_subtask_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_add_supplier = findViewById(R.id.btn_add_supplier) as androidx.appcompat.widget.AppCompatButton

        btn_add_supplier.setOnClickListener{
            val intent: Intent = Intent(this, AddSupplier::class.java)
            startActivity(intent)
        }
    }
}