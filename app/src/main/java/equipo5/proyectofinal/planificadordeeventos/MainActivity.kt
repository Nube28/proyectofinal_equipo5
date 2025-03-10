package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn_sign_in: Button = findViewById(R.id.btn_sign_in) as Button
        btn_sign_in.setOnClickListener {
            val intent: Intent = Intent(this, ChoseEvent::class.java)
            startActivity(intent)
        }

        val btn_not_account_register: TextView = findViewById(R.id.btn_not_account_register) as TextView
        btn_not_account_register.setOnClickListener{
            val intent: Intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

}