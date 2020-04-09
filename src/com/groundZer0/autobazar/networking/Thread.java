package com.groundZer0.autobazar.networking;

import com.groundZer0.autobazar.data.users.User;
import com.groundZer0.autobazar.data.users.UsersOps;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Thread extends java.lang.Thread {
    private Socket socket;

    public Thread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Connection from " + this.socket + "!");
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            User user = (User) objectInputStream.readObject();

            if(user.getOperation_note() == null){
                UsersOps.getUsersOps().users_saving();
                socket.close();
            }
            else if (Objects.equals(user.getOperation_note(), "registration")) {
                UsersOps.getUsersOps().add_user(user);
                System.out.println("old password " + user.getPassword());
                user.setPassword("rere");
                System.out.println("new password " + user.getPassword());

                objectOutputStream.writeObject(user);
            }

            System.out.println("Closing sockets.");
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
