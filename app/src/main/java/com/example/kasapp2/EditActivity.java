package com.example.kasapp2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.kasapp2.helper.Config;
import com.example.kasapp2.helper.CurrentDate;
import com.example.kasapp2.helper.SqliteHelper;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EditActivity extends AppCompatActivity {

    RadioGroup radio_status;
    RadioButton masuk, keluar;
    EditText jumlah, keterangan, edit_tanggal;
    Button simpan;

    String notifStatus, tangal;

    SqliteHelper sqliteHelper;
    Cursor cursor;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        sqliteHelper = new SqliteHelper(this);

        radio_status = findViewById(R.id.radio_status);
        masuk = findViewById(R.id.masuk);
        keluar = findViewById(R.id.keluar);
        jumlah = findViewById(R.id.jumlah);
        keterangan = findViewById(R.id.keterangan);
        edit_tanggal = findViewById(R.id.edit_tanggal);
        simpan = findViewById(R.id.simpan);

//        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
//        cursor = db.rawQuery("SELECT *, strftime('%d/%m/%Y %H:%M:%S', tanggal) FROM transaksi WHERE id='" + MainActivity.transaksi_id + "'", null);
//        cursor.moveToFirst();

        notifStatus = MainActivity.status;
        switch (notifStatus) {
            case "Masuk":
                masuk.setChecked(true);
                break;
            case "Keluar":
                keluar.setChecked(true);
                break;
        }

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

        jumlah.setText(MainActivity.jumlah);
        keterangan.setText(MainActivity.keterangan);

        tangal = MainActivity.tanggal;
        edit_tanggal.setText(MainActivity.tanggal2);
        edit_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        tangal = year + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);

                        edit_tanggal.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month) + "/" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);

                datePickerDialog.show();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                    simpanEdit();

                    AndroidNetworking.get(Config.HOST + "update.php")
                            .addQueryParameter("transaksi_id", MainActivity.transaksi_id)
                            .addQueryParameter("status", notifStatus)
                            .addQueryParameter("jumlah", jumlah.getText().toString())
                            .addQueryParameter("keterangan", keterangan.getText().toString())
                            .addQueryParameter("tanggal", tangal)
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response.optString("response").equals("success")) {
                                        Toast.makeText(getApplicationContext(), "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Transaksi gagal disimpan", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Toast.makeText(getApplicationContext(), "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void simpanEdit() {
        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
        database.execSQL("UPDATE transaksi SET status='" + notifStatus + "', " +
                "jumlah=" + "'" + jumlah.getText().toString() + "', " +
                "keterangan=" + "'" + keterangan.getText().toString() + "', " +
                "tanggal='" + tangal + "' " +
                "WHERE id='" + MainActivity.transaksi_id + "'");

        Toast.makeText(getApplicationContext(), "Transaksi berhasil disimpan", Toast.LENGTH_LONG).show();

        finish();
    }
}