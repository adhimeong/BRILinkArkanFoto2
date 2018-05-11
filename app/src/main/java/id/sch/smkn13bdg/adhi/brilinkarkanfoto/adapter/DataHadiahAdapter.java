package id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.R;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataHadiahController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;

import static java.security.AccessController.getContext;

/**
 * Created by adhi on 07/05/18.
 */

public class DataHadiahAdapter extends BaseAdapter {


    private List<DataHadiahController> data;
    Activity activity;
    TextView idhadiah, namahadiah, jumlahpoint;
    NetworkImageView fotohadiah;
    ImageLoader mImageLoader;
    String url = Server.url_server +"img/hadiah/";
    String IMAGE_URL ;

    public DataHadiahAdapter(Activity activity, List<DataHadiahController> data) {
        super();
        this.data = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int location) {
        return data.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(activity);
            v = vi.inflate(R.layout.listdatahadiah, null);

            idhadiah = (TextView) v.findViewById(R.id.listidhadiah);
            namahadiah = (TextView) v.findViewById(R.id.listnamahadiah);
            fotohadiah = (NetworkImageView) v.findViewById(R.id.listfotohadiah);
            jumlahpoint = (TextView) v.findViewById(R.id.listjumlah_point);

            DataHadiahController d = data.get(position);

            idhadiah.setText(String.valueOf(d.getId_hadiah()));
            namahadiah.setText(String.valueOf(d.getNama_hadiah()));
            jumlahpoint.setText(String.valueOf(d.getJumlah_point()));

            mImageLoader = MySingleton.getInstance(this.activity.getApplicationContext()).getImageLoader();
            IMAGE_URL = url + String.valueOf(d.getFoto_hadiah());
            fotohadiah.setImageUrl(IMAGE_URL, mImageLoader);


        }else{

        }

        return v;
    }

}
