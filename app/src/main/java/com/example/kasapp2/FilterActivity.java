package com.example.kasapp2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kasapp2.helper.CurrentDate;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FilterActivity extends AppCompatActivity {

    EditText tanggalAwal, tanggalAkhir;

    Button filter;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        tanggalAwal = findViewById(R.id.tanggal_awal);
        tanggalAkhir = findViewById(R.id.tanggal_akhir);
        filter = findViewById(R.id.filter);

        tanggalAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        MainActivity.tgl_awal = year + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);

                        tanggalAwal.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month) + "/" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        tanggalAkhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        MainActivity.tgl_akhir = year + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);

                        tanggalAkhir.setText(numberFormat.format(dayOfMonth) + "/" + numberFormat.format(month) + "/" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tanggalAwal.getText().toString().equals("") || tanggalAkhir.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Isi data terlebih dahulu", Toast.LENGTH_LONG).show();
                } else {
                    MainActivity.filter = true;
                    MainActivity.textFilter.setText(tanggalAwal.getText().toString() + " - " + tanggalAkhir.getText().toString());
                    MainActivity.textFilter.setVisibility(View.VISIBLE);

                    finish();
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
}