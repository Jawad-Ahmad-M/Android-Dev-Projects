package com.skillbuilder.app.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.skillbuilder.app.R;
import com.skillbuilder.app.databinding.ActivityMainBinding;
import com.skillbuilder.app.fragments.DashboardFragment;
import com.skillbuilder.app.fragments.SkillListFragment;
import com.skillbuilder.app.fragments.ProgressFragment;
import com.skillbuilder.app.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.skillbuilder.app.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setContentView(R.layout.activity_main);

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Handle bottom navigation clicks
        binding.navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            }else if (item.getItemId() == R.id.nav_skills) {
                selectedFragment = new SkillListFragment();
            } else if (item.getItemId() == R.id.nav_progress) {
                selectedFragment = new ProgressFragment();
            } else if (item.getItemId() == R.id.nav_settings){
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
