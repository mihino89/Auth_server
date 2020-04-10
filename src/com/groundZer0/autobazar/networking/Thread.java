package com.groundZer0.autobazar.networking;

import com.groundZer0.autobazar.data.security.UserSecurity;
import com.groundZer0.autobazar.data.users.User;
import com.groundZer0.autobazar.data.users.UsersOps;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Thread extends java.lang.Thread {
    private Socket socket;
    private List<User> list_of_users;

    public Thread(Socket socket) {
        this.socket = socket;
        this.list_of_users = UsersOps.getUsersOps().getUsers();
    }

    @Override
    public void run() {
        System.out.println("Accept connection from " + this.socket);
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            User user = (User) objectInputStream.readObject();

            /* application ending */
            if(user == null){
                System.out.println("End of application session. saving users...!");
                UsersOps.getUsersOps().users_saving();
                socket.close();
            }
            /* app user want registered */
            else if (Objects.equals(user.getOperation_note(), "registration")) {
                User new_user = registration_new_user(user);
                System.out.println("user registered " + user.getFirst_name());
                objectOutputStream.writeObject(new User("success"));
            }
            /* Process of login */
            else if(Objects.equals(user.getOperation_note(), "login_credentials")){
                User logged_user = login(user);
                if(logged_user != null){
                    System.out.println("user logged " + logged_user.getFirst_name());
                }

                objectOutputStream.writeObject(logged_user);
            }

            System.out.println("Closing sockets.");
            socket.close();
        } catch (IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private User registration_new_user(User user){
        Date session_start_time;
        UserSecurity userSecurity;
        String session_token;

        session_start_time = new Date(System.currentTimeMillis());
        userSecurity = UserSecurity.getInstance(session_start_time);

        user.setPublic_key(userSecurity.getPublicKey().getEncoded());
        user.setPrivate_key(userSecurity.getPrivateKey().getEncoded());

        session_token = userSecurity.encrypt(user.getPassword(), userSecurity.getPublicKey());
        user.setToken(session_token);

        UsersOps.getUsersOps().add_user(user);
        return user;
    }

    private User login(User user) throws BadPaddingException, IllegalBlockSizeException {
        list_of_users = UsersOps.getUsersOps().getUsers();
        UserSecurity userSecurity = UserSecurity.getInstance();

        for(User db_user : list_of_users){
            System.out.println("funguje " + Arrays.toString(db_user.getPrivate_key()));
            PrivateKey user_pvt_key = db_user.getPrivate_key_from_bytes();
            System.out.println("funguje2 " + user_pvt_key);
            String db_user_passwd = userSecurity.decrypt(db_user.getToken(), user_pvt_key);
            System.out.println("funguje3 " + db_user.getFirst_name());
            if (Objects.equals(db_user.getEmail(), user.getEmail()) && Objects.equals(db_user_passwd, user.getPassword())) {
                System.out.println("found credential match");
                db_user.setOperation_note("success");
                return new User(db_user.getFirst_name(), db_user.getLast_name(), db_user.getPhone_number(), db_user.getBirth(), db_user.getEmail(), db_user.getPrivilages(), db_user.getPublic_key(), db_user.getToken(), "success");
            }
            System.out.println("funguje4 " + db_user.getFirst_name());
        }

        System.out.println("nie je v zozname - login");
        return null;
    }
}
