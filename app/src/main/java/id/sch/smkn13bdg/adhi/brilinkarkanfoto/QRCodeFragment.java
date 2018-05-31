package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
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
public class QRCodeFragment extends Fragment {

    NetworkImageView imageprev;
    ImageLoader mImageLoader;
    String url = Server.url_server +"app/qrcode/";
    String IMAGE_URL ;
    String fotodefault = "defaultqrc.png";

    private ProgressDialog pd;
    String urldata2 = "app/kartupelanggan.php";
    String url2 = Server.url_server +urldata2;

    TextView viewnama, viewnokartu;
    String idpengguna, nokartu;


    public QRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        viewnama = (TextView) view.findViewById(R.id.kartunama);
        viewnokartu = (TextView) view.findViewById(R.id.kartunokartu);
        imageprev = (NetworkImageView) view.findViewById(R.id.kartuqrcode);

        //getting the current user
        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        idpengguna = user.getId_pelanggan();
        nokartu = user.getNo_kartu();

        //memanggil data user
        load_datapelanggan_from_server2();

        // Inflate the layout for this fragment
        return view;
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
                                String qrcode = jsonobject.getString("qrcode");

                                if (nama.equals("null")){
                                    viewnama.setText("Nama Belum Dimasukan");
                                }else{
                                    viewnama.setText(nama);
                                }

                                if (no_kartu.equals("null")){
                                    viewnokartu.setText("- - - ");
                                }else{
                                    viewnokartu.setText(no_kartu);
                                }

                                if (qrcode.equals("null")){
                                    mImageLoader = MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
                                    IMAGE_URL = url + String.valueOf(fotodefault);
                                    imageprev.setImageUrl(IMAGE_URL, mImageLoader);
                                }else{
                                    mImageLoader = MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
                                    IMAGE_URL = url + String.valueOf(qrcode);
                                    imageprev.setImageUrl(IMAGE_URL, mImageLoader);
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
