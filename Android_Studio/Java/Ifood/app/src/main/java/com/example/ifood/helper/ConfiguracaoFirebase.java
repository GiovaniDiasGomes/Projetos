package com.example.ifood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase
{
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenciaStorage;

    // Retornar a referência do DatabaseReference
    public static DatabaseReference getFirebase()
    {
        if(referenciaFirebase == null)
        {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return  referenciaFirebase;
    }

    // Retornar a referência do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao()
    {
        if(referenciaAutenticacao == null)
        {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return  referenciaAutenticacao;
    }

    // Retornar a referência do StorageReference
    public static StorageReference getFirebaseStorage()
    {
        if(referenciaStorage == null)
        {
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return  referenciaStorage;
    }

}
