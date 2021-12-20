package com.dev_marinov.talkme;

class ObjectMessage {


    public String photo;
    public String id_row;
    public String google_name, google_id, status;
    public String flag_user;
    public String txt;
    public String datatime;
    public String msg_status;

    ///////////////////.........
    public String not_my_google_id;
    ///////////////////..........

    public ObjectMessage(String id_row,
                         String google_name,
                         String google_id,
                         String status,
                         String flag_user,
                         String txt,
                         String datatime,
                         String msg_status,
                         String not_my_google_id)
    {
        this.google_name = google_name;
        this.google_id = google_id;
        this.status = status;
        this.flag_user = flag_user;
        this.txt = txt;
        this.datatime = datatime;
        this.msg_status = msg_status;
       // this.photo = photo;
        this.id_row = id_row;
        this.not_my_google_id = not_my_google_id;
    }


}
