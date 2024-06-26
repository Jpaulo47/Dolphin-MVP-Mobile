package com.joaorodrigues.dolphinmvp

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.Exclude
import java.io.Serializable
import java.util.Date

class Usuario : Serializable {
    @get:Exclude
    var id: String? = null
    var nome: String? = null
    var email: String? = null
    var cep: String? = null
    var bairro: String? = null
    var municipio: String? = null
    var estado: String? = null
    var logradouro: String? = null
    var documento: String? = null

    @get:Exclude
    var senha: String? = null
    var telefone: String? = null
    var sexo: String? = null
    var ocupacao: String? = null
    var isTermosdeUso = false
    var dataNascimento: Date? = null

    fun salvar() {
        val firebaseRef = FirebaseConfig.firebase
        val usuario = firebaseRef?.child("usuarios")!!.child(id!!)
        usuario.setValue(this)
    }

    fun atualizar(listener: OnCompleteListener<Void?>?) {
        val identificadorUsuario = UserFirebase.getIdentificadorUsuario()
        val database = FirebaseConfig.firebase
        val usuariosRef = database!!.child("usuarios")
            .child(identificadorUsuario)
        val valoresUsuario: Map<String, Any?> = converterParaMap()
        usuariosRef.updateChildren(valoresUsuario).addOnCompleteListener(listener!!)
    }

    @Exclude
    fun converterParaMap(): HashMap<String, Any?> {
        val usuarioMap = HashMap<String, Any?>()
        usuarioMap["email"] = email
        usuarioMap["nome"] = nome
        usuarioMap["id"] = id
        usuarioMap["cep"] = cep
        usuarioMap["telefone"] = telefone
        usuarioMap["dataNascimento"] = dataNascimento
        usuarioMap["documento"] = documento
        return usuarioMap
    }
}