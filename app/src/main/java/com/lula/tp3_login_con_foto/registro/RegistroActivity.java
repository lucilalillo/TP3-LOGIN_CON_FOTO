package com.lula.tp3_login_con_foto.registro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lula.tp3_login_con_foto.R;
import com.lula.tp3_login_con_foto.databinding.ActivityRegistroBinding;
import com.lula.tp3_login_con_foto.modelo.Usuario;

public class RegistroActivity extends AppCompatActivity {
    private RegistroActivityViewModel viewModel;
    private ActivityRegistroBinding binding;

    private static int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.tomarFoto(requestCode, resultCode, data, REQUEST_IMAGE_CAPTURE);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroActivityViewModel.class);
        binding.btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dni = Long.parseLong(binding.etDni.getText().toString());
                String nombre = binding.etNombre.getText().toString();
                String apellido = binding.etApellido.getText().toString();
                String mail = binding.etMail.getText().toString();
                String pass = binding.etPass.getText().toString();
                BitmapDrawable drawable = (BitmapDrawable) binding.ivFoto.getDrawable();
                Bitmap foto = drawable.getBitmap();
                viewModel.guardarUsuario(nombre, apellido, mail, pass, dni, foto);
            }
        });

        binding.btTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intentFoto.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intentFoto, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        viewModel.getUsuarioMutable().observe(this, new Observer<Usuario>() {
            @Override
            public void onChanged(Usuario usuario) {
                binding.etNombre.setText(usuario.getNombre().toString());
                binding.etApellido.setText(usuario.getApellido().toString());
                binding.etMail.setText(usuario.getMail().toString());
                binding.etPass.setText(usuario.getPass().toString());
                binding.ivFoto.setImageBitmap(BitmapFactory.decodeByteArray(usuario.getFoto(),0,usuario.getFoto().length));

            }
        });

        viewModel.getMFoto().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.ivFoto.setImageBitmap(bitmap);
            }
        });
        if(getIntent().getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK)
        {
            viewModel.recuperarUsuario();
        }
    }
}