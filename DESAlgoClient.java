

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DESAlgoClient {


    private static final String ALGORITHM = "DES";




    public SecretKey getSecretKey(String clientKey) {
        try {
            DESKeySpec key = new DESKeySpec(clientKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            return keyFactory.generateSecret(key);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("No Algorithm found." + e);
        } catch (InvalidKeyException e) {
            System.err.println("Invalid key error. " + e);
        } catch (Exception e) {
            System.err.println("unknown error while initializing DES Key : " + e);
        }
        return null;
    }

    public String encryptMessage(String message, SecretKey secretKey) {
        String encryptedMessage = null;
        try {
            Cipher desCipher = Cipher.getInstance(ALGORITHM);
            desCipher.init(Cipher.ENCRYPT_MODE, secretKey, desCipher.getParameters());
            byte[] messageInBytes = message.getBytes();
            byte[] byteCipherText = desCipher.doFinal(messageInBytes);
            byte[] encoded = Base64.getEncoder().encode(byteCipherText);
            return new String(encoded);
        } catch (InvalidKeyException e) {
            System.err.println("Invalid Cipher Key.");
        } catch (Exception e) {
            System.err.println("Unknown Error.");
        }
        return encryptedMessage;
    }

    public String decryptMessage(String message, SecretKey secretKey) {
        String decryptedMessage = null;
        try {
            Cipher desCipher = Cipher.getInstance(ALGORITHM);
            desCipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] messageInBytes = message.getBytes();
            byte [] decodedBytes = Base64.getDecoder().decode(message.getBytes());
            byte[] byteCipherText = desCipher.doFinal(decodedBytes);
            decryptedMessage = new String(byteCipherText);
        } catch (InvalidKeyException e) {
            System.err.println("Invalid Cipher Key.");
        } catch (Exception e) {
            System.err.println("Unknown Error." + e);
        }
        return decryptedMessage;
    }

}
