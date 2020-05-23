package com.groundZer0.autobazar.data.users;

import java.io.Serializable;
import java.time.LocalDate;

public class RegistrationUser implements Serializable {
    /* Personal informations */
    private String first_name;
    private String last_name;
    private String phone_number;

    static final long serialVersionUID = 42L;
    private LocalDate birth;

    /* Credentials */
    private String email;
    private String password;
    private String privilages;

    /* Security */
    private byte[] public_key;
    private byte[] private_key;
    private String token;

    /* Operationals */
    private String operation_note;

    /* Registration constructor */
    public RegistrationUser(String first_name, String last_name, String phone_number, LocalDate birth, String email, String password, String privilages, String operation_note) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.privilages = privilages;
        this.operation_note = operation_note;
    }

    /* Registration constructor */
    public RegistrationUser(String first_name, String phone_number, LocalDate birth, String email, String password, String privilages, String operation_note) {
        this.first_name = first_name;
        this.phone_number = phone_number;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.privilages = privilages;
        this.operation_note = operation_note;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getOperation_note() {
        return operation_note;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public String getPrivilages() {
        return privilages;
    }

    public String getToken() {
        return token;
    }

    public void setPrivilages(String privilages) {
        this.privilages = privilages;
    }

    public byte[] getPublic_key() {
        return public_key;
    }

    public byte[] getPrivate_key() {
        return private_key;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setOperation_note(String operation_note) {
        this.operation_note = operation_note;
    }

    public void setPublic_key(byte[] public_key) {
        this.public_key = public_key;
    }

    public void setPrivate_key(byte[] private_key) {
        this.private_key = private_key;
    }


}
