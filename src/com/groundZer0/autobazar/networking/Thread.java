package com.groundZer0.autobazar.networking;

import com.groundZer0.autobazar.data.users.User;

import java.io.*;
import java.net.Socket;

public class Thread extends java.lang.Thread {
    private Socket socket;

    public Thread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Connection from " + this.socket + "!");
        try{
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            User user = (User) objectInputStream.readObject();
            user.setPassword("rere");
            System.out.println("length of received users " + user.getPassword());

            System.out.println("Closing sockets.");
            objectOutputStream.writeObject(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
