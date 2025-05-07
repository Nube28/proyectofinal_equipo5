package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad que permite al usuario editar su perfil.
 */
class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        val btn_guardar_perfil: Button = findViewById(R.id.btn_guardar_perfil) as Button
        btn_guardar_perfil.setOnClickListener{
            val intent: Intent = Intent(this, AccountConfig::class.java)
            startActivity(intent)
        }

    }
}