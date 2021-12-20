package com.dev_marinov.talkme;

class ObjectNoSendMessage {

    String msg;
    //String dateTime;
    String google_id, get_user_google_id;

    public ObjectNoSendMessage(String msg, String google_id, String get_user_google_id) {
        this.msg = msg;
      //  this.dateTime = dateTime;
        this.google_id = google_id;
        this.get_user_google_id = get_user_google_id;
    }
}
