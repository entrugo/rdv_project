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
import android.util.Log;

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
        rdvDAO.open();
        // Récupération de tous les RDVs enregistrés
        List<RDV> rdvList = rdvDAO.getAllRDVs();
        rdvDAO.close();
        //rdvAdapter = new RDVAdapter(MainActivity.this, rdvList);
        //rdvListView.setAdapter(rdvAdapter);

        // Lorsqu'on clique sur un RDV, on peut le modifier
        rdvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RDV rdv = (RDV) parent.getItemAtPosition(position);
                Log.d("MainActivity", "RDV ID: " + rdv.getId());
                Intent editIntent = new Intent(MainActivity.this, EditRDVActivity.class);
                editIntent.putExtra("rdvId", rdv.getId());
                startActivity(editIntent);
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
        rdvDAO.open();
        // Retrieve the latest RDVs from the database and update the RecyclerView
        rdvs = rdvDAO.getAllRDVs();
        rdvDAO.close();
        RDVAdapter adapter = new RDVAdapter(this, rdvs);
        rdvListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fermeture de la connexion à la base de données
        rdvDAO.close();
    }
}
