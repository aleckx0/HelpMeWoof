package net.alecks.helpmewoof.ui.registrarse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.alecks.helpmewoof.databinding.FragmentRegistrarseBinding;

public class RegistrarseFragment extends Fragment {

    private RegistrarseViewModel registrarseViewModel;
    private FragmentRegistrarseBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registrarseViewModel =
                new ViewModelProvider(this).get(RegistrarseViewModel.class);

        binding = FragmentRegistrarseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button boton = binding.iniciarsesionbtn;
        final EditText contrasena = binding.contrasena;
        final EditText contrasenaconfirmacion = binding.contrasenaconfirmacion;
        final EditText correo = binding.correo;

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (contrasena.getText().toString().equals(contrasenaconfirmacion.getText().toString())){

                    mAuth.createUserWithEmailAndPassword(correo.getText().toString().trim(), contrasena.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        user1.sendEmailVerification();
                                        Toast.makeText(getActivity(), "Usuario creado.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(getContext(),"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}