package com.example.rifat.classroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.rifat.classroom.AccountActivity.Login;
import com.example.rifat.classroom.UnderFragments.MessageFragment;
import com.example.rifat.classroom.UnderFragments.RoutineFragment;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderContactFragment.FindFriendActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainNavigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference UserRef;
    private FirebaseAuth mAuth;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private TextView headerName,headerStatus;
    private CircleImageView headerImage;


    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        //startActivity(new Intent(MainNavigation.this,MainConversationActivity_v2.class));
        //getSupportActionBar().setElevation(0);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Class Room" + "</font>"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        headerName = (TextView)hView.findViewById(R.id.header_profile_name);
        headerStatus = (TextView)hView.findViewById(R.id.header_profile_status);
        headerImage = (CircleImageView) hView.findViewById(R.id.header_profile_image);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RetriveUserInfo();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RoutineFragment()).commit();
            navigationView.setCheckedItem(R.id.routine);
        }

    }

    private void RetriveUserInfo() {
        mAuth = FirebaseAuth.getInstance();
        UserRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("name")) headerName.setText(dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.hasChild("status")) headerStatus.setText(dataSnapshot.child("status").getValue().toString());
                    if(dataSnapshot.hasChild("image")) {
                        String downloadurl = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(downloadurl).into(headerImage);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.routine:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RoutineFragment()).commit();
                break;

            case R.id.search:
                Intent serarchfriend = new Intent(getApplicationContext(), FindFriendActivity.class);
                startActivity(serarchfriend);
                break;
            case R.id.message:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MessageFragment()).commit();
                break;


            case R.id.logout:
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                MainNavigation.this.finish();
                break;

            case R.id.account:
                Intent AccountSettings = new Intent(getApplicationContext(), AccountSettingsActivity.class);
                startActivity(AccountSettings);
                break;


        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }


}
