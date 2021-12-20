package com.dev_marinov.talkme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class AdapterViewPage2 extends RecyclerView.Adapter<AdapterViewPage2.Holder> {
    Context context;
        //AdapterUser adapterUser; // адаптер - связывание данных с элементами управлениями
    List<Holder> list_holder = new ArrayList<>();
    List<ObjectTab> list = new ArrayList<>();

   // HashMap<Integer,ObjectMessage> message = new HashMap<>();
    public AdapterViewPage2(Context context, List<ObjectTab> list) {
        this.context = context;
        this.list = list;
        Log.e("adapter","construktor");
    }

    @NonNull
    @NotNull
    @Override
    // с помощью объекта LayoutInflater создаем объект View для каждого отдельного элемента в списке:
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
    Log.e("adapter","onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.rv_view_rage_2, parent, false);
        Holder holder = new Holder(view);
        list_holder.add(holder);

    return holder;
    }

    @Override
    // onBindViewHolder() - получает весь список.
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        Log.e("adapter","onBindViewHolder "+position);
        if (list.get(position).name.equals("Чаты")) {
            {

            }
        }
        // ТУТ ДЛЯ ТАБА ЧАТЫ
        if (position == 0)
        {
            // передает массив в адаптер view_page_2
            list_holder.get(0).set_adapter_list_last_message_user(holder.list_last_message_user);
        }

        // ТУТ ДЛЯ ТАБА КОНТАКТЫ
        if (position == 1)
        {
            list_holder.get(1).set_adapter_list_contact(holder.list_contact_user);
        }

        // ТУТ ДЛЯ ТАБА НАСТРОЙКА
        if (position == 2)
        {
            list_holder.get(2).set_adapter_list_setting(holder.list_setting);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        RecyclerView rv_items;
        AdapterSetting adapterSetting;
        AdapterUser adapterUser;
        AdapterLastMessage adapterLastMessage;
        HashMap<Integer,ObjectMessage> list_last_message_user = new HashMap<>(); // расширяемый последних сообщений
        HashMap<Integer,ObjectContact> list_contact_user = new HashMap<>(); // расширяемый список пользователей
        HashMap<Integer,ObjectSetting> list_setting = new HashMap<>(); // расширяемый список настроек

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            Log.e("adapter","holder");

            rv_items = itemView.findViewById(R.id.rv_items);
            rv_items.setLayoutManager(new LinearLayoutManager(context)); // установка макета recyclerView
        }

        public void update_list_last_message_user(HashMap<Integer,ObjectMessage> list_last_message)
        {   Log.e("set","update_list_last_message_user");
            this.list_last_message_user = list_last_message;
          //notifyDataSetChanged();//  this.rv_items

//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user); // было
            //   ((MainActivity)context).list_users();
            adapterLastMessage.notifyDataSetChanged();
        }
        public void set_adapter_list_last_message_user(HashMap<Integer,ObjectMessage> list_last_message)
        {
            Log.e("set","set_adapter");
            this.list_last_message_user = list_last_message;
            //notifyDataSetChanged();//  this.rv_items
              adapterLastMessage = new AdapterLastMessage(context, list_last_message_user);
//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user); // было
            //   ((MainActivity)context).list_users();
            rv_items.setAdapter(adapterLastMessage);
        }
        public void update_list_contact(HashMap<Integer,ObjectContact> list_contact)
        {
            this.list_contact_user = list_contact;
            //notifyDataSetChanged();//  this.rv_items

//            AdapterLastMessage adapterLastMessage = new AdapterLastMessage(context, list_contact); // было
//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user);
            //   ((MainActivity)context).list_users();
            adapterUser.notifyDataSetChanged();
        }

        public void set_adapter_list_contact(HashMap<Integer,ObjectContact> list_contact)
        {
            this.list_contact_user = list_contact;
            //notifyDataSetChanged();//  this.rv_items
              adapterUser = new AdapterUser(context, list_contact_user); //
//            AdapterLastMessage adapterLastMessage = new AdapterLastMessage(context, list_contact); // было
//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user);
            //   ((MainActivity)context).list_users();
            rv_items.setAdapter(adapterUser);
        }
        public void update_list_setting(HashMap<Integer,ObjectSetting> list_setting)
        {
            this.list_setting = list_setting;
            //notifyDataSetChanged();//  this.rv_items
            Log.e("adapview","list_setting.size---" + list_setting.size());
//            AdapterLastMessage adapterLastMessage = new AdapterLastMessage(context, list_contact); // было
//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user);
            //   ((MainActivity)context).list_users();
            adapterSetting.notifyDataSetChanged();
         //   rv_items.setAdapter(adapterSetting);
        }
        public void set_adapter_list_setting(HashMap<Integer,ObjectSetting> list_setting)
        {
            this.list_setting = list_setting;
            //notifyDataSetChanged();//  this.rv_items
            Log.e("adapview","list_setting.size---" + list_setting.size());
              adapterSetting = new AdapterSetting(context, list_setting);
//            AdapterLastMessage adapterLastMessage = new AdapterLastMessage(context, list_contact); // было
//            AdapterUser adapterUser = new AdapterUser(context, list_last_message_user);
            //   ((MainActivity)context).list_users();
            rv_items.setAdapter(adapterSetting);
            }
        }

        public void update_list_last_message_user(HashMap<Integer,ObjectMessage> list_last_message)
        {
            try {
                list_holder.get(0).update_list_last_message_user(list_last_message);
                //Log.e("test", "update_list_last_message_user  1");
            } catch (Exception e) {
            }
        }
    public void set_adapter_list_last_message_user(HashMap<Integer,ObjectMessage> list_last_message)
    {
        try {
            list_holder.get(0).set_adapter_list_last_message_user(list_last_message);
            //Log.e("test", "update_list_last_message_user  1");
        } catch (Exception e) {
        }
    }
        public void update_list_contact(HashMap<Integer, ObjectContact> list_contact)
        {
            try
            {
                list_holder.get(1).update_list_contact(list_contact);
               // Log.e("test","update_list_last_message_user  2");
            }
            catch (Exception e) {
            }
        }
    public void set_adapter_list_contact(HashMap<Integer, ObjectContact> list_contact)
    {
        try
        {
            list_holder.get(1).set_adapter_list_contact(list_contact);
            // Log.e("test","update_list_last_message_user  2");
        }
        catch (Exception e) {
        }
    }
        public void update_list_setting(HashMap<Integer, ObjectSetting> list_setting)
        {
            try
            {
                list_holder.get(2).update_list_setting(list_setting);
                // Log.e("test","update_list_last_message_user  2");
            }
            catch (Exception e) {
            }
        }
    public void set_adapter_list_setting(HashMap<Integer, ObjectSetting> list_setting)
    {
        try
        {
            list_holder.get(2).set_adapter_list_setting(list_setting);
            // Log.e("test","update_list_last_message_user  2");
        }
        catch (Exception e) {
        }
    }
}
