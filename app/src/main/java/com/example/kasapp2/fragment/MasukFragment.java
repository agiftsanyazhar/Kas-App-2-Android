package com.example.kasapp2.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.kasapp2.R;
import com.example.kasapp2.helper.Config;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MasukFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasukFragment extends Fragment {

    EditText jumlah, keterangan;
    Button simpan;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MasukFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MasukFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MasukFragment newInstance(String param1, String param2) {
        MasukFragment fragment = new MasukFragment();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_masuk, container, false);

        jumlah = v.findViewById(R.id.jumlah);
        keterangan = v.findViewById(R.id.keterangan);
        simpan = v.findViewById(R.id.simpan);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMySql();
            }
        });

        return v;
    }

    private void insertMySql() {
        AndroidNetworking.get(Config.HOST + "add.php")
                .addQueryParameter("status", "Masuk")
                .addQueryParameter("jumlah", jumlah.getText().toString())
                .addQueryParameter("keterangan", keterangan.getText().toString())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optString("response").equals("success")) {
                            Toast.makeText(getContext(), "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Transaksi gagal disimpan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getContext(), "Error: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}