package com.dev_marinov.talkme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.Holder> {

    Context context;
    HashMap<Integer,ObjectMessage> message = new HashMap<>();

    public AdapterMessage(Context con, HashMap<Integer, ObjectMessage> message) {
        this.context = con;
        this.message = message;
    }

    public void update_list_message(HashMap<Integer, ObjectMessage> message1)
    {  this.message = message1;
       notifyDataSetChanged(); // принудительное обновление адаптера из frag_send
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_message, parent, false);
        Holder h = new Holder(view);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        // тут измнить вид исх и вх сообщений // 1 - это тому кому написали, 0 - тот, кто написал
//        Log.e("ADAP_MES","-flag_user-1" + message.get(position).flag_user);
//        Log.e("ADAP_MES","-message.size()" + message.size());

           if (message.size() == 0)
       {

       }

//        if (string_txt.equals("")) {holder.tv_msg_null.setVisibility(View.VISIBLE); // покажет что нет сообщений
//        } else {holder.tv_msg_null.setVisibility(View.GONE); } // не появиться сообщений

        if (message.get(position).flag_user.equals("0")) //Получатель
        {
            holder.cl_user2.setVisibility(View.VISIBLE);
            holder.cl_user1.setVisibility(View.GONE);
            holder.message_text_incoming.setText(message.get(position).txt);
           // holder.user_name_text_incoming.setText(message.get(position).google_name);
            holder.time_text_incoming.setText(message.get(position).datatime);

            ////////////////////////////////
            // здесь написать код который будет просматривать сообщения как получатель и каким-то образом ставить галочки
            // что прочитал сообщения отправителя

//            holder.img_msg_read.setVisibility(View.VISIBLE);
//            holder.img_msg_unread.setVisibility(View.GONE);
//            holder.img_msg_send.setVisibility(View.GONE);
            ////////////////////////////////
        }
        else {
            Log.e("ADAP_MES","-flag_user-2" + message.get(position).flag_user);
            if(message.get(position).msg_status.equals("1"))
            {
            holder.img_msg_send.setVisibility(View.VISIBLE); // отправлено сообщение на сервер
            }
            if(message.get(position).msg_status.equals("2"))
            {
            holder.img_msg_unread.setVisibility(View.VISIBLE);  // отправлено сообщение на сервер доставлено получателю
            }
            if(message.get(position).msg_status.equals("3"))
            {
            holder.img_msg_read.setVisibility(View.VISIBLE);  // отправлено сообщение на сервер доставлено получателю и прочитано
            }
            holder.cl_user1.setVisibility(View.VISIBLE);
            holder.message_text_out_going.setText(message.get(position).txt);
            //holder.user_name_text_out_going.setText(message.get(position).google_name);
            holder.time_text_out_going.setText(message.get(position).datatime);
            holder.cl_user2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_user1,cl_user2;
        TextView message_text_out_going,message_text_incoming;
        TextView time_text_out_going, user_name_text_out_going, user_name_text_incoming, time_text_incoming, tv_msg_null;
        ImageView img_msg_send, img_msg_read, img_msg_unread;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);
            cl_user1 = itemView.findViewById(R.id.cl_user1);
            cl_user2 = itemView.findViewById(R.id.cl_user2); //получатель
            message_text_out_going = itemView.findViewById(R.id.message_text_out_going);
            message_text_incoming = itemView.findViewById(R.id.message_text_incoming);
            time_text_out_going = itemView.findViewById(R.id.time_text_out_going);
//            user_name_text_out_going = itemView.findViewById(R.id.user_name_text_out_going);
//            user_name_text_incoming = itemView.findViewById(R.id.user_name_text_incoming);
            time_text_incoming = itemView.findViewById(R.id.time_text_incoming);
            tv_msg_null = itemView.findViewById(R.id.tv_msg_null);
            img_msg_send = itemView.findViewById(R.id.img_msg_send);
            img_msg_read = itemView.findViewById(R.id.img_msg_read);
            img_msg_unread = itemView.findViewById(R.id.img_msg_unread);
        }

    }
}
