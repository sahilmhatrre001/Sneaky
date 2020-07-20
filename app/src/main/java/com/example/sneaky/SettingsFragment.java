package com.example.sneaky;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private TextView username;
    private FirebaseUser user;
    View view;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_settings, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        username = (TextView) view.findViewById(R.id.usernamet);
        username.setText(user.getEmail());
        String [] settings_arr = {"Radius","Turn off notifications","Turn On notifications","Logout"};
        Integer [] images = {
                R.drawable.ic_radius,
                R.drawable.ic_notification_off,
                R.drawable.ic_notification_on,
                R.drawable.ic_logout_settings


        };
        final CustomList adapter = new
                CustomList(view.getContext(),settings_arr,images);
        ListView listView = view.findViewById(R.id.SettingList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value= adapter.getItem(position);

                switch (value)
                {
                    case "Logout":
                        logout();
                        break;
                    case "Radius":
                        set_radius();
                        break;
                    case "Turn off notifications":
                        Intent serviceIntent = new Intent(getActivity(),ExampleServie.class);
                        getActivity().stopService(serviceIntent);
                        break;
                    case "Turn On notifications":
                        Intent serviceIntent1 = new Intent(getActivity(),ExampleServie.class);
                        getActivity().startService(serviceIntent1);
                        return;
                }
            }
        });

        return view;
    }

    private void set_radius() {
        
    }

    private void  logout()
    {
        getActivity().finish();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent i = new Intent(getContext(),login.class);
        startActivity(i);
    }

}
