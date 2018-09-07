package id.sch.smkn13bdg.adhi.brilinkarkanfoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;


/**
 * A simple {@link Fragment} subclass.
 */
public class QRCodeFragment extends Fragment {

    public static String KEY_FRG = "msg_fragment";

    String nokartu;
    String idhadiah;

    public QRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);

        //getting the current user
        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();
        nokartu = user.getNo_kartu();

        //ambil data dari fragment pointku
        idhadiah = getArguments().getString("idhadiah");

        ImageView image = (ImageView) view.findViewById(R.id.qrcode);

        String text2Qr = nokartu+","+idhadiah;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {

            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return view;
    }

}
