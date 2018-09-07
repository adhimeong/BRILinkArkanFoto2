package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransaksiFragment extends Fragment {

    private ProgressDialog pd;

    String urldata = "app/prosestransaksi.php";
    String url = Server.url_server +urldata;

    String urldata1 = "app/cektarif.php";
    String url1 = Server.url_server +urldata1;

    EditText notujuan, nominaltransaksi;
    String no_kartu,rektujuan,nominal,transaksi,bank,message,tariftransaksi, statustransaksi;
    Spinner banktujuan, jenistransaksi;
    Button btnproses;
    TextView txttarif, nokartu;

    int success;

    public TransaksiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaksi, container, false);

        banktujuan = (Spinner) view.findViewById(R.id.spinnertransasibank);
        jenistransaksi = (Spinner) view.findViewById(R.id.spinnertransasijenistransaksi);
        nokartu = (TextView) view.findViewById(R.id.transaksinokartu);
        notujuan = (EditText)view.findViewById(R.id.edittransaksitujuan);
        nominaltransaksi = (EditText) view.findViewById(R.id.edittransaksinominal);
        btnproses =(Button) view.findViewById(R.id.btntransasiproses);
        txttarif = (TextView)view.findViewById(R.id.txttarif);

        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        nokartu.setText(user.getNo_kartu());

        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        jenistransaksi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                transaksi = jenistransaksi.getSelectedItem().toString();
                Toast.makeText(getActivity(), "dipilih " + jenistransaksi.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        banktujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nominal = nominaltransaksi.getText().toString();
                bank = banktujuan.getSelectedItem().toString();
                if (bank.equals("BANK TUJUAN")){
                    Toast.makeText(getActivity(), "PILIH BANK TUJUAN", Toast.LENGTH_SHORT).show();
                }else{
                    load_tarif_to_server();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnproses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no_kartu = nokartu.getText().toString();
                rektujuan = notujuan.getText().toString();
                nominal = nominaltransaksi.getText().toString();
                bank = banktujuan.getSelectedItem().toString();
                transaksi = jenistransaksi.getSelectedItem().toString();
                statustransaksi = "antri";
                tariftransaksi = txttarif.getText().toString();

                load_proses_transaksi_to_server(no_kartu, rektujuan, nominal, bank, transaksi, tariftransaksi, statustransaksi);

                Intent i = new Intent(getActivity(), PrintActivity.class);
                i.putExtra("nokartu", no_kartu);
                i.putExtra("rektujuan", rektujuan);
                i.putExtra("nominal", nominal);
                i.putExtra("bank", bank);
                i.putExtra("tarif", tariftransaksi);
                startActivity(i);
            }
        });

        // Inflate the layout for this fragment
        return view;

    }

    public void load_proses_transaksi_to_server( final String a, final String b, final String c, final String d, final String e, final String f, final String g){
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d("Response: ",response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt("success");
                            message = jObj.getString("message");

                            // Cek error node pada json
                            if (success == 1) {
                                Log.d("Add antrian transaksi", jObj.toString());
                                FancyToast.makeText(getActivity().getApplicationContext(),message,FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,true).show();
                            } else {
                                FancyToast.makeText(getActivity().getApplicationContext(),message,FancyToast.LENGTH_LONG, FancyToast.WARNING,true).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                        pd.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            FancyToast.makeText(getActivity().getApplicationContext(),"Terjadi ganguan dengan koneksi server",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show();
                            pd.hide();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String> params = new HashMap<String, String>();
                params.put("no_kartu", a);
                params.put("rektujuan", b);
                params.put("nominal", c);
                params.put("bank", d);
                params.put("jenis_transaksi", e);
                params.put("tariftransaksi", f);
                params.put("status_transaksi", g);
                return params;
            }

        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void load_tarif_to_server(){
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url1,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("string",response);

                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String tarif = jsonobject.getString("tarif_tansaksi").trim();
                                txttarif.setText(tarif.toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pd.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            FancyToast.makeText(getActivity().getApplicationContext(),"Terjadi ganguan dengan koneksi server",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show();
                            pd.hide();
                        }
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("banktujuan", bank);
                params.put("nominal", nominal);
                return params;
            }

        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
