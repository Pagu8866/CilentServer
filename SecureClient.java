


import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SecureClient {

    public static void main(String[] args) {

        String hostName = "localhost";
        int portNumber = 9090;
        // JDK 1.8, Used try with resource so we do not have to close any open stream
        try (Socket socket = new Socket(hostName, portNumber);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                     System.in));

        ) {
            System.out.println("Please enter your name : ");
            String name = stdIn.readLine();
            out.println(name);
            System.out.println("Please enter your key (Should be minimum 8 character long) to receive message : ");
            String key = stdIn.readLine();
            out.println(key);
            SecretKey secretKey = calculateSectretKey(key);
            new Reader(in, secretKey).start();
            DESAlgoClient desAlgoClient = new DESAlgoClient();
            String inputLine = null;
            while ((inputLine = stdIn.readLine()) != null) {
                String encryptedMessage = desAlgoClient.encryptMessage(name + " : " + inputLine, secretKey);
                out.println(encryptedMessage);
            }

        } catch (IOException e) {
            System.err.println(e);
        }

    }

    private static SecretKey calculateSectretKey(String key) {
        DESAlgoClient desAlgoClient = new DESAlgoClient();
        return desAlgoClient.getSecretKey(key);
    }
}


class Reader extends Thread {

    BufferedReader in;
    SecretKey secretKey;

    public Reader(BufferedReader in, SecretKey secretKey) {
        this.in = in;
        this.secretKey = secretKey;
    }

    public void run() {
        try {
            DESAlgoClient desAlgoClient = new DESAlgoClient();
            String inputMessage = null;
            while ((inputMessage = in.readLine()) != null) {
                String decryptMessage = desAlgoClient.decryptMessage(inputMessage, secretKey);
                System.out.println(decryptMessage);
            }
        } catch (Exception e) {
            System.err.println("Error Occurred in reader.");
        }

    }
}
