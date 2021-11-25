package net.alecks.helpmewoof.ui.NuevoReporte;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import net.alecks.helpmewoof.databinding.FragmentNuevoReporteBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuevoReporteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuevoReporteFragment extends Fragment {
    private FragmentNuevoReporteBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NuevoReporteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuevoReporteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NuevoReporteFragment newInstance(String param1, String param2) {
        NuevoReporteFragment fragment = new NuevoReporteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNuevoReporteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button boton = binding.iniciarsesionbtn;
        final EditText correo = binding.correo;
        final EditText contrasena = binding.contrasena;

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(correo.getText().toString().trim(), contrasena.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(getActivity(), "Autenticación correcta.",
                                            Toast.LENGTH_SHORT).show();
                                    if (!user.isEmailVerified()){
                                        Toast.makeText(getActivity(),"Correo electronico no verificado", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
        return root;
    }
}