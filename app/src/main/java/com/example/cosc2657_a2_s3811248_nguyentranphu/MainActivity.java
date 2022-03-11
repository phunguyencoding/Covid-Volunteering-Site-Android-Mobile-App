package com.example.cosc2657_a2_s3811248_nguyentranphu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//    ViewPager Screen slides are transitions between one entire screen to another and are common
//    with UIs like setup wizards or slideshows.
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CreateFragment createFragment;
    private AttendFragment attendFragment;
    private FloatingActionButton addButton, mapButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attendFragment = new AttendFragment();
        createFragment = new CreateFragment();
        toolbar = findViewById(R.id.mainToolbar);
//        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.mainTabLayout);
        mapButton = findViewById(R.id.mapBtn);
        addButton = findViewById(R.id.addBtn);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        viewPagerAdapter.addFragment(attendFragment, "Attend List");
        viewPagerAdapter.addFragment(createFragment, "Create List");
        viewPager.setAdapter(viewPagerAdapter);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewSiteActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<String> fragmentName =  new ArrayList<>();
        private List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String name) {
            fragments.add(fragment);
            fragmentName.add(name);
        }

        public void clearFragment() {
            fragments.clear();
            fragmentName.clear();
        }

        @Override
        public int getCount() {

            return fragments.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            return fragmentName.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_signout,null);
        Button yes = (Button) view.findViewById(R.id.yes_button);
        Button no = (Button) view.findViewById(R.id.no_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                finish();
                alertDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 100) {
            if (resultCode==RESULT_OK) {
                String res = "";
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    res = (String) bundle.get("resMessage");
                }
                Toast.makeText(MainActivity.this,res,Toast.LENGTH_SHORT).show();
            }
        }
    }
}