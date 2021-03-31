package com.movenet.movenet;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MapFragment extends Fragment
{
    public MapFragment() { super(R.layout.fragment_map); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed() { requireActivity().finish(); }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}