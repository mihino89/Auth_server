package com.groundZer0.autobazar.data.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;


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
            if(fileReader.read() == -1){
                return;
            }
            FileReader not_empty_file_reader = new FileReader(String.valueOf(path));
            Object obj = jsonParser.parse(not_empty_file_reader);

            JSONArray users = (JSONArray) obj;

            users.forEach( user -> parseUsers((JSONObject) user));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void users_saving(){
        JSONArray users = new JSONArray();
        System.out.println("users length: " + list_of_users.size());
        for(User user : list_of_users) {
            JSONObject user_detail = new JSONObject();

            user_detail.put("first_name", user.getFirst_name());
            user_detail.put("last_name", user.getLast_name());
            user_detail.put("phone_number", user.getPhone_number());
            user_detail.put("birth", user.getBirth().format(dateTimeFormatter));
            user_detail.put("email", user.getEmail());
            user_detail.put("privilages", user.getPrivilages());
            user_detail.put("public_key", Base64.getEncoder().encodeToString(user.getPublic_key()));
            user_detail.put("private_key", Base64.getEncoder().encodeToString(user.getPrivate_key()));
            user_detail.put("token", user.getToken());

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
        String pvt = (String) user.get("private_key");
//        System.out.println(pvt);
        String pub = (String) user.get("private_key");
        byte[] public_key = Base64.getDecoder().decode(pub);
        byte[] private_key = Base64.getDecoder().decode(pvt);
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
