package com.joaorodrigues.dolphinmvp

import Notificador.Companion.showToast
import Utils
import Validator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.joaorodrigues.dolphinmvp.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private var usuario: Usuario? = null

    private lateinit var  binding : ActivityCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cadastrarUsuario()
        adicionarMascaras()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_voltar)
        supportActionBar?.title = "Cadastre-se"
        toolbar.setTitleTextColor(resources.getColor(R.color.white))

    }

    private fun cadastrarUsuario() {
        binding.progressCadastro.visibility = ProgressBar.GONE
        binding.buttonEntrar.setOnClickListener { validarDados() }
    }

    private fun validarDados() {
        val textoNome = binding.editNomeUsuario.text.toString()
        val textoEmail = binding.editCadastroEmail.text.toString()
        val textoSenha = binding.textInputSenhaCadastro.text.toString()
        val textoTelefone = binding.editTelefone.text.toString()
        val dataNascimento = binding.editDataNascimento.text.toString()
        val documento = binding.editDocumentoCpf.text.toString()
        val termosUso = binding.checkboxTermos.isChecked

        val validator = Validator()

        validator.addDateValidator(
            binding.editDataNascimento,
            "Data de nascimento inválida!",
            true
        )
        validator.addEmailValidator(
            binding.editCadastroEmail,
            "Digite um e-mail no formato: nome@email.com",
            true
        )
        validator.addNameValidator(
            binding.editNomeUsuario,
            "Nome Inválido, informe seu nome e sobrenome!",
            true
        )
        validator.addPhoneNumberValidator(binding.editTelefone, "Campo obrigatório", true)
        validator.addCpfValidator(binding.editDocumentoCpf, "CPF inválido!", true)
        validator.addPasswordValidator(binding.textInputSenhaCadastro, "Senha inválida!", true)
        validator.addConfirmPasswordValidator(
            binding.textInputConfirmarSenha,
            binding.textInputSenhaCadastro,
            "As senhas tem que ser iguais!"
        )

        if (validator.validateFields()) {
            usuario = Usuario()
            usuario?.nome = textoNome
            usuario?.email = textoEmail
            usuario?.senha = textoSenha
            usuario?.telefone = textoTelefone
            usuario?.dataNascimento = Utils.stringToDate(dataNascimento)
            usuario?.isTermosdeUso = termosUso

            if (!binding.checkboxTermos.isChecked) {
                showToast(this, "É necessario aceitar os termos de uso!")
                return
            }

            binding.progressCadastro.visibility = View.VISIBLE
            binding.buttonEntrar.text = ""
            usuario?.let { cadastrar(it) }
        }
    }

    private fun cadastrar(usuario: Usuario) {
        binding.progressCadastro.visibility = View.VISIBLE
        val autenticacao = FirebaseConfig.referenciaAutenticacao

        autenticacao!!.createUserWithEmailAndPassword(
            usuario.email.toString(), usuario.senha.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("usuario", usuario)
                }
                startActivity(intent)

                binding.progressCadastro.visibility = View.GONE
                showToast(this, "Cadastro com sucesso")
                UserFirebase.atualizarNomeUsuario(usuario.nome.toString())
                finish()

                try {
                    val identificadorUsuario = Base64Custom.codificarBase64(usuario.email.toString())
                    usuario.id = identificadorUsuario
                    usuario.salvar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                binding.progressCadastro.visibility = View.GONE

                val erroExcecao = when (val exception = task.exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma senha mais forte!"
                    is FirebaseAuthInvalidCredentialsException -> "Por favor, digite um e-mail válido"
                    is FirebaseAuthUserCollisionException -> "Esta conta já tem cadastro!"
                    else -> "Ao cadastrar usuário: ${exception?.message}"
                }

                showToast(this, "Erro: $erroExcecao")
            }
        }
    }

    private fun adicionarMascaras() {
        Utils.addMaskToEditText(binding.editDataNascimento, "##/##/####")
        Utils.addMaskToEditText(binding.editTelefone, "(##) #####-####")
        Utils.addMaskToEditText(binding.editDocumentoCpf, "###.###.###-##")
    }

}