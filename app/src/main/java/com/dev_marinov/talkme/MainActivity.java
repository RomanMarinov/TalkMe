package com.dev_marinov.talkme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String google_photo = "", google_id, google_name = "";
    String id_contacts = "", names_contacts = "", phone_contacts = "";
    LinearLayout ll_frag_auth, ll_frag_home, ll_frag_send, ll_frag_setting;
    clWSS clwss;
    boolean flag_connect = false;

    HashMap<Integer,ObjectMessage> list_last_message_user; // список пользователей для ЧАТЫ с последним сообщением
    HashMap<Integer,ObjectContact> list_contact_user; // список контактов
    HashMap<Integer,ObjectSetting> list_setting; // список настроек
    HashMap<Integer, ObjectNoSendMessage> id_row_messageayNoSendMessage = new HashMap<>();

    public boolean prava_contact() // метод подтверждения прав доступа к телефонной книге
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // часть кода для старых устройств
            if ( checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {   // В permission. есть доступ и к другим частям телефона (например камера)
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},400);
                Log.e("доступ","Запрос доступа");
                return false;
            }
            else
            {
                return true;
                // Log.e("доступ","Доступ дали к камере");      // camera_ok();
            }
        }
        else
        {
            if (ContextCompat.checkSelfPermission( // часть кода для новых устройств
                    MainActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_DENIED) {
                ActivityCompat
                        .requestPermissions(
                                MainActivity.this,
                                new String[] { Manifest.permission.READ_CONTACTS },
                                400);
                return false;
            }
            else {
                return true;
                //  start_app();
               // camera_ok();
            }
        }
    }



    public void ReadyContact() // метод получения id, name, phone контактов, которые храняться на телефонеи передача их на сервер
    // сработает тогода кога я дал или не дал прравада доступа
    {
        int num = -1;
        Log.e("MAIN_ACT", "Contact");
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
              num++;
                id_contacts = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                names_contacts = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
               // String my_contact = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.));
                //Log.e("MAIN_ACT", "-name_contact-" + name_contact);
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    // Query phone here. Covered next
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id_contacts,null, null);
                    while (phones.moveToNext()) {
                        phone_contacts = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                       // Log.e("MAIN_ACT","-phoneNumber_contact-" + phone_contact);
                        break;
                    }
                    phones.close();
                }
                Log.e("MAIN_ACT","-name_contact-" + names_contacts);
                // передача данных телефонной книги в hashmap list_contact_user
                list_contact_user.put(num, new ObjectContact(names_contacts, "", id_contacts,
                        phone_contacts, "", "", ""));
            }
        }
    }

    @Override // сработает тогода кога я дал или не дал прравада доступа
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 400) && (grantResults[0] == 0)) //&& - и (|| - или)
        {
            ReadyContact(); // метод получения данных телефонной книги устройства
        }
    }

    public void auth_users() // от клиента серверу передать google_name и google_id
    {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "auth_user");
        hashMap.put("google_name", google_name);
        hashMap.put("google_id", google_id);
        String json_string = json.toJson(hashMap);
        clwss.send(json_string);
    }

    // НАПИСАТЬ ДЛЯ МЕТОДА ЗАПОЛНЕНИЯ ЛИСТА ЮЗЕР В ЧАТЫ
    // команда от клиента серверу на сохранение google_id
    public void cmd_list_chat() // от клиента серверу передать google_name, google_id
    {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "list_chat");
        hashMap.put("google_id", google_id);
        hashMap.put("q", ""); // это поиск на верху который
        String json_string = json.toJson(hashMap);
        clwss.send(json_string);
    }

// КОМАНДА ДЛЯ ПОЛУЧЕНИЯ ОТ БД ИЗ ТАБЛИЦЫ CONTACT_PHONE_BOOK NAME, PHONE, SETUPAPP, GOOGLE_ID(НЕ ТЕКУЩИЙ)
    // метод срабатывает после нажатия кнопки авторизации аккаунта в приложении
    // и передает от клиента серверу google_id и list_contact_user в котором храниться  name_contact и phone_contact
    // обратно клиент от сервера получает name, phone, setupapp, google_id(не текущий, а других пользователей)
    public void cmd_send_google_id_contact()
    {
        if (google_id != null) {
            Log.e("send_cmd", "cmd_send_google_id_contact " + google_id);
            Gson json = new Gson();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cmd", "cmd_send_google_id_contact");
            hashMap.put("google_id", google_id);
            hashMap.put("list_contact", list_contact_user);
            hashMap.put("device", "" + Build.MODEL);
            Log.e("MAIN", "");
            String json_string = json.toJson(hashMap);
            clwss.send(json_string);
        }
    }

// КОМАНДА НА ПОЛУЧЕНИЕ ОТ БД ИЗ ТАБЛИЦЫ PROFILE СТАТУС СЕТИ ПОЛЬЗОВАТЕЛЕЙ
    // срабатывает в когда от сервера приходят команды "list_users" и "update_list_user"
    // так же срабатывает в методе method_list_setupapp(после авторизации)
    public void cmd_get_status_network(String google_id) // от клиента серверу передать google_id
    {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "get_status_network");
        hashMap.put("google_id", google_id);
        String json_string = json.toJson(hashMap);
        clwss.send(json_string);
    }

    // команда срабатывает в момент появления открытого соединения с сервером
    // от клиента на сервер передается google_id в функцю auth_user, которая сохраняет
    // в бд данные (google_id, status, дату)
    public void cmd_connect_user() // от клиента серверу передать google_id
    {
                Log.e("send_cmd","cmd_connect_user " + google_id);
        if (google_id != null) {
            Gson json = new Gson();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cmd", "connect_user");
            hashMap.put("google_id", google_id);
            hashMap.put("google_name", google_name);
            hashMap.put("list_contact", list_contact_user);
            Log.e("wefwef","-list_contact-" + list_contact_user.toString());
            hashMap.put("device", "" + Build.MODEL);
            String json_string = json.toJson(hashMap);
            clwss.send(json_string);
        }

    }
    // команда отправки от клиента на сервер пинг, чтобы получить от сервера обратно понг(это значит соединение есть)
    // иначе соедиения нет, если не обмена не будет
    public void cmd_ping(String text)
    {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "pong");
        hashMap.put("ping", "ping");
        String json_string = json.toJson(hashMap);
        clwss.send(json_string);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //   ГРАДИЕНТ СТРОКИ СОСТОЯНИЯ И ЧЕРНЫЙ БАР НАВИГАЦИИ

        // установка черного цвета текста строки состояния
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // установка фона экрана
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.color.white);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS Флаг, указывающий, что это Окно отвечает за отрисовку фона для системных полос.
        // Если установлено, системные панели отображаются с прозрачным фоном, а соответствующие области в этом окне заполняются цветами,
        // указанными в Window#getStatusBarColor()и Window#getNavigationBarColor().
        window.setStatusBarColor(getResources().getColor(android.R.color.white));
        window.setNavigationBarColor(getResources().getColor(android.R.color.black));
        window.setBackgroundDrawable(background);

        clwss = new clWSS();

        // НА ВХОД ДАННЫЕ ДЛЯ ЧАТЫ
        list_last_message_user = new HashMap<>();
//        list_last_message_user.put(0, new ObjectMessage("Roma", "",
//                "","","","", ""));
//        list_last_message_user.put(1, new ObjectMessage("Nikita", "",
//                "","","","", ""));

        // НА ВХОД ДАННЫЕ ДЛЯ КОНТАКТЫ
        list_contact_user = new HashMap<>();
        list_setting = new HashMap<>();

        String[] array = new String[5];
        array[0] = "ИМЯ";
        array[1] = "ФОТО";
        array[2] = "СТАТУС";
        array[3] = "ВЫЙТИ";

        for(int i = 0; i < 4; i ++)
        {
        list_setting.put(i,new ObjectSetting(array[i],"234","423"));
        }

        ll_frag_auth = findViewById(R.id.ll_frag_auth);
        ll_frag_home = findViewById(R.id.ll_frag_home);
        ll_frag_send = findViewById(R.id.ll_frag_send);
        ll_frag_setting = findViewById(R.id.ll_frag_setting);
// после запуска mainactivity открываем фрагмент авторизации
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment_auth fragment_auth = new Fragment_auth();
        fragmentTransaction.replace(R.id.ll_frag_auth, fragment_auth);
        fragmentTransaction.commit();

        if (prava_contact()) // если права доступа подтвердил пользователь
        {
            ReadyContact(); // метод получения данных телефонной книги устройства
        }
        clwss.setonGetMessage(new clWSS.onGetMessage() {  // ансисинхронная функция, она работает НЕ В ГЛАВНОМ ПОТОКЕ
            // все что в блоке try синхронно и в главном потоке!!!!!!!!!!!!!!!!!!!
            @Override
            public void onGetMessage(String text) {
                Log.e("MAIN_ACT ", "text=" + text);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String cmd = jsonObject.getString("cmd");

                    if (cmd.equals("ping")) {
                        cmd_ping(text);
                    }
                    // получаем от сервера name, phone, setupapp, google_id(не текущий, а других пользователей)
                    if (cmd.equals("list_setupapp")) {
                        Log.e("MAIN_ACT","-list_setupapp-"+text);
                        method_list_setupapp(text);
                    }

                    if (cmd.equals("status_online")) {
                        Log.e("status_online","-status_online-"+text);
                        method_status_network_not_my_google_id(text);
                    }

                    if(cmd.equals("show_list_message"))
                    {
                        method_show_list_message(text);
                    }
                    if(cmd.equals("get_my_name_contact"))
                    {
                        Log.e("MAIN_ACT","--text--" + text);
                        method_get_my_name_contact(text);
                    }
                    if(cmd.equals("update_contact"))
                    {
                        Log.e("main_act","-update_contact-");
//cmd_send_google_id_contact();
                    }

                    if(cmd.equals("list_chat")) // команда от сервера клиенту с данными для ЧАТА
                    {
                        Log.e("MAIN_ACT","-list_chat-" + text);
                        method_list_chat(text);

//                        ///////////////............................
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                new Timer().schedule(new TimerTask() {  // задержка выполнения кода, т.к. на сервер
//                                    @Override
//                                    public void run() {
//                                        Log.e("start","start");
//                                        for (int z=0; z<list_last_message_user.size();z++)
//                                        {
//                                            if (!list_last_message_user.get(z).not_my_google_id.equals(""))
//                                            {
//                                                cmd_get_status_network(list_last_message_user.get(z).not_my_google_id);
//                                            }
//                                        }
//                                    }
//                                },5000);
//                            }
//                        });
//
//                        //////////////////...........................
                    }

// КОМАНДА update_list_user ОТ СЕРВЕРА ЧТОБЫ КЛИЕНТ ПЕРЕБРАЛ МАССИВ list_contact_user И ЕСЛИ В НЕМ НЕТ КАКОГО ЛИБО
                    // ГУГЛ АЙДИ ДРУГОГО ПОЛЬЗОВАТЕЛЯ (not_my_google_id), ТО ОН ЕГО ДОБАВИЛ
                    if (cmd.equals("update_list_user")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Timer().schedule(new TimerTask() {  // задержка выполнения кода, т.к. на сервер
                                   @Override
                                   public void run() {
                                       Log.e("start","---list_contact_user.size()-" + list_contact_user.size());

                                       for (int z=0; z<list_contact_user.size();z++)
                                       {
                                         //  Log.e("start","сколько раз выполниться цикл " + z);
                                           if (!list_contact_user.get(z).not_my_google_id.equals(""))
                                           {
                                               cmd_get_status_network(list_contact_user.get(z).not_my_google_id);
                                               Log.e("MAIN_ACT","---list_contact_user.get(z).not_my_google_id" + list_contact_user.get(z).not_my_google_id);
                                           }
                                           else {
                                               Log.e("MAIN_ACT","---list_contact_user.get(z).not_my_google_id ПУСТОЙ");
                                           }
                                       }
                                   }
                               },5000);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("error parse","txt error: "+e);
                }
            }
        }); // последняя скобка setOngetMessage


        clwss.setonGetConnect(new clWSS.onGetConnect() { // открытое соединение с сервером
            @Override
            public void onGetConnect(boolean isconnect) {
                Log.e("con","onGetConnect "+isconnect);
                flag_connect = isconnect;
                if (flag_connect) // если true
                {
                    cmd_connect_user();
                    cmd_list_chat(); // метод получения списка пользователей с теми с кем была переписка

                    if (!google_id.equals("")) //Пользователь авторизован в приложении, и есть повторное соединение, то отправляем на сервер инфу о себе (google_id)
                    {
                       // метод посылает от клиента на сервер
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (id_row_messageayNoSendMessage.size() != 0) //есть сообщения которые еще не отправлены
                                {
                                    for (int i=0;i<=id_row_messageayNoSendMessage.size();i++)
                                    {
//                                        Gson json = new Gson();
//                                        HashMap<String, Object> hashMap = new HashMap<>();
//                                        hashMap.put("cmd", "send_msg_client");
//                                        hashMap.put("msg", id_row_messageayNoSendMessage.get(i).msg);
//                                        // тут передается google_id того, кто отпраляет сообщение
//                                        hashMap.put("google_id", google_id);
//                                        // тут передается get_user_google_id того, кто получает сообщение
//                                        hashMap.put("get_user_google_id", id_row_messageayNoSendMessage.get(i).get_user_google_id);
//                                        // при нажатии кнопки отправить сообщение клиент шлет серверу "1" чтобы сообщение отправлено (одна серая галка)
//                                        String json_string = json.toJson(hashMap);
//                                        clwss.send(json_string);
                                    }
                                    id_row_messageayNoSendMessage.clear();

                                    // от клиента на сервер, получить список сообщений
                                    Gson json = new Gson();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("cmd", "list_message");
                                    // тут передается google_id того, кто отпраляет сообщение
                                    hashMap.put("google_id",google_id);
                                    // тут передается get_user_google_id того, кто получает сообщение
                                    String json_string = json.toJson(hashMap);
                                    clwss.send(json_string);
                                }
                            }
                        });
                    }
                }
            }
        });
        // для переподлючения
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("con","flag_connect "+flag_connect);
                if (flag_connect == false)
                {
                    Log.e("con","connet");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clwss.ConnectSocket(getApplicationContext());
                        }
                    });
                }
            }
        },3000,3000);

        Log.e("con","connet");
        clwss.ConnectSocket(getApplicationContext());
    }


    // метод заполнения данными list_last_message_user
    public void method_list_chat(String text)
    {
        Log.e("MAIN_ACT","-method_list_chat-" + text);
        String status = "";

        // JSONObject и блок try для получения от сервера списка всех пользоваетелей
        // парсинг данных и передачу во Fragment_main
        JSONObject jsonObject_1 = null;
        try {
            jsonObject_1 = new JSONObject(text);
            String cmd = jsonObject_1.getString("cmd");
            String list_users = jsonObject_1.getString("list_chat");

            list_last_message_user.clear();
            if (cmd.equals("list_chat")) {
                JSONObject jsonObject = new JSONObject(list_users);
                int count = -1;
                Iterator<String> k = jsonObject.keys();
                while (k.hasNext()) {
                    count++;
                    String id_row_profile = k.next();
                    String google_name = jsonObject.getJSONObject(id_row_profile).getString("google_name");
                    String google_id = jsonObject.getJSONObject(id_row_profile).getString("google_id");
                    status = jsonObject.getJSONObject(id_row_profile).getString("status");
                    String polsedie_msg = jsonObject.getJSONObject(id_row_profile).getString("polsedie_msg");


                    Log.e("FRAG_ACT", "-rrgoogle_id!- " + google_id);
                    Log.e("FRAG_ACT", "-rrstatus!- " + status);
                    Log.e("FRAG_ACT", "-rrpolsedie_msg!- " + polsedie_msg);


                    list_last_message_user.put(count, new ObjectMessage("", google_name, "", status,
                            "", polsedie_msg, "", "", google_id));

//                    // отправляем запрос на серсер чтобы узнать гугл айди в сети или нет
//                    if (!not_my_google_id.equals("")) {
//                        cmd_get_status_network(not_my_google_id);
                }
            }
        }
            catch (Exception e) {
                  Log.e("MAIN_ACT", "-исключение 1-" + e);
                        }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Fragment_home fragment_home = (Fragment_home) getSupportFragmentManager().findFragmentById(R.id.ll_frag_home);
                                    if (fragment_home != null) {
                                        Log.e("статус online1", "update adapter");
                                        fragment_home.adapterViewPage2.update_list_last_message_user(list_last_message_user);
                                    }
                                }
                            });

    }




    // метод срабатывает после нажатия кнопки авторизации аккаунта
    // получаем от сервера name, phone, setupapp, google_id(не текущий, а других пользователей)
    public void method_status_network_not_my_google_id(String text) {
        // JSONObject и блок try для получения от сервера списка всех пользоваетелей
        // парсинг данных и передачу во Fragment_main
        Log.e("MAIN_ACT", "--method_status_network_not_my_google_id" + text);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(text);
            // получаем из массива jsonObject по ключу not_my_google_id_from_server два значения по двум дургим ключам
            String google_id = jsonObject.getJSONObject("not_my_google_id_from_server").getString("google_id");
            String status = jsonObject.getJSONObject("not_my_google_id_from_server").getString("status");
            // создаем пустой массив, по типу аналогичный list_contact_user
            HashMap<Integer, ObjectContact> list_contact_user2 = new HashMap<>();

            // НЕ ПОНЯТНО КАК РАБОТАЕТ
            for (int u = 0; u < list_contact_user.size(); u++) {
                // передаем в obContact все значения list_contact_user
                ObjectContact obContact = list_contact_user.get(u);
                // сравниваем. Если не текущий not_my_google_id равен текущему google_id
                if (obContact.not_my_google_id.equals(google_id)) {
                    obContact.status_online = status; // то записываем в obContact.status_online статус сети
// и передаем в новый hashmap obContact. В этом случае на экране будут отображаться пользователи со статусом в сети
                    list_contact_user2.put(u, obContact);
                } else {
// и передаем в новый hashmap obContact. В этом случае на экране будут отображаться пользователи со статусом не сети
                    list_contact_user2.put(u, obContact);

                }
             }

                list_contact_user = list_contact_user2;
                Log.e("online1", "generator new id_row_messageay" + list_contact_user2.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment_home fragment_home = (Fragment_home) getSupportFragmentManager().findFragmentById(R.id.ll_frag_home);
                        if (fragment_home != null) {
                            Log.e("статус online1", "update adapter");
                            fragment_home.adapterViewPage2.update_list_contact(list_contact_user);

                          ///////////////////////////...........................

                       //     fragment_home.adapterViewPage2.update_list_last_message_user(list_last_message_user);
                        }
                    }
                });
          }
        catch (Exception e) {
            e.printStackTrace();
            Log.e("MAIN_ACT","-ошибка-" + e);
        }
    }






    public void method_get_my_name_contact(String text)
    {
        Log.e("MAIN_ACT", "method_get_my_name_contact" + text);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(text);
            String cmd = jsonObject.getString("cmd");
            String my_name_contact = jsonObject.getString("get_my_name_contact");

            if (cmd.equals("get_my_name_contact"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment_Setting fragment_setting = (Fragment_Setting) getSupportFragmentManager().findFragmentById(R.id.ll_frag_setting);
                        if (fragment_setting != null) {
                            Log.e("MAIN_ACT", "get_my_name_contact" + my_name_contact);
                            fragment_setting.method_my_name(my_name_contact);
                        }
                        else {
                            Log.e("MAIN_ACT", "list_message ФРАГМЕНТ НУЛЕВОЙ" );
                        }
                    }
                });

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("MAIN_ACT", "list_message ошибка" + e);

        }
    }



    public void method_show_list_message(String text) {

        Log.e("MAIN_ACT", "method_text" + text);
        JSONObject jsonObject = null;
        try {
        jsonObject = new JSONObject(text);
        String cmd = jsonObject.getString("cmd");
        String list_message = jsonObject.getString("list_message");

            if (cmd.equals("show_list_message"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment_Send fragment_send = (Fragment_Send) getSupportFragmentManager().findFragmentById(R.id.ll_frag_send);
                        if (fragment_send != null) {
                            Log.e("MAIN_ACT", "list_message" + list_message);
                            fragment_send.new_message(list_message);
                        }
                        else {
                            Log.e("MAIN_ACT", "list_message ФРАГМЕНТ НУЛЕВОЙ" );
                        }
                    }
                });

                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("MAIN_ACT", "list_message ошибка" + e);

        }
    }





    // метод срабатывает после нажатия кнопки авторизации аккаунта
    // получаем от сервера name, phone, setupapp,not_my_google_id
    public void method_list_setupapp(String text) {
        // JSONObject и блок try для получения от сервера списка всех пользоваетелей
        // парсинг данных и передачу во Fragment_main
        JSONObject jsonObject_test = null;
        try {
            jsonObject_test = new JSONObject(text);
            String cmd = jsonObject_test.getString("cmd");
            String list_users = jsonObject_test.getString("list_setupapp");
            if (cmd.equals("list_setupapp")) {
                Fragment_home fragment_home = (Fragment_home) getSupportFragmentManager().findFragmentById(R.id.ll_frag_home);
                if (fragment_home != null) {
                    // ПОКА МЕТОД list_setupapp НЕ ИСПОЛЬЗУЕТСЯ
                    // fragment_home.list_setupapp(list_users); // передача списка всех пользователей
                }

                Log.e("FRAG_MAIN","проверка-jsontext-" + list_users);
                JSONObject jsonObject = null;
                    jsonObject = new JSONObject(list_users);
                     list_contact_user.clear();
                    int count = -1;
                    Iterator<String> k = jsonObject.keys();
                    while (k.hasNext())
                    {
                        count ++;
                        String id_row_profile = k.next();
                        String name = jsonObject.getJSONObject(id_row_profile).getString("name");
                        String phone = jsonObject.getJSONObject(id_row_profile).getString("phone");
                        String setupapp = jsonObject.getJSONObject(id_row_profile).getString("setupapp");
                        String not_my_google_id = jsonObject.getJSONObject(id_row_profile).getString("google_id");

                        Log.e("пров","-name-" + name);
                        Log.e("пров","-phone-" + phone);
                        Log.e("пров","-setupapp-" + setupapp);
                        Log.e("пров","-not_my_google_id-" + not_my_google_id);

       // после того как список тел книги вывелся на экран методом ReadyContact(); где данные передались в list_contact_user,
                        // то в методе cmd_list_setupapp данные поступившие от сервера, которые ранее были записаны в БД
                        // снова передались в list_contact_user
                        list_contact_user.put(count, new ObjectContact(name,"","",phone,setupapp, "", not_my_google_id));

                        // отправляем запрос на серсер чтобы узнать гугл айди в сети или нет
                        if (!not_my_google_id.equals("")) {
                            cmd_get_status_network(not_my_google_id);
                        }
                    }
            }
        } catch (Exception e) {
            Log.e("MAIN_ACT", "error list_users - " + e);
        }

    }


    @Override
    // вызовется тогда, когда активити закроется (закрытие это тогда, когда юзер зайдет по учетке или передумает)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode - код выбора, data - если не null, значит есть данные, сработало
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                run_app(account);

            } catch (ApiException e) {
                Log.e("err", "err=" + e);
                Log.e("account ", "null");
                Toast.makeText(getApplicationContext(), "На устройстве нет приложения Play Market = " + e, Toast.LENGTH_LONG).show();
                run_app(null);
            }
        }
    }
// метод срабатывает после нажатия кнопки авторизации аккаунта в приложении
    public void run_app(GoogleSignInAccount account) {
        if (account.equals(null)) {

        } else {
            if (account.getPhotoUrl() != null) {
                google_photo = account.getPhotoUrl().toString();
            }
            google_id = account.getId();
            google_name = account.getDisplayName();
            //String google_email = account.getEmail();
            auth_users(); //от клиента серверу передать google_name и google_id
//            Log.e("MAIN_ACT","-auth_usersывп-");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ll_frag_auth.removeAllViews();
                    //
              //      auth_users(); //от клиента серверу передать google_name и google_id
                Log.e("MAIN_ACT","-auth_usersывп-");
                    cmd_send_google_id_contact(); // метод отправки на сервер данных из телефонной книги
//                    cmd_list_users(); // метод получения списка пользователей с теми с кем была переписка

                    // открытие фрагмента после авторизации
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment_home fragment_home = new Fragment_home();
                    fragmentTransaction.replace(R.id.ll_frag_home, fragment_home);
                    fragmentTransaction.commit();
                }
            });
        }
    }
}