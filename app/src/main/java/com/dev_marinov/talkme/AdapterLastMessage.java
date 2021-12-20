package com.dev_marinov.talkme;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

class AdapterLastMessage extends RecyclerView.Adapter<AdapterLastMessage.Holder> {
    HashMap<Integer, ObjectMessage> list_last_message_user;
    Context context;

    public AdapterLastMessage(Context context, HashMap<Integer, ObjectMessage> list_last_message_user) {
        this.context = context;
        this.list_last_message_user = list_last_message_user;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_last_message, parent, false);
        Holder holder_class = new Holder(view);
        return holder_class;
    }

    @Override
    public int getItemViewType(int position) { // ЕСТЬ ЕЩЕ getViewTypeCount
        return R.layout.row_last_message;
    }

    // onBindViewHolder - заполняет список пользователей в момент нахождения в области экрана
    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterLastMessage.Holder holder, int position) {
            ObjectMessage objectMessage = list_last_message_user.get(position);

        holder.tvPrefix.setText(objectMessage.google_name.substring(0,1)); //
        holder.tvLastMsg.setText(objectMessage.txt); // текст последнего сообщения
        holder.tvUsername.setText(objectMessage.google_name); // сюда приходит имя тому кому я пишу
        // holder.tvPrefix.setText(objectMessage.getPhoto());
        // установка статуса online иначе offline всему списку
        if (objectMessage.status.equals("1")) { holder.img_status_online.setVisibility(View.VISIBLE); }
        else { holder.img_status_online.setVisibility(View.GONE); }

        // нажатие на любой элемент списка
        holder.itemView.setOnClickListener(new View.OnClickListener() { //  делаю переход из fragment_main во фрагмент_send
            @Override
            public void onClick(View view) {

                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
                    Fragment_Send fragment_send = new Fragment_Send();
                    //holder.img_read.setImageResource(R.drawable.msg_read); //
                    //fragment_send.selectedUser(objectMessage, position);
//                fragment_send.setParam(objectContact.name, objectContact.google_id, objectContact.status,
//                        objectContact.txt, objectMessage.msg_status);

                    fragment_send.setParam(objectMessage.google_name, objectMessage.not_my_google_id,  objectMessage.status, "", "");

                    //fragment_send.setParam(objectContact.name, objectContact.status_online);
                    fragmentTransaction.replace(R.id.ll_frag_send, fragment_send);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity)context).ll_frag_send.setVisibility(View.VISIBLE);
//                selectedUser.selectedUser(objectMessage, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_last_message_user.size(); // получаю размер hasmap массива
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView tvPrefix;
        TextView tvUsername;
        ImageView img_status_online, img_msg_status;
        TextView tvLastMsg;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvPrefix = itemView.findViewById(R.id.prefix);
            tvUsername = itemView.findViewById(R.id.username);
            img_status_online = itemView.findViewById(R.id.img_status_online);
            img_msg_status = itemView.findViewById(R.id.img_msg_status);
            tvLastMsg = itemView.findViewById(R.id.tvLastMsg);
        }
    }
}