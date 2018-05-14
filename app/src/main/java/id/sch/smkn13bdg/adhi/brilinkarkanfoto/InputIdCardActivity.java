package id.sch.smkn13bdg.adhi.brilinkarkanfoto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.barcode.Barcode;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataPerolehanPelangganController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;

public class InputIdCardActivity extends AppCompatActivity {

    public Button loginbtn;
    public EditText result;
    public static final int REQUEST_CODE = 100;
    public static final int PERMISSION_REQUEST = 200;
    private ProgressDialog pd;
    String urldata = "app/login.php";
    String url = Server.url_server +urldata;
    String idkartu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_id_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pd = new ProgressDialog(this);
        pd.setMessage("loading");

        //if the user is already logged in we will directly start the main activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST);
        }

        loginbtn = (Button) findViewById(R.id.btnlogin);
        result = (EditText) findViewById(R.id.editidkartu);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_scan);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ScanCardActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");
                result.post(new Runnable() {

                    @Override
                    public void run() {
                        result.setText(barcode.displayValue);
                    }
                });
            }
        }

    }

    private void userLogin(){
        idkartu = result.getText().toString();
        load_data_from_server();
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

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            FancyToast.makeText(getApplicationContext(),"Terjadi ganguan dengan koneksi server",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show();
                            pd.hide();
                        }
                    }
                }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idkartu", idkartu);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
