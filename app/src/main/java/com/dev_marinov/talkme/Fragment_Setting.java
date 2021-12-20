package com.dev_marinov.talkme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_Setting extends Fragment{

    Context context;
    View frag;

    public String my_name_setting;
    public String my_photo_setting;
    public String my_status_sleep;

    TextView tv_setting;
    EditText edt_setting;

    TextView tv_dostupen, tv_nebespokoit;
    ImageView img_tv_dostupen, img_tv_nebespokoit, img_send_name;
    
    SharedPreferences sharedPreferences;
    String my_status_device;

    public void setParam(String my_name_setting, String my_photo_setting, String my_status_sleep) {
        this.my_name_setting = my_name_setting;
        this.my_photo_setting = my_photo_setting;
        this.my_status_sleep = my_status_sleep;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         frag = inflater.inflate(R.layout.fragment_setting, container, false);

         tv_setting = frag.findViewById(R.id.tv_setting);
         edt_setting = frag.findViewById(R.id.edt_setting);

        tv_dostupen = frag.findViewById(R.id.tv_dostupen);
        tv_nebespokoit = frag.findViewById(R.id.tv_nebespokoit);
        img_tv_dostupen = frag.findViewById(R.id.img_tv_dostupen);
        img_tv_nebespokoit = frag.findViewById(R.id.img_tv_nebespokoit);
        img_send_name = frag.findViewById(R.id.img_send_name);

        tv_setting.setText("изменить " + my_name_setting.toLowerCase());



        // реализовать клик на текствью
        if(my_name_setting.equals("ИМЯ"))
        {   edt_setting.setCursorVisible(true);
            edt_setting.setSelection(0);
           // edt_setting.setText();
            tv_dostupen.setVisibility(View.GONE);
            tv_nebespokoit.setVisibility(View.GONE);
            img_tv_dostupen.setVisibility(View.GONE);
            img_tv_nebespokoit.setVisibility(View.GONE);
            img_send_name.setVisibility(View.GONE);

            if(!edt_setting.equals(""))
            {
                img_send_name.setVisibility(View.VISIBLE);
                img_send_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        // запрос на сервер получить свое имя
//                        Gson json = new Gson();
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("cmd", "send_new_google_name");
//                        hashMap.put("google_id", ((MainActivity)getActivity()).google_id);
//                        hashMap.put("google_name", edt_setting.getText().toString());
//                        String json_string = json.toJson(hashMap);
//                        ((MainActivity)getActivity()).clwss.send(json_string);
                    }
                });

            }




            // запрос на сервер получить свое имя
            Gson json = new Gson();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cmd", "get_my_name_contact");
            hashMap.put("google_id", ((MainActivity)getActivity()).google_id);
            String json_string = json.toJson(hashMap);
            ((MainActivity)getActivity()).clwss.send(json_string);




        }
        
        

        // реализовать клик на текствью
        if(my_name_setting.equals("СТАТУС"))
        {
            method();
            img_send_name.setVisibility(View.GONE);
            edt_setting.setFocusable(false);
            tv_dostupen.setVisibility(View.VISIBLE);
            tv_nebespokoit.setVisibility(View.VISIBLE);
//            tv_dostupen.setOnClickListener((View.OnClickListener) this);
//            tv_nebespokoit.setOnClickListener((View.OnClickListener) this);

//            img_tv_dostupen.setVisibility(View.GONE);
//            img_tv_nebespokoit.setVisibility(View.GONE);

            tv_dostupen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edt_setting.setHint(tv_dostupen.getText().toString());
                    img_tv_dostupen.setVisibility(View.VISIBLE);
                    img_tv_nebespokoit.setVisibility(View.GONE);
                    saveSettingString("my_status_device", tv_dostupen.getText().toString());
                }
            });

            tv_nebespokoit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edt_setting.setHint(tv_nebespokoit.getText().toString());
                    img_tv_dostupen.setVisibility(View.GONE);
                    img_tv_nebespokoit.setVisibility(View.VISIBLE);
                    saveSettingString("my_status_device", tv_nebespokoit.getText().toString());
                }
            });

        }


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        edt_setting.setFocusable(true);
                        edt_setting.requestFocus();
                    }
                });


            }
        },10);









         return frag;
    }











    // считывает файл
    public String loadSettingString(String key, String default_value) {
        sharedPreferences = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, default_value);
    }

    // сохраняет в файл
    public void saveSettingString(String key, String value) {
        sharedPreferences = getActivity().getSharedPreferences("myPref", context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit(); // edit() - редактирование файлов
        ed.putString(key, value); // добавляем ключ и его значение
        if (ed.commit()) // сохранить файл
        {
            //успешно записано данные в файл
        }
        else
        {
            //ошибка при записи
            Toast.makeText(context, "Write error", Toast.LENGTH_SHORT).show();
        }
    }



    public void method()
    {
        my_status_device = loadSettingString("my_status_device", "");
        edt_setting.setHint(my_status_device);
        if(my_status_device.equals("доступен"))
        {
            img_tv_dostupen.setVisibility(View.VISIBLE);
            img_tv_nebespokoit.setVisibility(View.GONE);
        }
        if(my_status_device.equals("не беспокоить"))
        {
            img_tv_dostupen.setVisibility(View.GONE);
            img_tv_nebespokoit.setVisibility(View.VISIBLE);
        }

    }



    public void method_my_name(String my_name_contact) {


        try {
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(my_name_contact);

                int count = -1;
                Iterator<String> k = jsonObject.keys();
                while (k.hasNext())
                {
                    count ++;
                    String id_row_profile = k.next();
                    String name = jsonObject.getJSONObject(id_row_profile).getString("google_name");
                    Log.e("пров","-name-" + name);

                    edt_setting.setHint(name);
                }

        } catch (Exception e) {
            Log.e("MAIN_ACT", "error list_users - " + e);
        }



    }
}
