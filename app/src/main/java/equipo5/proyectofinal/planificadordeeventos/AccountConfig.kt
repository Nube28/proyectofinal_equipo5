package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Actividad que permite al usuario interacuar con un menu para configurar su cuenta.
 */
class AccountConfig : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_config)

        val btn_editarPerfil: Button = findViewById(R.id.btn_editarPerfil) as Button
        btn_editarPerfil.setOnClickListener{
            val intent: Intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val btn_cambiarContraseña: Button = findViewById(R.id.btn_cambiarContraseña) as Button
        btn_cambiarContraseña.setOnClickListener{
            val intent: Intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val btn_cerrarSesion: TextView = findViewById(R.id.btn_cerrarSesion) as TextView
        btn_cerrarSesion.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }
}