package id.sch.smkn13bdg.adhi.brilinkarkanfoto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter.BannerAdapter;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter.DataPerolehanPelangganAdapter;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataBannerController;
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
    String urldata3 = "app/tampilbanner.php";
    String url3 = Server.url_server +urldata3;
    String urldata2 = "app/perolehanpoint.php";
    String url2 = Server.url_server +urldata2;
    String urldata = "app/perolehansemuapoint.php";
    String url = Server.url_server +urldata;
    String no_kartu;

    //list costume adapter
    List<DataPerolehanPelangganController> dataController = new ArrayList<DataPerolehanPelangganController>();
    DataPerolehanPelangganAdapter adapter;

    List<DataBannerController> databannerController = new ArrayList<DataBannerController>();
    BannerAdapter bannerAdapter;
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    ListView listView;

    public UtamaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_utama, container, false);

        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        no_kartu = user.getNo_kartu();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        listView = (ListView) view.findViewById(R.id.listview02);
        adapter = new DataPerolehanPelangganAdapter(dataController, getActivity());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pointperolehan = (TextView) view.findViewById(R.id.txtpoint);
        tglpasif = (TextView) view.findViewById(R.id.texttglpasif);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_radio_button_unchecked_black_24dp));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_radio_button_checked_black_24dp));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //load data
        load_data_from_server2();
        load_data_from_server();
        load_banner_from_server();

        return view;
    }


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

    public void load_banner_from_server(){
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url3,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("string",response);

                        try {

                            bannerAdapter = new BannerAdapter(databannerController, getActivity());

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                //String id_banner = jsonobject.getString("id,_banner").trim();
                                //String nama_banner = jsonobject.getString("nama_banner").trim();
                                String foto_banner = jsonobject.getString("foto_banner").trim();
                                String fotobanner = foto_banner.toString();

                                DataBannerController d4 = new DataBannerController();
                                d4.setFoto_banner(fotobanner);
                                databannerController.add(d4);

                                Log.d("urlbanner",fotobanner);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        viewPager.setAdapter(bannerAdapter);

                        dotscount = bannerAdapter.getCount();
                        dots = new ImageView[dotscount];

                        for(int i = 0; i < dotscount; i++){

                            dots[i] = new ImageView(getActivity());
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_radio_button_unchecked_black_24dp));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(8, 0, 8, 0);

                            sliderDotspanel.addView(dots[i], params);
                        }

                        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_radio_button_checked_black_24dp));
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
