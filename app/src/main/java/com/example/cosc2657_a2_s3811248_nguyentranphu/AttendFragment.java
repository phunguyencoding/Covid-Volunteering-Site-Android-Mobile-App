package com.example.cosc2657_a2_s3811248_nguyentranphu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendFragment extends Fragment {

    FireBaseHandler fireBaseHandler = new FireBaseHandler();
    RecyclerView recyclerView;
    AttendAdapter myAdapter;
    List<SiteModel> siteList;
    ArrayList<Task<QuerySnapshot>> data;
    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AttendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendFragment newInstance(String param1, String param2) {
        AttendFragment fragment = new AttendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_attend, container, false);
        return view;
    }

    @Override //update when resumed
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        siteList = new ArrayList<>();
        data = fireBaseHandler.retrieveAttendSiteData(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        data.get(0).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String location, date, host, description, testedPeople;
                GeoPoint geoPoint;
                int thumbnail;
                //Load participants
                List<String> participants;
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    location = Objects.requireNonNull(documentSnapshot.getData().get("location")).toString();
                    date = Objects.requireNonNull(documentSnapshot.getData().get("date")).toString();
                    host = Objects.requireNonNull(documentSnapshot.getData().get("host")).toString();
                    description = Objects.requireNonNull(documentSnapshot.getData().get("description")).toString();
                    geoPoint = Objects.requireNonNull(documentSnapshot.getGeoPoint("geoPoint"));
                    testedPeople = Objects.requireNonNull(documentSnapshot.get("testedPeople").toString());
                    thumbnail = Integer.parseInt(Objects.requireNonNull(documentSnapshot.getData().get("thumbnail").toString()));
                    SiteModel site = new SiteModel(location, host, date, description, geoPoint, Integer.parseInt(testedPeople),thumbnail);
                    participants = (List<String>) documentSnapshot.get("participants");
                    if (participants != null) {
                        site.setParticipants(participants);
                    }
                    siteList.add(site);
                }
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                myAdapter = new AttendAdapter(AttendFragment.this.getContext(), siteList);
                recyclerView.setLayoutManager(new GridLayoutManager(AttendFragment.this.getContext(), 1));
                recyclerView.setAdapter(myAdapter);
            }
        });
    }
}