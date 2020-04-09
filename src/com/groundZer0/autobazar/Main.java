package com.groundZer0.autobazar;

import com.groundZer0.autobazar.datamodel.users.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            System.out.println("ServerSocket awaiting connections...");
            Socket socket = serverSocket.accept();
            System.out.println("Connection from " + socket + "!");

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            User user = (User) objectInputStream.readObject();
            user.setPassword("rere");
            System.out.println("length of received users " + user.getPassword());

            System.out.println("Closing sockets.");
            objectOutputStream.writeObject(user);
//            serverSocket.close();
//            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
