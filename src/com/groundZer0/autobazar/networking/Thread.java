package com.groundZer0.autobazar.networking;

import com.groundZer0.autobazar.data.security.UserSecurity;
import com.groundZer0.autobazar.data.users.User;
import com.groundZer0.autobazar.data.users.UsersOps;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
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
            User user = null;
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            Object o = (Object) objectInputStream.readObject();
            user = o instanceof User ? (User) o : null;

            /* application ending */
            if(user == null){
                System.out.println("End of application session. saving users...!");
                UsersOps.getUsersOps().users_saving();
                socket.close();
                return;
            }

            /* app user want registered */
            else if (Objects.equals(user.getOperation_note(), "registration")) {
                User new_user = registration_new_user(user);
                objectOutputStream.writeObject(new User("success"));
            }

            /* app user want registered */
            else if (Objects.equals(user.getOperation_note(), "registration_admin")) {
                User new_user = registration_new_user(user);
                new_user.setOperation_note("success");
                objectOutputStream.writeObject(new_user);
            }

            /* Process of login */
            else if(Objects.equals(user.getOperation_note(), "login_credentials")){
                User logged_user = login(user);
                if(logged_user == null){
                    objectOutputStream.writeObject(null);
                    return;
                }
                System.out.println("user logged " + logged_user.getFirst_name());
                objectOutputStream.writeObject(logged_user);

                /* Ak user bol admin, cakaj este na odpoved a posli mu vsetkych userov v db */
                if(Objects.equals(logged_user.getPrivilages(), "admin")){
                    User admin_user = (User) objectInputStream.readObject();
                    System.out.println(admin_user.getOperation_note());

                    if(login(admin_user) != null){
                        System.out.println("admin logged " + logged_user.getFirst_name());
                        Object[] data = new Object[list_of_users.size()];
                        int counter = 0;
                        for(User list_user : list_of_users){
                            data[counter] = list_user;
                            counter++;
                        }
                        objectOutputStream.writeObject(data);
                    }
                }
            }

            else if(Objects.equals(user.getOperation_note(), "delete_user")){
                /* TODO check if request is from admin user */
                User deleted_user = user_delete(user);

                objectOutputStream.writeObject(deleted_user);
            }

            System.out.println("Closing no - sockets.");
            socket.close();
        } catch (IOException | ClassNotFoundException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * helper function to registred new user
     * @param user
     * @return
     */
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

    /**
     * helper function in logged in
     * @param user
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private User login(User user) throws BadPaddingException, IllegalBlockSizeException {
        list_of_users = UsersOps.getUsersOps().getUsers();
        UserSecurity userSecurity = UserSecurity.getInstance();

        for(User db_user : list_of_users){
            PrivateKey user_pvt_key = db_user.getPrivate_key_from_bytes();
            String db_user_passwd = userSecurity.decrypt(db_user.getToken(), user_pvt_key);
            if (Objects.equals(db_user.getEmail(), user.getEmail()) && Objects.equals(db_user_passwd, user.getPassword())) {
                db_user.setOperation_note("success");
                return new User(db_user.getFirst_name(), db_user.getLast_name(), db_user.getPhone_number(), db_user.getBirth(), db_user.getEmail(), db_user.getPrivilages(), db_user.getPublic_key(), db_user.getToken(), "success");
            }
        }

        return null;
    }

    /**
     * helper function to delete user
     * @param user
     * @return
     */
    private User user_delete(User user){
        list_of_users = UsersOps.getUsersOps().getUsers();
        for(User list_user : list_of_users){
            if(Objects.equals(user.getEmail(), list_user.getEmail()) && Objects.equals(user.getToken(), list_user.getToken())){
                UsersOps.getUsersOps().remove_user(list_user);

                return new User("success");
            }
        }

        return null;
    }
}
