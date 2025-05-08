package equipo5.proyectofinal.planificadordeeventos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

/**
 * Actividad que permite al usuario editar su perfil.
 */
class EditProfileActivity : AppCompatActivity() {

    private var email = ""
    private var contraseña = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        val user = FirebaseAuth.getInstance().currentUser

        val originalEmail = user?.email ?: ""

        auth = FirebaseAuth.getInstance()

        val btn_guardar_perfil: Button = findViewById(R.id.btn_guardar_perfil) as Button
        val et_contrasenia : EditText = findViewById(R.id.et_contrasenia) as EditText
        val et_new_email: EditText = findViewById(R.id.et_new_email) as EditText

        et_new_email.setText(originalEmail)

        btn_guardar_perfil.setOnClickListener {
            email = et_new_email.text.toString().trim()
            contraseña = et_contrasenia.text.toString().trim()

            if (email.isNotBlank() && email != originalEmail && contraseña.isNotBlank()) {
                actualizarEmail(contraseña)
            } else {
                Toast.makeText(this@EditProfileActivity, "No se actualizaron los valores del email", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, AccountConfig::class.java)
            startActivity(intent)
        }

    }

    private fun actualizarEmail(currentPassword: String) {
        val user = auth.currentUser

        val credential = user?.email?.let {
            EmailAuthProvider.getCredential(it, currentPassword)
        }

        if (user != null && credential != null) {
            user.reauthenticate(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.verifyBeforeUpdateEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Verifica tu nuevo correo para completar el cambio.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this, "Error al intentar actualizar el correo.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Reautenticación fallida. Verifica tu contraseña.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}