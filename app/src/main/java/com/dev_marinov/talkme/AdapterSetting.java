package com.dev_marinov.talkme;

import android.content.Context;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

// Адаптер, который используется в RecyclerView, должен наследоваться от абстрактного класса RecyclerView.Adapter.
// Этот класс определяет три метода:
// onCreateViewHolder: возвращает объект ViewHolder, который будет хранить данные по одному объекту Phone.
// onBindViewHolder: выполняет привязку объекта ViewHolder к объекту Phone по определенной позиции.
// getItemCount: возвращает количество объектов в списке
class AdapterSetting extends RecyclerView.Adapter<AdapterSetting.Holder>{
    private HashMap<Integer,ObjectSetting> settingList; // расширяемый список настроек
    private Context context;

    //............
    GoogleSignInClient mGoogleSignInClient;

    //...........



    // Все взаимодействие со списком здесь будет идти через класс AdapterUser.
    // В конструкторе AdapterUser нам надо передать в конструктор базового класса два параметра:
    // контекст, в котором используется класс. В его роли кк правило выступает класс Activity
    // и набор объектов, которые будут выводиться в ListView
    // В конструкторе AdapterUser мы получаем набор объектов и сохраняем их в отдельные переменные.
    public AdapterSetting(Context context, HashMap<Integer,ObjectSetting> settingList) {
        this.settingList = settingList;
        this.context = context;
    }

    @NonNull
    @Override
    // В методе onCreateViewHolder() устанавливается отображение элемента списка.
    // В данном случае с помощью объекта LayoutInflater создаем объект View для каждого отдельного элемента в списке:
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_setting,parent,false);
        Holder holder_class = new Holder(view);
        return holder_class;
    }

    // метод позволяет нам возвращать фактический идентификатор макета,
    // который платформа Android сохраняет для нас в качестве ресурса макета.
    @Override
    public int getItemViewType(final int position) {
        return R.layout.row_setting;
    }

    // onBindViewHolder() - получает весь список. Это часть для всего списка
    // RecyclerView использует тот факт, что при прокрутке и появлении новых строк на экране старые строки
    // также исчезают с экрана. Вместо создания нового представления для каждой новой строки старое представление
    // перерабатывается и используется повторно, привязывая к нему новые данные. Это происходит именно в onBindViewHolder()
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ObjectSetting objectSetting = settingList.get(position);

        Log.e("ADAP_SET","-objectSetting.my_name_setting-" + objectSetting.my_name_setting);
        holder.tv_setting.setText(objectSetting.my_name_setting);
//        holder.img_icon_setting.setText(objectSetting.name);

        if(objectSetting.my_name_setting.equals("ИМЯ"))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() { //  делаю переход из fragment_main во фрагмент_send
                @Override
                public void onClick(View view) {

                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
                    Fragment_Setting fragment_setting = new Fragment_Setting();
                    //holder.img_read.setImageResource(R.drawable.msg_read); //
                    //fragment_send.selectedUser(objectMessage, position);
//                fragment_send.setParam(objectContact.name, objectContact.google_id, objectContact.status,
//                        objectContact.txt, objectMessage.msg_status);
                    fragment_setting.setParam(objectSetting.my_name_setting, objectSetting.my_photo_setting, objectSetting.my_status_sleep);
                    fragmentTransaction.replace(R.id.ll_frag_setting, fragment_setting);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity)context).ll_frag_setting.setVisibility(View.VISIBLE);
                }
            });
        }




        if(objectSetting.my_name_setting.equals("СТАТУС"))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() { //  делаю переход из fragment_main во фрагмент_send
                @Override
                public void onClick(View view) {

                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
                    Fragment_Setting fragment_setting = new Fragment_Setting();
                    //holder.img_read.setImageResource(R.drawable.msg_read); //
                    //fragment_send.selectedUser(objectMessage, position);
//                fragment_send.setParam(objectContact.name, objectContact.google_id, objectContact.status,
//                        objectContact.txt, objectMessage.msg_status);
                    fragment_setting.setParam(objectSetting.my_name_setting, objectSetting.my_photo_setting, objectSetting.my_status_sleep);
                    fragmentTransaction.replace(R.id.ll_frag_setting, fragment_setting);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity)context).ll_frag_setting.setVisibility(View.VISIBLE);
                }
            });
        }



// условие для выхода из приложения при нажатии на кнопку ВЫЙТИ В НАСТРОЙКАХ
        if(objectSetting.my_name_setting.equals("ВЫЙТИ"))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    mGoogleSignInClient = GoogleSignIn.getClient(context, gso); // возможно requireContext()
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //     ((MainActivity) getActivity()).show_or_hide_fragment("auth",new String[]{});
                            ((MainActivity)context).ll_frag_home.removeAllViews();
                            FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment_auth fragment_auth = new Fragment_auth();
                            fragmentTransaction.replace(R.id.ll_frag_auth, fragment_auth);
                            fragmentTransaction.commit();
                        }
                    });
                }
            });
        }
        else {
            // иначе любое другое нажатие на любой элемент списка
//            holder.itemView.setOnClickListener(new View.OnClickListener() { //  делаю переход из fragment_main во фрагмент_send
//                @Override
//                public void onClick(View view) {
//                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager(); // получить доступ к фрагментам
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); // добавить фрагмента в стек
//                    Fragment_Setting fragment_setting = new Fragment_Setting();
//                    //holder.img_read.setImageResource(R.drawable.msg_read); //
//                    //fragment_send.selectedUser(objectMessage, position);
////                fragment_send.setParam(objectContact.name, objectContact.google_id, objectContact.status,
////                        objectContact.txt, objectMessage.msg_status);
//                    fragment_setting.setParam(objectSetting.my_name_setting, objectSetting.my_photo_setting, objectSetting.my_status_sleep);
//                    fragmentTransaction.replace(R.id.ll_frag_setting, fragment_setting);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                    ((MainActivity)context).ll_frag_setting.setVisibility(View.VISIBLE);
//                }
//            });
        }







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
        return settingList.size();
    }

    public interface SelectedUser{
        void selectedUser(ObjectMessage objectMessage, int position);
    }

    // Для хранения ссылок на используемые элементы ImageView и TextView определен внутренний класс Holder,
    // который в конструкторе получает объект View, содержащий ImageView и TextView.
    public class Holder extends RecyclerView.ViewHolder {
        TextView tv_setting;
        //ImageView img_icon_setting;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_setting = itemView.findViewById(R.id.tv_setting);
           // img_icon_setting = itemView.findViewById(R.id.img_icon_setting);
        }
    }
}