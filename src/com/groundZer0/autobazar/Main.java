package com.groundZer0.autobazar;

import com.groundZer0.autobazar.data.users.User;
import com.groundZer0.autobazar.data.users.UsersOps;
import com.groundZer0.autobazar.networking.Thread;

import java.io.*;
import java.net.ServerSocket;

public class Main {
    private static int PORT = 8000;

    public static void main(String[] args) {
        UsersOps.getUsersOps().users_loading();
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            while (true){
                System.out.println("ServerSocket awaiting connections...");
                new Thread(serverSocket.accept()).start();
            }
//            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
