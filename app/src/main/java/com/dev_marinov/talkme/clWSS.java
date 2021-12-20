package com.dev_marinov.talkme;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


class clWSS { // класс для раоты с вебсокетом

    Context context;
    WebSocket webSocket;
    public OkHttpClient client;
    String ws_url = "wss://dev-marinov.ru:8081";
    onGetMessage onGetMessage;
    onGetConnect onGetConnect;


    // подключение к сокету это интерфейс, позволяющий связывать между собой программы различных устройств,
    // находящихся в одной сети.
    // Сокеты бывают двух типов: клиентский (Socket) и серверный (ServerSocket). Главное различие между ними
    // связано с тем, что сервер
    // «открывает» определенный порт на устройстве, «слушает» его и обрабатывает поступающие запросы,
    // а клиент должен подключиться к этому серверу, зная его IP-адрес и порт.
    public void ConnectSocket(final Context context) {
        this.context = context;

        Request request = new Request.Builder()
                .url(ws_url)
                // .header("Auth-Token", "secret-api-token-here")
                .build();
        try { client = getSSLClient(context); }
        catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        catch (KeyStoreException e) { e.printStackTrace(); }
        catch (KeyManagementException e) { e.printStackTrace(); }
        catch (CertificateException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

           // создадим экз подключения по сокету
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                onGetConnect.onGetConnect(true);
                Log.e("onOpen=","clWSS");
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                 onGetMessage.onGetMessage(text);
            }

            // метод закрытия клиентского сокета
            @Override
            public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosing(webSocket, code, reason);
                onGetConnect.onGetConnect(false);
                Log.e("onClosing=","clWSS");
            }

            // метод закрытия клиентского сокета
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                super.onClosed(webSocket, code, reason);
                onGetConnect.onGetConnect(false);
                Log.e("onClosed=","clWSS");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                onGetConnect.onGetConnect(false);
            }
        });
    }

    public void disconnect() // отправляет на сервеср ссобщнеие текст
    {
         try {
            webSocket.close(100,"100"); // 100 это ручное отключение от сервера (не аварийное )
        }
        catch (Exception ignored)
        {

        }
    }

    public void send(String msg) // отправляет на сервеср ссобщнеие текст
    {
        try {
            webSocket.send(msg);
        }
        catch (Exception ignored)
        {

        }
    }

    interface onGetMessage { void onGetMessage(String text);}

    public void setonGetMessage( onGetMessage onGetMessage)
    {
        this.onGetMessage = onGetMessage;
    }

    interface onGetConnect { void onGetConnect(boolean isconnect);}

    public void setonGetConnect( onGetConnect onGetConnect)
    {
        this.onGetConnect = onGetConnect;
    }

    // безопасный протокол ssl
    private static OkHttpClient getSSLClient(Context context) throws
            NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException,
            CertificateException,
            IOException {

        OkHttpClient client;
        SSLContext sslContext;
        SSLSocketFactory sslSocketFactory;
        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory;
        X509TrustManager trustManager;

        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(readKeyStore(context));
        trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        trustManager = (X509TrustManager) trustManagers[0];
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        sslSocketFactory = sslContext.getSocketFactory();
        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();
        return client;
    }

    private static KeyStore readKeyStore(Context context) throws
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            IOException {
        KeyStore ksTrust = KeyStore.getInstance("BKS");
        InputStream instream = context.getResources().openRawResource(R.raw.devmarinov);

        ksTrust.load(instream, "73735555".toCharArray());
        return ksTrust;
    }
}
