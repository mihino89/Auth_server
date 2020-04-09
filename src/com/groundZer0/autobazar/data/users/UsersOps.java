package com.groundZer0.autobazar.data.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;


public class UsersOps {

    /* Singleton */
    private static UsersOps usersOps = new UsersOps();
    /* DB of users */
    private final String file_name = "src/com/groundZer0/autobazar/data/db/users.json";
    Path path = Paths.get(file_name);

    private ObservableList<User> list_of_users;
    private DateTimeFormatter dateTimeFormatter;

    /* Singleton - private constructor */
    private UsersOps() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public void users_loading() {
        list_of_users = FXCollections.observableArrayList();
        JSONParser jsonParser = new JSONParser();

        try (FileReader fileReader = new FileReader(String.valueOf(path))) {
            Object obj = jsonParser.parse(fileReader);

            JSONArray users = (JSONArray) obj;
            System.out.println(users);

            users.forEach( user -> parseUsers((JSONObject) user));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void users_saving(){
        JSONArray users = new JSONArray();
        for(User user : list_of_users) {
            JSONObject user_detail = new JSONObject();

            user_detail.put("first_name", user.getFirst_name());
            user_detail.put("last_name", user.getLast_name());
            user_detail.put("phone_number", user.getPhone_number());
            user_detail.put("birth", user.getBirth().format(dateTimeFormatter));
            user_detail.put("email", user.getEmail());
            user_detail.put("privilages", user.getPrivilages());
            user_detail.put("public_key", Arrays.toString(user.getPublic_key()));
            user_detail.put("private_key", Arrays.toString(user.getPrivate_key()));
            user_detail.put("token", user.getToken());

//            JSONObject user_json = new JSONObject();
//            user_json.put(user.getPrivilages(), user_detail);
            users.add(user_detail);
        }

        try(FileWriter file = new FileWriter(String.valueOf(path))){
            file.write(users.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseUsers(org.json.simple.JSONObject user){
        String first_name = (String) user.get("first_name");
        String last_name = (String) user.get("last_name");
        String phone_number = (String) user.get("phone_number");
        LocalDate localDate = LocalDate.parse((String) user.get("birth"), dateTimeFormatter);
        String email = (String) user.get("email");
        String privilages = (String) user.get("privilages");
        byte[] public_key = ((String) user.get("public_key")).getBytes();
        byte[] private_key = ((String) user.get("private_key")).getBytes();
        String token = (String) user.get("token");

        add_user(new User(first_name, last_name, phone_number, localDate, email, privilages, public_key, private_key, token));
    }

    /* Singleton - return only one instance of class*/
    public static UsersOps getUsersOps() {
        return usersOps;
    }

    /* return list of users */
    public ObservableList<User> getUsers() {
        return list_of_users;
    }

    public void setUsers(ObservableList<User> users) {
        this.list_of_users = users;
    }

    public void add_user(User new_user){
        list_of_users.add(new_user);
    }

    public void remove_user(User new_user){
        list_of_users.remove(new_user);
    }
}
