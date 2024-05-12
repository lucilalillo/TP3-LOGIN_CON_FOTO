package com.lula.tp3_login_con_foto.registro;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lula.tp3_login_con_foto.modelo.Usuario;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class RegistroActivityViewModel extends AndroidViewModel {
    private MutableLiveData<Usuario> usuarioMutable;
    private MutableLiveData<Bitmap> mFoto;
    public RegistroActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Usuario> getUsuarioMutable()
    {
        if(usuarioMutable == null)
        {
            usuarioMutable = new MutableLiveData<>();
        }
        return usuarioMutable;
    }

    public MutableLiveData<Bitmap> getMFoto(){
        if(mFoto== null){
            mFoto = new MutableLiveData<>();
        }
        return mFoto;
    }

    public void guardarUsuario(String nombre, String apellido, String mail, String pass, long dni, Bitmap foto){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Usuario usu = new Usuario(nombre, apellido, mail, pass, dni, byteArray);
        File archivo = new File(getApplication().getFilesDir(), "usuario.dat");

        try {
            FileOutputStream fos = new FileOutputStream(archivo, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(usu);
            bos.flush();
            fos.close();
            usu = null;
            Toast.makeText(getApplication(), "Usuario Guardado", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplication(), "Error al acceder al archivo FileNotFound", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplication(), "Error al acceder al archivo IOExcep", Toast.LENGTH_LONG).show();
        }
    }

    public void recuperarUsuario()
    {
        File archivo = new File(getApplication().getFilesDir(), "usuario.dat");
        Usuario usu = new Usuario();

        try {
            FileInputStream fis = new FileInputStream(archivo);
            BufferedInputStream bis = new BufferedInputStream(fis);
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                usu = (Usuario) ois.readObject();

                String nombre = usu.getNombre();
                String apellido = usu.getApellido();
                String mail = usu.getMail();
                String pass = usu.getPass();
                long dni = usu.getDni();
                if(usu.getFoto()!=null){
                    Bitmap foto = BitmapFactory.decodeByteArray(usu.getFoto(),0, usu.getFoto().length);
                    getMFoto().setValue(foto);
                    getUsuarioMutable().setValue(usu);
                    fis.close();
                    usu=null;
                }

            } catch (EOFException eof) {
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplication(), "Error al acceder al archivo FileNotFound", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplication(), "Error al acceder al archivo IOExcep", Toast.LENGTH_LONG).show();
        }
    }

    public void tomarFoto(int requestCode, int resultCode, @Nullable Intent data, int REQUEST_IMAGE_CAPTURE ){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            mFoto.setValue(imageBitmap);
        }
    }

}
