package com.dev_marinov.talkme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

// Адаптер, который используется в RecyclerView, должен наследоваться от абстрактного класса RecyclerView.Adapter.
// Этот класс определяет три метода:
// onCreateViewHolder: возвращает объект ViewHolder, который будет хранить данные по одному объекту Phone.
// onBindViewHolder: выполняет привязку объекта ViewHolder к объекту Phone по определенной позиции.
// getItemCount: возвращает количество объектов в списке
class AdapterUser extends RecyclerView.Adapter<AdapterUser.Holder>{
    private HashMap<Integer,ObjectContact> userList = new HashMap<>(); // расширяемый список пользователей
    private Context context;

    // Все взаимодействие со списком здесь будет идти через класс AdapterUser.
    // В конструкторе AdapterUser нам надо передать в конструктор базового класса два параметра:
    // контекст, в котором используется класс. В его роли кк правило выступает класс Activity
    // и набор объектов, которые будут выводиться в ListView
    // В конструкторе AdapterUser мы получаем набор объектов и сохраняем их в отдельные переменные.
    public AdapterUser(Context context, HashMap<Integer,ObjectContact> userList) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    // В методе onCreateViewHolder() устанавливается отображение элемента списка.
    // В данном случае с помощью объекта LayoutInflater создаем объект View для каждого отдельного элемента в списке:
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);
        Holder holder_class = new Holder(view);
        return holder_class;
    }

    // метод позволяет нам возвращать фактический идентификатор макета,
    // который платформа Android сохраняет для нас в качестве ресурса макета.
    @Override
    public int getItemViewType(final int position) {
        return R.layout.row_users;
    }

    // onBindViewHolder() - получает весь список. Это часть для всего списка
    // RecyclerView использует тот факт, что при прокрутке и появлении новых строк на экране старые строки
    // также исчезают с экрана. Вместо создания нового представления для каждой новой строки старое представление
    // перерабатывается и используется повторно, привязывая к нему новые данные. Это происходит именно в onBindViewHolder()
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ObjectContact objectContact = userList.get(position);

        holder.tvPrefix.setText(objectContact.name.substring(0,1)); //
        holder.tvUsername.setText(objectContact.name);
        holder.tv_phone.setText(objectContact.phone);

        // появиться зеленый кружок онлайн
        if(objectContact.status_online.equals("1")) { holder.img_status_online.setVisibility(View.VISIBLE); }
        // не появиться зеленый кружок онлайн
        else { holder.img_status_online.setVisibility(View.GONE); }

        // установка картинки что приложение установено на телефоне пользователя
        if (objectContact.status_setup.equals("1")) { holder.img_setupapp.setVisibility(View.VISIBLE); }
        else { holder.img_setupapp.setVisibility(View.GONE); }

        // holder.tvPrefix.setText(objectMessage.getPhoto());
        // установка статуса online иначе offline всему списку
//        if (objectMessage.status.equals("1")) { holder.img_status_online.setVisibility(View.VISIBLE); }
//        else { holder.img_status_online.setVisibility(View.GONE); }
//
//
//        Log.e("ADAPTER","-msg_status-" + objectMessage.msg_status);
//        if (objectMessage.msg_status.equals("1")) { holder.img_msg_status.setImageResource(R.drawable.msg_sent); };
//            if (objectMessage.msg_status.equals("2")) { holder.img_msg_status.setImageResource(R.drawable.msg_unread); };
//                if (objectMessage.msg_status.equals("3")) { holder.img_msg_status.setImageResource(R.drawable.msg_read); };
//
        // нажатие на любой элемент списка
        holder.itemView.setOnClickListener(new View.OnClickListener() { //  делаю переход из fragment_main во фрагмент_send
            @Override
            public void onClick(View view) {
                // написать условие если приложение не установлено то в пользователя не провалиться
                // и во фрагемнт сенд не зайдет
                if (objectContact.status_setup.equals("0"))
                {
                    // КОНТЕЙНЕР ДЛЯ ТОГО, ЧТОБЫ ПОДЕЛИТЬСЯ ПРИЛОЖЕНИЕМ
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    String shareSub = ((MainActivity)context).getResources().getString(R.string.shareSub); // тема письма
                    // вставить ссылку на правильное приложение
                    String shareBody = "Бесплатно:    https://play.google.com/store/apps/details?id=com.dev_marinov.xo"; //  тело письма
                    intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    ((MainActivity)context).startActivity(Intent.createChooser(intent, "Поделиться игрой"));
                }
                else
                {
                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
                    Fragment_Send fragment_send = new Fragment_Send();
                    //holder.img_read.setImageResource(R.drawable.msg_read); //
                    //fragment_send.selectedUser(objectMessage, position);
//                fragment_send.setParam(objectContact.name, objectContact.google_id, objectContact.status,
//                        objectContact.txt, objectMessage.msg_status);

                    fragment_send.setParam(objectContact.name, objectContact.not_my_google_id,  objectContact.status_online, "", "");

                    //fragment_send.setParam(objectContact.name, objectContact.status_online);
                    fragmentTransaction.replace(R.id.ll_frag_send, fragment_send);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity)context).ll_frag_send.setVisibility(View.VISIBLE);
//                selectedUser.selectedUser(objectMessage, position);

                }
            }
        });

//        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
//        Fragment_main fragment_main = new Fragment_main();
//
//        //fragment_send.selectedUser(objectMessage, position);
//        fragment_main.setParam(objectMessage.google_name, objectMessage.google_id, objectMessage.status, objectMessage.txt);
//
//        Log.e("ADAPTER ","-objectMessage.status-" + objectMessage.status);
//        Log.e("ADAPTER ","-objectMessage.txt-" + objectMessage.txt);
        //fragment_send.setParam(objectMessage.google_id);

       // fragmentTransaction.replace(R.id.ll_frag_send, fragment_send);
      //  fragmentTransaction.addToBackStack(null);
       // fragmentTransaction.commit();
       // ((MainActivity)context).ll_frag_main.setVisibility(View.GONE);
     //   ((MainActivity)context).ll_frag_send.setVisibility(View.VISIBLE);
    }
// Этот метод возвращает размер коллекции, содержащей элементы, которые мы хотим отобразить.
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface SelectedUser{
        void selectedUser(ObjectMessage objectMessage, int position);
    }

    // Для хранения ссылок на используемые элементы ImageView и TextView определен внутренний класс Holder,
    // который в конструкторе получает объект View, содержащий ImageView и TextView.
    public class Holder extends RecyclerView.ViewHolder {
        TextView tvPrefix, tvUsername, tv_phone;
        ImageView img_status_online, img_setupapp;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvPrefix = itemView.findViewById(R.id.prefix);
            tvUsername = itemView.findViewById(R.id.username);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            img_status_online = itemView.findViewById(R.id.img_status_online);
            img_setupapp = itemView.findViewById(R.id.img_setupapp);

        }
    }



}