package com.chaicopaillag.app.mageli.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaicopaillag.app.mageli.R;
import com.chaicopaillag.app.mageli.frame.CitasFragment;
import com.chaicopaillag.app.mageli.frame.ConsultasFragment;
import com.chaicopaillag.app.mageli.frame.CuentaFragment;
import com.chaicopaillag.app.mageli.frame.InicioFragment;
import com.chaicopaillag.app.mageli.frame.PacientesFragment;
import com.chaicopaillag.app.mageli.frame.PediatrasFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    NavigationView menu_slide;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    TextView nombreUser,correoUser;
    ImageView imgUsuario;
    View nav_cabecera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, (GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.abrir_nav, R.string.cerar_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        menu_slide = (NavigationView) findViewById(R.id.nav_view);
        nav_cabecera=(View)menu_slide.getHeaderView(0);

        nombreUser=(TextView) nav_cabecera.findViewById(R.id.nombreUsuario);
        correoUser=(TextView)nav_cabecera.findViewById(R.id.correoUsuario);
        imgUsuario=(ImageView)nav_cabecera.findViewById(R.id.imgUsuario);

        menu_slide.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean transframe=false;
                Fragment mi_fragmento=null;
                int id = item.getItemId();
                switch (id){
                    case R.id.item_pediatra:
                        mi_fragmento=new PediatrasFragment();
                        transframe=true;
                        break;
                    case R.id.item_paciente:
                        mi_fragmento=new PacientesFragment();
                        transframe=true;
                        break;
                    case R.id.item_citas:
                        mi_fragmento=new CitasFragment();
                        transframe=true;
                        break;
                    case R.id.item_consulta:
                        mi_fragmento=new ConsultasFragment();
                        transframe=true;
                        break;
                    case R.id.item_cuenta:
                        mi_fragmento=new CuentaFragment();
                        transframe=true;
                        break;
                    case R.id.item_salir:
                        SalirMenu();
                        break;
                }
                if (transframe){
                    ponerFragmento(mi_fragmento);
                    getSupportActionBar().setTitle(item.getTitle());
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        ponerFragmento(new InicioFragment());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    colocar_datos_usuario(user);
                } else {
                    Ir_a_login();
                }
            }
        };
    }

    private void ponerFragmento(Fragment mi_fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor,mi_fragmento)
                .commit();
    }
    private void colocar_datos_usuario(FirebaseUser user) {
        nombreUser.setText(user.getDisplayName());
        correoUser.setText(user.getEmail());
        if (user.getPhotoUrl()!=null){
            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(imgUsuario);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    private void SalirMenu() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Toast.makeText(getApplicationContext(), R.string.sesion_cerado_google, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_sesion_google, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Ir_a_login();
    }
    private void Ir_a_login() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}