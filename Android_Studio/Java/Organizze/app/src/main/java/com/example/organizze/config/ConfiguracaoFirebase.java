package com.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase
{
    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebase;

    // retorna a instancia do Firebase Auth
    public static FirebaseAuth getFirebaseAutenticacao()
    {
        if(autenticacao == null)
        {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    // retorna a instancia do Firebase Database
    public static DatabaseReference getFirebaseDatabase()
    {
        if(firebase == null)
        {
            firebase = FirebaseDatabase.getInstance("https://organizze-95b3a-default-rtdb.firebaseio.com/").getReference();
        }
        return firebase;
    }

}
