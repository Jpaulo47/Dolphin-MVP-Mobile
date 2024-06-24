package com.joaorodrigues.dolphinmvp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

/**
 * Classe singleton para configuração do Firebase.
 * Retorna a instância do FirebaseAuth, FirebaseStorage & FirebaseDatabase.
 */

object FirebaseConfig {
    private var referenciaFirebase: DatabaseReference? = null

    //retorna instancia do fitebaseAuth
    @JvmStatic
    var referenciaAutenticacao: FirebaseAuth? = null
        get() {
            if (field == null) {
                field = FirebaseAuth.getInstance()
            }
            return field
        }
        private set
    private var storage: StorageReference? = null

    //retorna a referencia do database
    val firebase: DatabaseReference?
        get() {
            if (referenciaFirebase == null) {
                referenciaFirebase = FirebaseDatabase.getInstance().reference
            }
            return referenciaFirebase
        }

    //retorna a instancia do storage
    val firebaseStorage: StorageReference?
        get() {
            if (storage == null) {
                storage = FirebaseStorage.getInstance().reference
            }
            return storage
        }
}