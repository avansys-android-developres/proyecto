package com.chaicopaillag.app.mageli.Fragmento;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaicopaillag.app.mageli.Activity.MenuActivity;
import com.chaicopaillag.app.mageli.Activity.PerfilActivity;
import com.chaicopaillag.app.mageli.Modelo.Persona;
import com.chaicopaillag.app.mageli.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilFragment extends Fragment {
    private ScrollView scrollperfil;
    private LinearLayout contenedor_perf,baner_perf;
    private FloatingActionButton fab_editar_perf;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference firebase_bd;
    private ImageView imgUsuario;
    private Persona persona;
    private TextView txtperfil_nombre,
            txtperfil_correo,
            txtperfil_telefono,
            txtperfil_direccion,
            txtperfil_numero_doc,
            txtperfil_nhc,
            txtperfil_fecha_nac,
            txtperfil_genero;
    private  String uid;
    private ProgressDialog progress_carga;
    public PerfilFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progress_carga=new ProgressDialog(getContext(),R.style.progrescolor);
        inicializar_controles();
        inicializar_servicios();
        progres_carga_datos();
    }

    private void progres_carga_datos() {
        progress_carga.setTitle(R.string.app_name);
        progress_carga.setMessage(getString(R.string.carga_perfil));
        progress_carga.setIndeterminate(true);
        progress_carga.setCancelable(false);
        progress_carga.show();
    }

    private void inicializar_servicios() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uid=user.getUid();
        firebase_bd= FirebaseDatabase.getInstance().getReference("Persona").child(uid);
        firebase_bd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                persona= dataSnapshot.getValue(Persona.class);
                if (persona!=null){
                    llenar_datos(persona);
                }else {
                    if (progress_carga.isShowing()){
                        progress_carga.dismiss();
                    }
                    contenedor_perf.setVisibility(View.INVISIBLE);
                    baner_perf.setVisibility(View.INVISIBLE);
                    fab_editar_perf.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.actualiza_perfil), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity().getApplicationContext(), PerfilActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (user.getPhotoUrl()!=null){
            Glide.with(getContext()).load(user.getPhotoUrl()).into(imgUsuario);
        }
    }

    private void inicializar_controles() {
        contenedor_perf=(LinearLayout)getView().findViewById(R.id.contenedor_datos) ;
        baner_perf=(LinearLayout)getView().findViewById(R.id.barner_perfil);
        fab_editar_perf=(FloatingActionButton) getView().findViewById(R.id.fab_editar_perfil);
        txtperfil_nombre=(TextView)getView().findViewById(R.id.perfil_nombre);
        txtperfil_correo=(TextView)getView().findViewById(R.id.perfil_correo);
        txtperfil_telefono=(TextView)getView().findViewById(R.id.pefil_telefono);
        txtperfil_direccion=(TextView)getView().findViewById(R.id.perfil_direccion);
        txtperfil_numero_doc=(TextView)getView().findViewById(R.id.perfil_numero_doc);
        txtperfil_nhc=(TextView)getView().findViewById(R.id.perfil_nhc);
        txtperfil_fecha_nac=(TextView)getView().findViewById(R.id.perfil_fecha_nac);
        txtperfil_genero=(TextView)getView().findViewById(R.id.perfil_genero);
        imgUsuario=(ImageView)getView().findViewById(R.id.img_perfil);
        scrollperfil=(ScrollView)getView().findViewById(R.id.scrollperfil);
        contenedor_perf.setVisibility(View.INVISIBLE);
        baner_perf.setVisibility(View.INVISIBLE);
        fab_editar_perf.setVisibility(View.INVISIBLE);
        fab_editar_perf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(getContext(), PerfilActivity.class);
                inten.putExtra("editar_perfil","ok");
                startActivity(inten);
            }
        });
    }

    private void llenar_datos(Persona persona) {
        txtperfil_nombre.setText(persona.getNombre()+" "+persona.getApellidos());
        txtperfil_correo.setText(persona.getCorreo());
        txtperfil_telefono.setText(persona.getTelefono());
        txtperfil_numero_doc.setText(persona.getNumero_documento());
        txtperfil_nhc.setText(persona.getNumero_hc());
        txtperfil_fecha_nac.setText(persona.getFecha_nacimiento());
        txtperfil_direccion.setText(persona.getDireccion());
        if (persona.isGenero()){
            txtperfil_genero.setText(R.string.masculino);
        }else {
            txtperfil_genero.setText(R.string.femenino);
        }
        if(progress_carga.isShowing()){
            progress_carga.dismiss();
        }
        contenedor_perf.setVisibility(View.VISIBLE);
        baner_perf.setVisibility(View.VISIBLE);
        fab_editar_perf.setVisibility(View.VISIBLE);
    }
    @Override
    public void onStart() {
        super.onStart();

    }
}
