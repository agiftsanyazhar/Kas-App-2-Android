package com.example.kasapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.kasapp2.databinding.ActivityMainBinding;
import com.example.kasapp2.helper.SqliteHelper;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView listKas;

    SwipeRefreshLayout swipe_refresh;
    ArrayList<HashMap<String, String>> arusKas;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    SqliteHelper sqliteHelper;
    Cursor cursor;

    public static String transaksi_id, tgl_awal, tgl_akhir;
    public static boolean filter;
    String queryKas, queryPemasukan, queryPengeluaran, queryTotal;
    public static TextView textFilter;

    TextView pemasukan, pengeluaran, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.fab)
//                        .setAction("Action", null).show();
            }
        });

        listKas = findViewById(R.id.list_kas);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        pemasukan = findViewById(R.id.pemasukan);
        pengeluaran = findViewById(R.id.pengeluaran);
        total = findViewById(R.id.total);
        textFilter = findViewById(R.id.text_filter);
        arusKas = new ArrayList<>();

        sqliteHelper = new SqliteHelper(this);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryKas = "SELECT *, strftime('%d/%m/%Y', tanggal) FROM transaksi ORDER BY id DESC";
                queryPemasukan = "SELECT SUM(jumlah) AS 'MASUK' FROM transaksi WHERE status='Masuk'";
                queryPengeluaran = "SELECT SUM(jumlah) AS 'KELUAR' FROM transaksi WHERE status='Keluar'";
                queryTotal = "SELECT SUM(jumlah) AS 'TOTAL' FROM transaksi";

                kasAdapter();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        queryKas = "SELECT *, strftime('%d/%m/%Y', tanggal) FROM transaksi ORDER BY id DESC";
        queryPemasukan = "SELECT SUM(jumlah) AS 'MASUK' FROM transaksi WHERE status='Masuk'";
        queryPengeluaran = "SELECT SUM(jumlah) AS 'KELUAR' FROM transaksi WHERE status='Keluar'";
        queryTotal = "SELECT SUM(jumlah) AS 'TOTAL' FROM transaksi";

        if (filter) {
            queryKas = "SELECT *, strftime('%d/%m/%Y', tanggal) FROM transaksi " +
                    "WHERE (tanggal >= '" + tgl_awal + "') AND (tanggal <= '" + tgl_akhir + "') " +
                    "ORDER BY id DESC";
            queryPemasukan = "SELECT SUM(jumlah) AS 'MASUK' FROM transaksi WHERE status='Masuk' " +
                    "AND (tanggal >= '" + tgl_awal + "') AND (tanggal <= '" + tgl_akhir + "')";
            queryPengeluaran = "SELECT SUM(jumlah) AS 'KELUAR' FROM transaksi WHERE status='Keluar'" +
                    " AND (tanggal >= '" + tgl_awal + "') AND (tanggal <= '" + tgl_akhir + "')";
            queryTotal = "SELECT SUM(jumlah) AS 'TOTAL' FROM transaksi " +
                    "WHERE (tanggal >= '" + tgl_awal + "') AND (tanggal <= '" + tgl_akhir + "')";
        }

        kasAdapter();
    }

    private void kasAdapter() {
        arusKas.clear();
        listKas.setAdapter(null);

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();
        cursor = db.rawQuery(queryKas, null);
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            HashMap<String, String> map = new HashMap<>();
            map.put("id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
//            map.put("tanggal", cursor.getString(4));
            map.put("tanggal", cursor.getString(5));

            arusKas.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arusKas, R.layout.list_kas,
                new String[]{"id", "status", "jumlah", "keterangan", "tanggal"},
                new int[]{R.id.id, R.id.text_status, R.id.jumlah, R.id.keterangan, R.id.tanggal});

        listKas.setAdapter(simpleAdapter);
        listKas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksi_id = ((TextView) view.findViewById(R.id.id)).getText().toString();

                listMenu();
            }
        });

        kasTotal();
    }

    private void kasTotal() {
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        SQLiteDatabase db = sqliteHelper.getReadableDatabase();

        cursor = db.rawQuery(queryPemasukan, null);
        cursor.moveToFirst();
        double masukSum = cursor.getDouble(cursor.getColumnIndex("MASUK"));
        pemasukan.setText(rupiah.format(masukSum));

        cursor = db.rawQuery(queryPengeluaran, null);
        cursor.moveToFirst();
        double keluarSum = cursor.getDouble(cursor.getColumnIndex("KELUAR"));
        pengeluaran.setText(rupiah.format(keluarSum));

        cursor = db.rawQuery(queryTotal, null);
        cursor.moveToFirst();
        double totalSum = cursor.getDouble(cursor.getColumnIndex("TOTAL"));
        total.setText(rupiah.format(totalSum));

        double difference = masukSum - keluarSum;
        total.setText(rupiah.format(difference));

        swipe_refresh.setRefreshing(false);

        if (!filter) {
            textFilter.setVisibility(View.GONE);
        }
        filter = false;

//        cursor = db.rawQuery("SELECT SUM(jumlah) AS 'TOTAL'," +
//                "(SELECT SUM(jumlah) AS 'MASUK' FROM transaksi WHERE status='Masuk')," +
//                "(SELECT SUM(jumlah) AS 'KELUAR' FROM transaksi WHERE status='Keluar')", null);
//        cursor.moveToFirst();
//
//        pemasukan.setText(rupiah.format(cursor.getDouble(1)));
//        pengeluaran.setText(rupiah.format(cursor.getDouble(2)));
//        total.setText(rupiah.format(cursor.getDouble(1) - cursor.getDouble(2)));
    }

    private void listMenu() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_menu);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView text_edit = dialog.findViewById(R.id.text_edit);
        TextView text_hapus = dialog.findViewById(R.id.text_hapus);

        text_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });
        text_hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                hapus();
            }
        });

        dialog.show();
    }

    private void hapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Peringatan!");
        builder.setMessage("Anda yakin ingin menghapus ini?");
        builder.setPositiveButton(
                "Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM transaksi WHERE id='" + transaksi_id + "'");

                        Toast.makeText(getApplicationContext(), "Data berhasil dihapus", Toast.LENGTH_LONG).show();

                        kasAdapter();
                    }
                });
        builder.setNegativeButton(
                "Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            startActivity(new Intent(this, FilterActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}