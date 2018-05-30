package id.sch.smkn13bdg.adhi.brilinkarkanfoto;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter.DataHadiahAdapter;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataHadiahController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


/**
 * A simple {@link Fragment} subclass.
 */
public class PointKuFragment extends Fragment {

    //progres chart
    ArcProgress arcProgress1;

    private ProgressDialog pd;

    //volley
    String urldata = "app/service_hadiah.php";
    String url = Server.url_server +urldata;

    String urldata2 = "app/perolehanpoint.php";
    String url2 = Server.url_server +urldata2;
    String no_kartu;

    //list costume adapter
    List<DataHadiahController> dataController = new ArrayList<DataHadiahController>();
    DataHadiahAdapter adapter;
    ListView listView;
    //setting
    TextView tanggalpasif, pointperolehan, sisaproses;
    int pointperolehanint;


    public PointKuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pointku, container, false);

        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        no_kartu = user.getNo_kartu();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        arcProgress1 = (ArcProgress) view.findViewById(R.id.arc_progress);
        pointperolehan = (TextView) view.findViewById(R.id.textpointku);
        tanggalpasif = (TextView)view.findViewById(R.id.texttanggalpasif);
        sisaproses = (TextView) view.findViewById(R.id.textsisatransaksi);

        listView = (ListView)view.findViewById(R.id.listview01);
        dataController.clear();

        adapter = new DataHadiahAdapter(dataController, getActivity() );

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String datajumlahpoint = dataController.get(position).getJumlah_point();
                int pointhadiah = Integer.parseInt(datajumlahpoint);

                if(pointperolehanint >= pointhadiah){
                    //setting grafik
                    arcProgress1.setMax(100);
                    arcProgress1.setProgress(100);
                    //seting selisih
                    sisaproses.setText("Selamat Anda berhasil ");
                    //pengumaman
                    final PrettyDialog pDialog = new PrettyDialog(getActivity());
                    pDialog
                            .setTitle("SELAMAT")
                            .setTitleColor(R.color.colorAccent)
                            .setMessage("ANDA MAU MENUKARKAN POINT ANDA ?")
                            .setAnimationEnabled(false)
                            .setIcon(
                                    R.drawable.pdlg_icon_info,     // icon resource
                                    R.color.colorAccent,      // icon tint
                                    new PrettyDialogCallback() {   // icon OnClick listener
                                        @Override
                                        public void onClick() {
                                            pDialog.dismiss();
                                        }
                                    })
                            .addButton(
                                    "YA",     // button text
                                    R.color.pdlg_color_white,  // button text color
                                    R.color.colorPrimaryDark,  // button background color
                                    new PrettyDialogCallback() {  // button OnClick listener
                                        @Override
                                        public void onClick() {
                                            // Do what you gotta do
                                        }
                                    }
                            )
                            .addButton(
                                    "TIDAK",
                                    R.color.pdlg_color_white,
                                    R.color.colorAccent,
                                    new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            pDialog.dismiss();
                                        }
                                    }
                            )
                            .show();


                }else{
                    //perhitungan persentase
                    float a = Float.intBitsToFloat(pointperolehanint);
                    float b = Float.intBitsToFloat(pointhadiah);
                    float proses = (a/b)*100;
                    int hasilproses = (int)proses;
                    //setting grafik proses
                    arcProgress1.setMax(100);
                    arcProgress1.setProgress(hasilproses);
                    //setting selisih
                    int selisihint = pointhadiah - pointperolehanint;
                    String selisih = String.valueOf(selisihint);
                    sisaproses.setText("Anda perlu " + selisih + " transaksi lagi ");
                    String sisaprosestxt = "Anda perlu " + selisih + " transaksi lagi ";

                    FancyToast.makeText(getActivity().getApplicationContext(),sisaprosestxt,FancyToast.LENGTH_SHORT, FancyToast.WARNING,true).show();
                }

            }
        });

        //load data
        load_data_from_server2();
        load_data_from_server();


        // Inflate the layout for this fragment
        return view;
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

                                String id_hadiah = jsonobject.getString("id_hadiah").trim();
                                String nama_hadiah = jsonobject.getString("nama_hadiah").trim();
                                String foto_hadiah = jsonobject.getString("foto_hadiah").trim();
                                String jumlah_point = jsonobject.getString("jumlah_point").trim();

                                DataHadiahController d1 = new DataHadiahController();
                                d1.setId_hadiah(id_hadiah.toString());
                                d1.setNama_hadiah(nama_hadiah.toString());
                                d1.setFoto_hadiah(foto_hadiah.toString());
                                d1.setJumlah_point(jumlah_point.toString());

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

                                tanggalpasif.setText(tanggal_pasif);
                                pointperolehan.setText(skor_point);
                                pointperolehanint = Integer.parseInt(skor_point);

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

}
