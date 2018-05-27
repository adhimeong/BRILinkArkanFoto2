package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfilFragment extends Fragment {

    NetworkImageView imageprev;
    ImageLoader mImageLoader;
    String url = Server.url_server +"app/images/";
    String IMAGE_URL ;
    String fotodefault = "defaultprofile.jpg";

    private ProgressDialog pd;
    String urldata2 = "app/profilpelanggan.php";
    String url2 = Server.url_server +urldata2;

    String urldata = "app/updateprofilpelanggan.php";
    String urlupdate = Server.url_server +urldata;

    EditText editnama, editemail, editkontak, editalamat;
    String idpengguna, nokartu, message;
    int success;

    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100

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
        btnfoto = (Button) view.findViewById(R.id.btnpilihfoto);
        imageprev = (NetworkImageView) view.findViewById(R.id.updateprevimage);

        //getting the current user
        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");

        idpengguna = user.getId_pelanggan();
        nokartu = user.getNo_kartu();

        //memanggil data user
        load_datapelanggan_from_server2();

        //pilih foto
        btnfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihGambar();
            }
        });

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

    private void pilihGambar(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageprev.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                // 100 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 100));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatedatapengguna() {

        final String namaupdate = editnama.getText().toString();
        final String emailupdate = editemail.getText().toString();
        final String kontakupdate = editkontak.getText().toString();
        final String alamatupdate = editalamat.getText().toString();
        final String fotoupdate = getStringImage(decoded);

        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                urlupdate,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.d("Response: ",response.toString());
                        Log.d("foto yang dipilih: ", fotoupdate);
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
                params.put("foto", fotoupdate);
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
                                 String foto = jsonobject.getString("foto");
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

                                if (foto.equals("null")){
                                    mImageLoader = MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
                                    IMAGE_URL = url + String.valueOf(fotodefault);
                                    imageprev.setImageUrl(IMAGE_URL, mImageLoader);
                                }else{
                                    mImageLoader = MySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader();
                                    IMAGE_URL = url + String.valueOf(foto);
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
