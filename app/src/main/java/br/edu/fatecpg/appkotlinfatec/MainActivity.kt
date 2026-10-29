package br.edu.fatecpg.appkotlinfatec

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvStatus: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnResetPassword: Button
    private lateinit var btnDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvStatus = findViewById(R.id.tvStatus)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)

        atualizarStatusUsuario()

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Usuário criado com sucesso!", Toast.LENGTH_SHORT).show()
                        atualizarStatusUsuario()
                    } else {
                        Toast.makeText(this, "Erro ao criar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // 2. Logar
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        atualizarStatusUsuario()
                    } else {
                        Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnResetPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail para redefinição", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }


        btnDeleteAccount.setOnClickListener {
            val user = auth.currentUser

            if (user != null) {
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                            atualizarStatusUsuario()
                        } else {
                            // Se a sessão for muito antiga, o Firebase pode exigir reautenticação prévia
                            Toast.makeText(this, "Erro ao excluir conta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "É necessário estar logado para excluir a conta.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarStatusUsuario() {
        val user = auth.currentUser
        if (user != null) {
            tvStatus.text = "Conectado como:\n${user.email}"
        } else {
            tvStatus.text = "Nenhum usuário conectado"
        }
    }
}