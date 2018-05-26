package id.sch.smkn13bdg.adhi.brilinkarkanfoto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    TextView idpengguna, nama, nokartu;
    Button logoutbtn, updatebtn;


    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        idpengguna = (TextView) view.findViewById(R.id.txtprofilid);
        nama = (TextView) view.findViewById(R.id.txtprofilnama);
        nokartu = (TextView) view.findViewById(R.id.txtprofilnokartu);
        logoutbtn = (Button) view.findViewById(R.id.btnlogout);
        updatebtn = (Button) view.findViewById(R.id.btnupdateprofil);


        //getting the current user
        UserController user = SharedPrefManager.getInstance(getActivity().getApplicationContext()).getUser();

        idpengguna.setText(user.getId_pelanggan());
        nama.setText(user.getNama_pelanggan());
        nokartu.setText(user.getNo_kartu());

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(getActivity().getApplicationContext()).logout();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new UpdateProfilFragment()).commit();
            }
        });

        return view;
    }

}
