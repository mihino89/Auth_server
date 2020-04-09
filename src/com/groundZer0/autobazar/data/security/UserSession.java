package com.groundZer0.autobazar.data.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Date;

public class UserSession {
    private Date time_of_loggin;

    private PublicKey pub;
    private PrivateKey pvt;

    /* Singleton */
    private static UserSession userSession;

    private UserSession(Date time_of_loggin) {
        this.time_of_loggin = time_of_loggin;

        generateTokens();
    }

    private void generateTokens(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            pub = keyPair.getPublic();
            pvt = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static UserSession getInstance(Date time_of_loggin){
        userSession = new UserSession(time_of_loggin);
        return userSession;
    }

    public Date getTime_of_loggin() {
        return time_of_loggin;
    }

    public String encrypt(String passwd, PublicKey publicKey){
        byte[] cipherText = new byte[0];

        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = encryptCipher.doFinal(passwd.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String cipherText, PrivateKey privateKey) throws BadPaddingException, IllegalBlockSizeException {
        Cipher decriptCipher = null;
        byte[] bytes = Base64.getDecoder().decode(cipherText);
        try{
            decriptCipher = Cipher.getInstance("RSA");
            decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        assert decriptCipher != null;
        return new String(decriptCipher.doFinal(bytes), StandardCharsets.UTF_8);
    }

    public PublicKey getPublicKey() {
        return pub;
    }

    public void test(){
        String test = "toto_je_test";
        String token = encrypt(test, pub);

        System.out.println(token);
        try{
            System.out.println(decrypt(token, pvt));
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
