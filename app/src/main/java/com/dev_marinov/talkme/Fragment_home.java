package com.dev_marinov.talkme;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_home extends Fragment {
    View frag;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    AdapterViewPage2 adapterViewPage2;
    List<ObjectTab> list = new ArrayList<>();

    //ImageView img_exit_razlog;
    //GoogleSignInClient mGoogleSignInClient;
    //String setupapp;
    TextInputLayout text_input_layout;
    TextInputEditText input_edit_text_search;

    HashMap<Integer, ObjectMessage> list_last_message_user = new HashMap<>();
    AdapterMessage adapterMessage;

    int activetab = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frag = inflater.inflate(R.layout.fragment_home, container, false);
        adapterMessage = new AdapterMessage(getContext(), list_last_message_user);

        text_input_layout = frag.findViewById(R.id.text_input_layout);
        input_edit_text_search = frag.findViewById(R.id.input_edit_text_search);
        //img_exit_razlog = frag.findViewById(R.id.img_exit_razlog);

        // НАПИСАТЬ ДЛЯ МЕТОДА ЗАПОЛНЕНИЯ ЛИСТА ЮЗЕР В ЧАТЫ
        // команда от клиента серверу на сохранение google_id

            input_edit_text_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) { // закончил ввод последнего символа


                Gson json = new Gson();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cmd", "list_chat");
                hashMap.put("google_id", ((MainActivity)getActivity()).google_id);
                hashMap.put("q", input_edit_text_search.getText().toString()); // это поиск на верху который
                String json_string = json.toJson(hashMap);
                ((MainActivity)getActivity()).clwss.send(json_string);


            }
        });


            Gson json = new Gson();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cmd", "list_chat");
            hashMap.put("google_id", ((MainActivity)getActivity()).google_id);
            hashMap.put("q", input_edit_text_search.getText().toString()); // это поиск на верху который
            String json_string = json.toJson(hashMap);
            ((MainActivity)getActivity()).clwss.send(json_string);

//        //////////////
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso); // возможно requireContext()
//        ///////////////

        // виджет tablayout позволяет добавить табы в своем интерфейсе, чтобы переключаться между фрагментами
        tabLayout = frag.findViewById(R.id.tablayout);
        // виджет viewpage позволяет добавить листание в своем интерфейсе, чтобы переключаться между фрагментами
        viewPager2 = frag.findViewById(R.id.view_pager2);

        list.add(new ObjectTab("1","Чаты"));
        list.add(new ObjectTab("2","Контакты"));
        list.add(new ObjectTab("3","Настройки"));
        tabLayout.removeAllTabs();// удалить все закладки

// Таб ЧАТЫ
        TabLayout.Tab new_Tab_1 = tabLayout.newTab();
        new_Tab_1.setText("Чаты");
        new_Tab_1.setTag("0");
        tabLayout.addTab(new_Tab_1);
// Таб КОНТАКТЫ
        TabLayout.Tab new_Tab_2 = tabLayout.newTab();
        new_Tab_2.setText("Контакты");
        new_Tab_2.setTag("1");
        tabLayout.addTab(new_Tab_2);

        TabLayout.Tab new_Tab_3 = tabLayout.newTab();
        new_Tab_3.setText("Настройки");
        new_Tab_3.setTag("3");
        tabLayout.addTab(new_Tab_3);

        adapterViewPage2 = new AdapterViewPage2(getContext(), list); // помещаем в адаптер массив list
        viewPager2.setAdapter(adapterViewPage2); // устанавливаем в виджет viewpage адаптер

        set_adapter();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // если я кликаю на закладку я получаю из тега ноль или еденицу
                activetab = Integer.parseInt(tab.getTag().toString());
                viewPager2.setCurrentItem(Integer.parseInt(tab.getTag().toString()), true);
               new Timer().schedule(new TimerTask() {
                   @Override
                   public void run() {
                       getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               set_adapter();
                           }
                       });
                   }
               },100);

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                activetab = position;
                tabLayout.getTabAt(position).select(); //
                new Timer().schedule(new TimerTask() { // timer для того чтобы установить адаптер
                    // после того как закладк будет отбражена на экране
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                set_adapter();
                            }
                        });
                    }
                },100);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
             try {
                getActivity().runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           try {

                            if(activetab == 0)
                            {
                               // список последних сообщение
                                adapterViewPage2.update_list_last_message_user(((MainActivity)getActivity()).list_last_message_user);
                            }
                              if(activetab == 1)
                               {
                                   // список всех пользователей телефонной книги
                                   adapterViewPage2.update_list_contact(((MainActivity)getActivity()).list_contact_user); // добавил
                               }

                               if(activetab == 2)
                               {
                                   // список элементов НАСТРОЙКА
                                   adapterViewPage2.update_list_setting(((MainActivity)getActivity()).list_setting);
                               }

                           }
                           catch (Exception e)
                           {

                           }
                       }
                   });
               }
               catch (Exception e)
               {

               }
            }
        },1000,1000);

//        img_exit_razlog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        //     ((MainActivity) getActivity()).show_or_hide_fragment("auth",new String[]{});
//
//
//                        ((MainActivity) getActivity()).ll_frag_home.removeAllViews();
//
//                        FragmentManager fragmentManager = ((MainActivity) getActivity()).getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        Fragment_auth fragment_auth = new Fragment_auth();
//                        fragmentTransaction.replace(R.id.ll_frag_auth, fragment_auth);
//                        fragmentTransaction.commit();
//                    }
//                });
//            }
//        });


                return frag;
    }



    public void set_adapter()
    {
        if (activetab == 0) {
            adapterViewPage2.set_adapter_list_last_message_user(((MainActivity) getActivity()).list_last_message_user);
        }
        if (activetab == 1) {
            adapterViewPage2.set_adapter_list_contact(((MainActivity) getActivity()).list_contact_user);
        }
        if (activetab == 2) {
            adapterViewPage2.set_adapter_list_setting(((MainActivity) getActivity()).list_setting);
        }


    }





}

