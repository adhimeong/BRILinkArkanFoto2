package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter.DataPerolehanPelangganAdapter;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataPerolehanPelangganController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;


/**
 * A simple {@link Fragment} subclass.
 */
public class UtamaFragment extends Fragment {

    TextView pointperolehan, tglpasif;
    private ProgressDialog pd;
    String urldata2 = "app/perolehanpoint.php";
    String url2 = Server.url_server +urldata2;
    String urldata = "app/perolehansemuapoint.php";
    String url = Server.url_server +urldata;
    String no_kartu;

    //list costume adapter
    List<DataPerolehanPelangganController> dataController = new ArrayList<DataPerolehanPelangganController>();
    DataPerolehanPelangganAdapter adapter;
    ListView listView;


    CarouselView carouselView;
    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};


    public UtamaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_utama, container, false);

        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        no_kartu = user.getNo_kartu();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        carouselView = (CarouselView) view.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        listView = (ListView) view.findViewById(R.id.listview02);

        adapter = new DataPerolehanPelangganAdapter(dataController, getActivity());

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pointperolehan = (TextView) view.findViewById(R.id.txtpoint);
        tglpasif = (TextView) view.findViewById(R.id.texttglpasif);

        //load data
        load_data_from_server2();
        load_data_from_server();

        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    public void load_data_from_server2() {
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

                                String nama_pelanggan = jsonobject.getString("nama_pelanggan").trim();
                                String tanggal_pasif = jsonobject.getString("tanggal_pasif").trim();
                                String skor_point = jsonobject.getString("jumlah_point").trim();

                                pointperolehan.setText(skor_point);
                                tglpasif.setText(tanggal_pasif);

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
                params.put("no_kartu", no_kartu);
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }




    public void load_data_from_server() {
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("string",response);

                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String tglpasif = jsonobject.getString("tanggal_pasif").trim();
                                String nama_pelanggan = jsonobject.getString("nama_pelanggan").trim();
                                String foto_pelanggan = jsonobject.getString("foto_pelanggan").trim();
                                String jumlah_point_pelanggan = jsonobject.getString("jumlah_point").trim();

                                DataPerolehanPelangganController d1 = new DataPerolehanPelangganController();
                                d1.setTanggal_pasif(tglpasif.toString());
                                d1.setNama_pelanggan(nama_pelanggan.toString());
                                d1.setFoto_pelanggan(foto_pelanggan.toString());
                                d1.setJumlah_point(jumlah_point_pelanggan.toString());

                                dataController.add(d1);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();

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

        );

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
