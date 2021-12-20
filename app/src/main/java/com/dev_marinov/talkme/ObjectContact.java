package com.dev_marinov.talkme;

class ObjectContact {

    public String name;
    public String photo;
    public String id_row,phone, status_setup;
    public String status_online;
    public String not_my_google_id;

    public ObjectContact(String name, String photo, String id_row, String phone, String status_setup, String status_online, String not_my_google_id) {
        this.name = name;
        this.photo = photo;
        this.id_row = id_row;
        this.phone = phone;
        this.status_setup = status_setup;
        this.status_online = status_online;
        this.not_my_google_id = not_my_google_id;
    }
}
