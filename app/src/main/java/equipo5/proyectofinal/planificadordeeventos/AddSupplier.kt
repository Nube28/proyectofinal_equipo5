package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddSupplier : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_supplier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_register_supplier = findViewById(R.id.btn_register_supplier) as androidx.appcompat.widget.AppCompatButton
        btn_register_supplier.setOnClickListener{
            val intent: Intent = Intent(this, SubtaskDetail::class.java)
            startActivity(intent)
        }
    }
}