package com.example.rdvmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RDVDAO rdvDAO;
    private RDVAdapter rdvAdapter;
    private ListView rdvListView;

    private List<RDV> rdvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rdvDAO = new RDVDAO(this);
        rdvListView = findViewById(R.id.rdv_listview);

        // Récupération de tous les RDVs enregistrés
        List<RDV> rdvList = rdvDAO.getAllRDVs();
        //rdvAdapter = new RDVAdapter(MainActivity.this, rdvList);
        //rdvListView.setAdapter(rdvAdapter);

        // Lorsqu'on clique sur un RDV, on peut le modifier
        rdvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RDV rdv = (RDV) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, AddRDVActivity.class);
                intent.putExtra("rdvId", rdv.getId());
                startActivity(intent);
            }
        });
    }

    /**
     * Appelée lorsqu'on clique sur le bouton "Ajouter un RDV"
     * @param view la vue actuelle
     */
    public void onAddRDVClick(View view) {
        Intent intent = new Intent(this, AddRDVActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve the latest RDVs from the database and update the RecyclerView
        rdvs = rdvDAO.getAllRDVs();
        RDVAdapter adapter = new RDVAdapter(rdvs, new RDVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RDV rdv) {
                // Handle item click
            }

            @Override
            public void onLocationButtonClick(RDV rdv) {

            }

            @Override
            public void onPhoneButtonClick(RDV rdv) {

            }

            @Override
            public void onShareButtonClick(RDV rdv) {

            }
        });
        rdvListView.setAdapter((ListAdapter) adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fermeture de la connexion à la base de données
        rdvDAO.close();
    }
}
