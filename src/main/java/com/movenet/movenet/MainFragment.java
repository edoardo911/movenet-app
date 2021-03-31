package com.movenet.movenet;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.movenet.movenet.model.User;

public class MainFragment extends Fragment
{
    private static boolean pwdOk = false;
    private static boolean numberOk = false;
    private static boolean changed = false;

    private void setErrorMessage(String message)
    {
        TextView error = getView().findViewById(R.id.errorMessage);
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

    private int getSecondaryColor()
    {
        TypedArray ta = getView().getContext().getTheme().obtainStyledAttributes(R.styleable.Theme);
        return ta.getColor(R.styleable.Theme_colorSecondary, 0);
    }

    private ColorStateList getEditTextColorStateList()
    {
        return new ColorStateList(new int[][]
                {
                        new int[] { android.R.attr.state_focused },
                        new int[] { android.R.attr.state_enabled }
                },
                new int[] { getSecondaryColor(), getResources().getColor(R.color.underline) });
    }

    public MainFragment()
    {
        super(R.layout.fragment_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        EditText username = view.findViewById(R.id.username);
        EditText email = view.findViewById(R.id.email);
        EditText number = view.findViewById(R.id.number);
        EditText pwd = view.findViewById(R.id.pwd);
        EditText confirm = view.findViewById(R.id.confirm);
        TextView login = view.findViewById(R.id.gotoLogin);
        Button register = view.findViewById(R.id.register);

        number.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() != 10)
                {
                    view.findViewById(R.id.numberError).setVisibility(View.VISIBLE);
                    number.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    numberOk = false;
                }
                else
                {
                    view.findViewById(R.id.numberError).setVisibility(View.GONE);
                    number.setBackgroundTintList(getEditTextColorStateList());
                    numberOk = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        pwd.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.length() < 8)
                {
                    view.findViewById(R.id.pwdError).setVisibility(View.VISIBLE);
                    pwd.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    pwdOk = false;
                }
                else
                {
                    view.findViewById(R.id.pwdError).setVisibility(View.GONE);
                    pwd.setBackgroundTintList(getEditTextColorStateList());
                    pwdOk = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        NavController nav = Navigation.findNavController(view);

        login.setOnClickListener(vw -> {
            nav.navigate(R.id.action_mainFragment_to_loginFragment);
        });

        register.setOnClickListener(vw -> {
            if(username.getText().toString().equals("") || email.getText().toString().equals("") || number.getText().toString().equals("") || pwd.getText().toString().equals("") || confirm.getText().equals(""))
            {
                setErrorMessage("Error: must compile form");
                return;
            }

            if(!pwd.getText().toString().equals(confirm.getText().toString()))
            {
                setErrorMessage("Error: passwords must match");
                return;
            }

            if(!email.getText().toString().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
            {
                setErrorMessage("Invalid email");
                return;
            }

            if(!pwdOk)
                pwd.requestFocus();
            if(!numberOk)
                number.requestFocus();
            if(pwdOk && numberOk)
            {
                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("users");
                DatabaseReference db = userDb.push();
                User usr = new User(db.getKey(), username.getText().toString(), email.getText().toString(), number.getText().toString(), "");

                userDb.orderByChild("username").addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        boolean theError = false;
                        for(DataSnapshot child:dataSnapshot.getChildren())
                        {
                            User user = child.getValue(User.class);
                            if(user.getEmail() != null && user.getEmail().equals(usr.getEmail()))
                            {
                                theError = true;
                                break;
                            }
                        }

                        if(!theError)
                        {
                            register.setOnClickListener(null);
                            db.child("username").setValue(usr.getUsername());
                            db.child("email").setValue(usr.getEmail());
                            db.child("number").setValue(usr.getNumber());
                            db.child("pwd").setValue(pwd.getText().toString());

                            db.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(!changed)
                                    {
                                        changed = true;
                                        nav.navigate(R.id.action_mainFragment_to_mapFragment);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        }
                        else
                            setErrorMessage("Email already taken");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }
        });
    }
}
