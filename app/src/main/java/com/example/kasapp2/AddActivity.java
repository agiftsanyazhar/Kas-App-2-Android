package com.example.kasapp2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.kasapp2.helper.SqliteHelper;

public class AddActivity extends AppCompatActivity {

    RadioGroup radio_status;
    RadioButton masuk, keluar;
    EditText jumlah, keterangan;
    Button simpan;

    String notifStatus;

    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        notifStatus = "";

        sqliteHelper = new SqliteHelper(this);

        radio_status = findViewById(R.id.radio_status);
        masuk = findViewById(R.id.masuk);
        keluar = findViewById(R.id.keluar);
        jumlah = findViewById(R.id.jumlah);
        keterangan = findViewById(R.id.keterangan);
        simpan = findViewById(R.id.simpan);

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                if (i == R.id.masuk) {
                    notifStatus = "Masuk";
                } else if (i == R.id.keluar) {
                    notifStatus = "Keluar";
                }
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notifStatus.equals("") || jumlah.equals("") || keterangan.equals("")) {
                    Toast.makeText(getApplicationContext(), "Isi data terlebih dahulu", Toast.LENGTH_LONG).show();
                    radio_status.requestFocus();
                } else if (notifStatus.equals("")) {
                    Toast.makeText(getApplicationContext(), "Status harus diisi", Toast.LENGTH_LONG).show();
                    radio_status.requestFocus();
                } else if (jumlah.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Jumlah harus diisi", Toast.LENGTH_LONG).show();
                    jumlah.requestFocus();
                } else if (keterangan.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Keterangan harus diisi", Toast.LENGTH_LONG).show();
                    keterangan.requestFocus();
                } else {
                    simpanData();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Tambah");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void simpanData() {
        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
        database.execSQL("INSERT INTO transaksi  (status, jumlah, keterangan) VALUES ('" + notifStatus + "', " +
                "'" + jumlah.getText().toString() + "', " +
                "'" + keterangan.getText().toString() + "')");

        Toast.makeText(getApplicationContext(), "Transaksi berhasil disimpan", Toast.LENGTH_LONG).show();

        finish();
    }
}