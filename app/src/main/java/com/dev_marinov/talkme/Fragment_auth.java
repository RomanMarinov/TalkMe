package com.dev_marinov.talkme;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

public class Fragment_auth extends Fragment {

    View frag;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton bSingInGoogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_auth, container, false);
        bSingInGoogle = frag.findViewById(R.id.google_singInButton);

        // Настройте вход, чтобы запросить идентификатор пользователя, адрес электронной почты,
        // а также идентификатор основного профиля, а основной профиль включен в DEFAULT_SIGN_IN.
        // инициализация компоненнта авторизации в гугл
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // https://developers.google.com/identity/sign-in/android/start
        // Создайте GoogleSignInClient с параметрами, указанными в gso.
        // НАПОМИНАНИЕ: если нет файл бат, то нужно его создать пройдя по ссылке выше, вставить 4 параметра
        // в файл бат, создав его в любом месте
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso); // возможно requireContext()
        bSingInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });
// не будет диалога авторизации
// проверяет авторизован я на телефоне или нет, Если да, то в account передается не null
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        Log.e("FRAG_AUTH", "-account-" + account);
        if (account != null)
        {

                ((MainActivity)getActivity()).run_app(account);

        }
        return frag;
    }




}