package com.example.cosc2657_a2_s3811248_nguyentranphu;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FireBaseHandler {
    User user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FireBaseHandler(){
    }

    public void addUser(String name, String email, String address) {
        User user = new User(name, email, address);
        db.collection("users").document(email).set(user);
    }

    public void addNewSite(String host, String location, String date, String description, GeoPoint geoPoint) {
        SiteModel site = new SiteModel(location, host, date, description, geoPoint, ThumbnailPicker.randomPick());
        site.addParticipant(host);
        db.collection("sites").document(location).set(site);
    }

    public ArrayList<Task<QuerySnapshot>> retrieveSiteData() {
        final ArrayList<Task<QuerySnapshot>> data = new ArrayList<Task<QuerySnapshot>>();
        data.add(db.collection("sites").get());
        return data;
    }

    public ArrayList<Task<QuerySnapshot>> retrieveUserData() {
        final ArrayList<Task<QuerySnapshot>> data = new ArrayList<Task<QuerySnapshot>>();
        data.add(db.collection("users").get());
        return data;
    }

    public ArrayList<Task<QuerySnapshot>> retrieveHostSiteData(String email) {
        final ArrayList<Task<QuerySnapshot>> data = new ArrayList<Task<QuerySnapshot>>();
        data.add(db.collection("sites").whereEqualTo("host",email).get());
        return data;
    }

    public ArrayList<Task<QuerySnapshot>> retrieveAttendSiteData(String email) {
        final ArrayList<Task<QuerySnapshot>> data = new ArrayList<Task<QuerySnapshot>>();
        data.add(db.collection("sites").whereNotEqualTo("host",email).whereArrayContains("participants",email).get());
        return data;
    }

    public void unregister(String location, String email) {
        db.collection("sites")
                .document(location).update("participants", FieldValue.arrayRemove(email));
    }


    public void updateIsNewUser(String email){
        db.collection("users")
                .document(email).update("isNewUser", false);
    }

    public void updateSiteTestedPeople(String location, int testedPeople){
        db.collection("sites")
                .document(location).update("testedPeople", testedPeople);
    }

    public void updateParticipant(String location, String email) {
        db.collection("sites")
                .document(location).update("participants", FieldValue.arrayUnion(email));
    }

    public void deleteSite(String location){
        db.collection("sites")
                .document(location).delete();
    }

}
