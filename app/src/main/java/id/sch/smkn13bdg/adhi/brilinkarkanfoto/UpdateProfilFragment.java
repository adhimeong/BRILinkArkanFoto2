package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
public class UpdateProfilFragment extends Fragment {

    private ProgressDialog pd;
    String urldata2 = "app/profilpelanggan.php";
    String url2 = Server.url_server +urldata2;

    String urldata = "app/updateprofilpelanggan.php";
    String urlupdate = Server.url_server +urldata;

    EditText editnama, editemail, editkontak, editalamat;
    String idpengguna, nokartu, message;
    int success;

    Button btnfoto, btnupdate;

    public UpdateProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_profil, container, false);

        editnama = (EditText) view.findViewById(R.id.updatenama);
        editemail = (EditText) view.findViewById(R.id.updateemail);
        editkontak = (EditText) view.findViewById(R.id.updatekontak);
        editalamat = (EditText) view.findViewById(R.id.updatealamat);
        btnupdate = (Button) view.findViewById(R.id.btnupdate);

        //getting the current user
        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        idpengguna = user.getId_pelanggan();
        nokartu = user.getNo_kartu();

        //memanggil data user
        load_datapelanggan_from_server2();

        //updatedata
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatedatapengguna();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void updatedatapengguna() {

        final String namaupdate = editnama.getText().toString();
        final String emailupdate = editemail.getText().toString();
        final String kontakupdate = editkontak.getText().toString();
        final String alamatupdate = editalamat.getText().toString();

        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                urlupdate,
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
                                Log.d("Add/update", jObj.toString());
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfilFragment()).commit();
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
                params.put("no_kartu", nokartu);
                params.put("nama", namaupdate);
                params.put("email", emailupdate);
                params.put("kontak", kontakupdate);
                params.put("alamat", alamatupdate);
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void load_datapelanggan_from_server2() {
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url2,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("string",response);

                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                 String id_pelanggan = jsonobject.getString("id_pelanggan").trim();
                                 String no_kartu= jsonobject.getString("no_kartu").trim();
                                 String nama = jsonobject.getString("nama_pelanggan").trim();
                                 String password = jsonobject.getString("password").trim();
                                 String alamat = jsonobject.getString("alamat").trim();
                                 String kontak = jsonobject.getString("kontak").trim();
                                 String email = jsonobject.getString("email").trim();

                                 if (nama.equals("null")){
                                     editnama.setText(null);
                                 }else{
                                     editnama.setText(nama);
                                 }

                                if (kontak.equals("null")){
                                    editkontak.setText(null);
                                }else{
                                    editkontak.setText(kontak);
                                }

                                if (email.equals("null")){
                                    editemail.setText(null);
                                }else{
                                    editemail.setText(email);
                                }

                                if (alamat.equals("null")){
                                    editalamat.setText(null);
                                }else{
                                    editalamat.setText(alamat);
                                }
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

        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("no_kartu", nokartu);
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
