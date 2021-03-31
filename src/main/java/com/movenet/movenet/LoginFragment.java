package com.movenet.movenet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movenet.movenet.model.User;

public class LoginFragment extends Fragment
{
    private static boolean changed = false;

    private void setErrorMessage(String message)
    {
        TextView error = getView().findViewById(R.id.errorMessageLogin);
        error.setVisibility(View.VISIBLE);
        error.setText(message);
        Animation fadeout = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
        fadeout.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) { error.setVisibility(View.GONE); }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        error.startAnimation(fadeout);
    }

    public LoginFragment() { super(R.layout.fragment_login); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        EditText email = view.findViewById(R.id.email_login);
        EditText pwd = view.findViewById(R.id.pwdLogin);
        TextView register = view.findViewById(R.id.gotoRegister);
        Button login = view.findViewById(R.id.login);

        NavController nav = Navigation.findNavController(view);

        register.setOnClickListener(v -> {
            nav.navigate(R.id.action_loginFragment_to_mainFragment);
        });

        login.setOnClickListener(v -> {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
            db.orderByChild("username").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    boolean flag = false;
                    for(DataSnapshot child:dataSnapshot.getChildren())
                    {
                        User user = child.getValue(User.class);
                        if(user.getEmail().equals(email.getText().toString()) && user.getPwd().equals(pwd.getText().toString()))
                        {
                            flag = true;
                            break;
                        }
                    }

                    if(flag)
                    {
                        if(!changed)
                        {
                            changed = true;
                            nav.navigate(R.id.action_loginFragment_to_mapFragment);
                        }
                    }
                    else
                        setErrorMessage("Error: email or password incorrect");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        });
    }
}