package com.groundZer0.autobazar.networking;

import com.groundZer0.autobazar.data.security.UserSession;
import com.groundZer0.autobazar.data.users.User;
import com.groundZer0.autobazar.data.users.UsersOps;

import java.io.*;
import java.net.Socket;
import java.util.Date;
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

            /* application ending */
            if(user == null){
                System.out.println("End of application session.!");
                UsersOps.getUsersOps().users_saving();
                socket.close();
            }
            /* app user want registered */
            else if (Objects.equals(user.getOperation_note(), "registration")) {
                registration_new_user(user);
                System.out.println("user registered");
                UsersOps.getUsersOps().add_user(user);
                System.out.println("user added to array");
//                System.out.println("old password " + user.getPassword());
//                user.setPassword("rere");
//                System.out.println("new password " + user.getPassword());

                objectOutputStream.writeObject(new User("success"));
            }

            System.out.println("Closing sockets.");
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private User registration_new_user(User user){
        Date session_start_time;
        UserSession userSession;
        String session_token;

        session_start_time = new Date(System.currentTimeMillis());
        userSession = UserSession.getInstance(session_start_time);

        user.setPublic_key(userSession.getPublicKey().getEncoded());
        user.setPrivate_key(userSession.getPrivateKey().getEncoded());

        session_token = userSession.encrypt(user.getPassword(), userSession.getPublicKey());
        user.setToken(session_token);

        return user;
    }
}
