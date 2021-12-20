package com.dev_marinov.talkme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Fragment_Send extends Fragment {

    View frag;
    Button bt_send;
    RecyclerView chat_recycler; // прокручиваемый список
    HashMap<Integer, ObjectMessage> message = new HashMap<>();
    HashMap<Integer, ObjectMessage> list_last_message_user = new HashMap<>();
    Intent intent;
    String not_my_name_contact, not_my_google_id, my_name_contact, fr_send_status, fr_send_txt, fr_send_msg_status;
    AdapterMessage adapterMessage;
    TextView tv_title_select_user, tv_status_online, tv_msg_null;
    String msg_status;
    TextInputEditText input_edit_text_search_send_msg;
    String string_txt = "", string_create_time = "", id_row ="", string_google_id = "", flag_user = "0";

    public void setParam(String not_my_name_contact, String not_my_google_id,  String status_online, String txt, String msg_status) {
        this.not_my_name_contact = not_my_name_contact;
        this.not_my_google_id = not_my_google_id;
        //this.my_name_contact = my_name_contact;
        this.fr_send_status = status_online;
        this.fr_send_txt = txt;
        this.fr_send_msg_status = msg_status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        frag = inflater.inflate(R.layout.fragment_send, container, false);
        tv_title_select_user = frag.findViewById(R.id.tv_title_select_user);
        tv_status_online = frag.findViewById(R.id.tv_status_online);
        tv_msg_null = frag.findViewById(R.id.tv_msg_null);
        chat_recycler = frag.findViewById(R.id.chat_recycler);

        input_edit_text_search_send_msg = frag.findViewById(R.id.input_edit_text_search_send_msg);
        //send_text = frag.findViewById(R.id.send_text);
        bt_send = frag.findViewById(R.id.bt_send);

        // false - показать список с снизу
        LinearLayoutManager lm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        chat_recycler.setLayoutManager(lm);

//        adapterLastMessage = new AdapterLastMessage(getContext(),message);
//        chat_recycler.setAdapter(adapterLastMessage);

        //////////////////////
        adapterMessage = new AdapterMessage(getContext(), message);
        chat_recycler.setAdapter(adapterMessage);
        ///////////////////////


        // ЭТОТ КУСОК КОДА ПОЧЕМУ ТО НЕ ВЫВОДИЛ НА ЭКРАН ВСЕ СООБЩЕНИЯ ПРИ ЗАПУСКЕ ONCREATE
        // ОН РАБОТАЕТ ТОЛЬКО ЕСЛИ УКАЗАТЬ ЕСТЬ ЛИ СОЕДИЕНЕНИЕ С ИНТЕРНЕТОМ
        // от клиента на сервер, получить список сообщений
        if (((MainActivity)getActivity()).flag_connect) // Если есть связь с сервером или нет
        {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "list_message");
        hashMap.put("google_id", ((MainActivity) getActivity()).google_id);
        String json_string = json.toJson(hashMap);
        ((MainActivity) getActivity()).clwss.send(json_string);
        }
        else {

        }

// метод считывания из input_edit_text_search ввод текста в реальном времени
        // условие если кол-во вводимого текста >= 1 значения(исключаяя пробелы), то появляется или нет кнопка отправки сообщения
        input_edit_text_search_send_msg.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {   //Convert the Text to String
                String inputText = input_edit_text_search_send_msg.getText().toString();
                if (inputText.trim().length() >= 1)
                {
                    bt_send.setVisibility(View.VISIBLE); // показать кнопку
                    // ДЕЙСТВИЯ ВЫПОЛНЯЕМЫЕ ПРИ КЛИКЕ
                    bt_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((MainActivity) getActivity()).flag_connect == false) //Нет связи с сервером
                            {
                                ((MainActivity) getActivity()).id_row_messageayNoSendMessage.put(((MainActivity) getActivity()).id_row_messageayNoSendMessage.size(),
                                        new ObjectNoSendMessage(input_edit_text_search_send_msg.getText().toString(), ((MainActivity) getActivity()).google_id, not_my_google_id));

                                Log.e("FRAG_SEND", "-list_last_message_user.size()-" + list_last_message_user.size());
                                list_last_message_user.put(list_last_message_user.size(), new ObjectMessage("", ((MainActivity) getActivity()).google_name, ((MainActivity) getActivity()).google_id, "0", "1",
                                        input_edit_text_search_send_msg.getText().toString(), "12:00", "clock", ""));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (string_txt.equals("")) {
                                            tv_msg_null.setVisibility(View.VISIBLE); // открыт
                                        } else {
                                            tv_msg_null.setVisibility(View.GONE);
                                        } // закрыт

                                        ////////////////////////
                                        adapterMessage.notifyDataSetChanged();
                                        /////////////////////////

                                        //  adapterLastMessage.notifyDataSetChanged(); // обновление адаптра после доабвленния данных
                                        chat_recycler.smoothScrollToPosition(100000000);
                                    }
                                });
                            } else {
                                // ОТПРАВКА ДАННЫХ О СООБЩЕНИИ НА СЕРВЕР И БД
                                Gson json = new Gson();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("cmd", "send_msg_client");
                                hashMap.put("msg", input_edit_text_search_send_msg.getText().toString());
                                // тут передается google_id того, кто отпраляет сообщение
                                hashMap.put("google_id", ((MainActivity) getActivity()).google_id);


                                // тут передается not_my_user_google_id того, кто получает сообщение
                                hashMap.put("not_my_user_google_id", not_my_google_id);
                                hashMap.put("name", not_my_name_contact); //имя пользователя с кем идет диалог (имя из адресной книги)
                                // при нажатии кнопки отправить сообщение клиент шлет серверу "1" чтобы сообщение отправлено (одна серая галка)

                                String json_string = json.toJson(hashMap);
                                ((MainActivity) getActivity()).clwss.send(json_string);

                                Log.e("FRAG_SEND","-wfr_send_name-"+ not_my_name_contact);
                                Log.e("FRAG_SEND","-wmsg-"+ input_edit_text_search_send_msg.getText().toString());
                                Log.e("FRAG_SEND","-wgoogle_id-"+ ((MainActivity) getActivity()).google_id);
                                Log.e("FRAG_SEND","-wnot_my_google_id-"+ not_my_google_id);

                            }
                            input_edit_text_search_send_msg.setText("");
                        }
                    });

                    //////////////////////////////////////////////////////
                }
                else
                {
                    bt_send.setVisibility(View.GONE); // скрыть кнопку если нет текста и одни пробелы
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // не используемые методы
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // не используемые методы
            }
        });

        // name пришло из UserAdapter после нажатия на item. В UserAdapter я записал в метод setParam, который в fragment_send
        tv_title_select_user.setText(not_my_name_contact);

        intent = getActivity().getIntent(); // intent - намерение выполнить действие
        if (intent.getExtras() != null)
        {
            ObjectMessage objectMessage = (ObjectMessage) intent.getSerializableExtra("data");
            tv_title_select_user.setText(objectMessage.google_name); // запись в титул выбранного имени пользователя из списка
        }
        else
        {

        }

        setStatusOnline(); // чтобы при открытии фрагмента fragment_send и его oncreate запускался метод setStatusOnline();

        /////////////////////////////////////////
        ////////////////////////////////////
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                    return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int itemPosition = viewHolder.getAdapterPosition();
                new AlertDialog.Builder(getContext())
                        .setMessage("хочешь удалить " + adapterMessage.getItemId(itemPosition))
                        ///////////////
                        .setMessage("хочешь удалить : \"" + message.get(itemPosition).id_row + "\"?")
                        .setPositiveButton("УДАЛИТЬ",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapterMessage.notifyItemRemoved(itemPosition);
                                        remove_message(message.get(itemPosition).id_row);
                                        message.remove(itemPosition);


                                        Gson json = new Gson();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("cmd", "list_chat");
                                        hashMap.put("google_id", ((MainActivity)getContext()).google_id);
                                        hashMap.put("q", ""); // это поиск на верху который
                                        String json_string = json.toJson(hashMap);
                                        ((MainActivity)getContext()).clwss.send(json_string);



                                    }
                                })
                        //.setMessage("Do you want to delete: \"" + mRecyclerViewAdapter.getItemAtPosition(itemPosition).getName() + "\"?")
                       // .setPositiveButton("Delete", (dialog, which) -> mYourActivityViewModel.removeItem(itemPosition))
                        .setNegativeButton("отмена", (dialog, which) -> adapterMessage.notifyItemChanged(itemPosition))
                        .setOnCancelListener(dialogInterface -> adapterMessage.notifyItemChanged(itemPosition))
                        .create().show();
////////////////////////
                adapterMessage.notifyDataSetChanged();
            }
        }).attachToRecyclerView(chat_recycler);

        return frag;
    }


    public void remove_message(String id_row)
    {
        Gson json = new Gson();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cmd", "remove_message");

        hashMap.put("id_row", id_row);
        hashMap.put("google_id", ((MainActivity) getActivity()).google_id);
        String json_string = json.toJson(hashMap);
        ((MainActivity) getActivity()).clwss.send(json_string);
    }

    public void setStatusOnline() // !!!!!!!!!!!!!! ПОЧЕМУ ТО ЗАПУСТИВ В ГЛАВНОМ ПОТОКЕ
            // СТАТУС НЕ ОБНОВЛЯЕТСЯ САМ ПО СЕБЕ,А ТОЛЬКО ЕСЛИ ВЫХОДИТЬ ИЗ ОДНОГО ФРАГМЕНТА В ДРУГОЙ
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (fr_send_status.equals("0")) {
                    tv_status_online.setText("не в сети");
                } else {
                    tv_status_online.setText("в сети");
                }

//                if (fr_send_txt.equals("")) {
//                    tv_msg_null.setVisibility(View.VISIBLE);
//                } else {
//                    tv_msg_null.setVisibility(View.GONE);
//                }
            }
        });
    }

    // метод получения гугл айди отправителя и получаетля, текст сообщений, дату,
    // тут происходит заполнение экрана всеми сообщениями
    public void new_message(String jsontext) {

        Log.e("FRAG_SEND","666-jsontext-" + jsontext);

        message.clear(); // Эта строка именно не в try потому что если нет на входе данных после удаления все сообщений, то
        // try не выполниться
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsontext);


            int count = -1;
            Iterator<String> k = jsonObject.keys();
            while (k.hasNext())
            {
                count ++;
                String id_row_message = k.next();

                String id_row = jsonObject.getJSONObject(id_row_message).getString("id_row");
                String string_google_id = jsonObject.getJSONObject(id_row_message).getString("google_id");
                String string_txt = jsonObject.getJSONObject(id_row_message).getString("txt");
                String string_create_time = jsonObject.getJSONObject(id_row_message).getString("create_time");
                // flag_user - это исходящее или входящее
                String flag_user = jsonObject.getJSONObject(id_row_message).getString("flag_user");
                // msg_status это 0 не прочитано, 1 доставлено, 2 прочитано
                String msg_status = jsonObject.getJSONObject(id_row_message).getString("msg_status");

                Log.e("FRAG_SEND ","666-string_txt-" + string_txt);
    //            Log.e("FRAG_SEND ","-msg_status-" + msg_status);

//                status_send = jsonObject.getJSONObject(id_row_message).getString("status_send");

                message.put(count,new ObjectMessage(id_row,string_google_id, string_google_id,"0",flag_user,string_txt,
                        string_create_time, msg_status,""));

            }
            Log.e("FRAG_SEND","-message1-" + message.size());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapterMessage.update_list_message(message);
                            Log.e("FRAG_SEND","-message2-" + message.size());
                            //   adapterLastMessage.notifyDataSetChanged(); // обновление адаптра после доабвленния данных
                            chat_recycler.smoothScrollToPosition(100000000);
                        }
                    });


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("FRAG_SEND","-ex-" + e);
        }

    }

}
