package com.example.rdvmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MainActivity extends BaseActivity {

    private RDVDAO rdvDAO;
    private RDVAdapter rdvAdapter;
    private ListView rdvListView;

    private static int visibleActivityCount = 0;

    private List<RDV> rdvs;

    private Handler handler = new Handler();
    private Runnable checkRDVsRunnable = new Runnable() {
        @Override
        public void run() {
            checkAndUpdateRDVs();
            handler.postDelayed(this, 1000); // Check every minute
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("requestPostNotificationsPermission", false)) {
            requestPostNotificationsPermission();
        }

        rdvDAO = new RDVDAO(this);
        rdvListView = findViewById(R.id.rdv_listview);
        rdvDAO.open();
        List<RDV> rdvList = rdvDAO.getAllRDVs();
        rdvDAO.close();
        rdvAdapter = new RDVAdapter(MainActivity.this, rdvList);
        rdvListView.setAdapter(rdvAdapter);


        rdvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RDV rdv = (RDV) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "ID du RDV: " + rdv.getId(), Toast.LENGTH_SHORT).show();
                Intent editIntent = new Intent(MainActivity.this, EditRDVActivity.class);
                editIntent.putExtra("rdv_Id", rdv.getId());
                startActivity(editIntent);
            }
        });
    }

    public void onAddRDVClick(View view) {
        Intent intent = new Intent(this, AddRDVActivity.class);
        startActivity(intent);
    }

    private void refreshRDVList() {
        rdvDAO.open();
        rdvs = rdvDAO.getAllRDVs();
        rdvDAO.close();
        rdvAdapter = new RDVAdapter(this, rdvs);
        rdvListView.setAdapter(rdvAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshRDVList();

        handler.post(checkRDVsRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(checkRDVsRunnable);
    }

    public static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    public void requestPostNotificationsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_POST_NOTIFICATIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission de montrer les notifications refusée", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rdvDAO.close();
    }

    private void checkAndUpdateRDVs() {
        boolean updated = false;

        for (RDV rdv : rdvs) {
            if (!rdv.isDone() && RDV.isRDVOverdue(rdv.getDate(), rdv.getTime())) {
                rdv.setDone(true);
                rdvDAO.open();
                rdvDAO.updateRDV(rdv);
                rdvDAO.close();
                updated = true;
            }
        }

        if (updated) {
            refreshRDVList();
        }
    }

}
