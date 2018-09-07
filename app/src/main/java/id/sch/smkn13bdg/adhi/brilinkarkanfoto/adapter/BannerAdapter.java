package id.sch.smkn13bdg.adhi.brilinkarkanfoto.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.R;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.DataBannerController;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.MySingleton;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley.Server;

/**
 * Created by adhi on 29/05/18.
 */

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<DataBannerController> bannerImg;
    private ImageLoader imageLoader;
    String url = Server.url_server +"app/banner/";
    String IMAGE_URL ;

    public BannerAdapter(List bannerImg,Context context) {
        this.bannerImg = bannerImg;
        this.context = context;
    }


    @Override
    public int getCount() {
        return bannerImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.custom_layout, null);

        DataBannerController banner = bannerImg.get(position);

        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.imageView);

        imageLoader = MySingleton.getInstance(context).getImageLoader();
        IMAGE_URL = url + String.valueOf(banner.getFoto_banner());
        imageView.setImageUrl(IMAGE_URL,imageLoader);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
