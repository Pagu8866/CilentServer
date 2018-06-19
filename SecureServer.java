

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecureServer {

    // This will store all clients.
  HashMap<String, List<Client>> clients = new HashMap<>();


    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int portNumber = 9090; 
        try {
            DESAlgoClient desAlgoClient = new DESAlgoClient();
            SecureServer secureServer = new SecureServer();
            serverSocket = new ServerSocket(portNumber);
            boolean listening = true;
            while (listening) {
                // LListening to Connection
                Socket clientSocket = serverSocket.accept();

                // Create new Thread to serve different client.
                ClientThread request = new ClientThread(clientSocket, secureServer, desAlgoClient);

                //secureServer.clients.put(thread.getId(), new ArrayList<String>());
                // Start the thread.
                request.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    // This method will remove the client from the list..
    public synchronized void removeClient(Client client, String key) {
        if (this.clients.containsKey(key)) {
            this.clients.get(key).remove(client);
        }
    }

    // This method will add new client based on the key he/she entered..
    public synchronized void addClient(Client client, String key) {
        if (this.clients.containsKey(key)) {
            this.clients.get(key).add(client);
        } else {
            List<Client> clients = new ArrayList<>();
            clients.add(client);
            this.clients.put(key, clients);
        }

    }

    // This method will get group of people with same key and broadcast the message...
    public synchronized void broadCastMessage(String message, Client currentClient) {
        List<Client> clients = this.clients.get(currentClient.getKey());
        if (clients != null) {
            for (Client client : clients) {
                if (client.getId() != currentClient.getId()) {
                    client.getOut().println(message);
                }
            }
        }
    }


}

class ClientThread extends Thread {

    private Socket socket;
    private SecureServer secureServer;
    private DESAlgoClient desAlgoClient;

    public ClientThread(Socket socket, SecureServer secureServer, DESAlgoClient desAlgoClient) {
        this.secureServer = secureServer;
        this.socket = socket;
        this.desAlgoClient = desAlgoClient;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            Client newClient = getClientInfo(in, out);
            String inputMessage;
            while ((inputMessage = in.readLine()) != null) {

                if ("END".equalsIgnoreCase(inputMessage)) {
                    processEndRequest(out, newClient);
                    break;
                }
                // This will read all message which is stored
                System.out.println("Message Received form " + newClient.getName() + " : " + inputMessage);
                // Broadcast to other clients
                secureServer.broadCastMessage(inputMessage, newClient);
            }
            socket.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    private void processEndRequest(PrintWriter out, Client client) {
        this.secureServer.removeClient(client, client.getKey());
    }

    // This will calculate DES Secret key based on the key provided by client..
    public SecretKey calculateSecretKey(BufferedReader in, DESAlgoClient desAlgoClient) throws IOException {
        String key = null;
        while ((key = in.readLine()) != null) {
            return desAlgoClient.getSecretKey(key);
        }
        return null;
    }

    // This method will get name and key from the client..
    public Client getClientInfo(BufferedReader in, PrintWriter out) throws IOException {
        String clientName = null;
        Client newClient = null;
        while ((clientName = in.readLine()) != null) {
            System.out.println(clientName + " is Connected.");
            break;
        }
        String key = null;
        while ((key = in.readLine()) != null) {
            newClient = new Client(Thread.currentThread().getId(), clientName, out, key);
            secureServer.addClient(newClient, key);
            break;
        }
        return newClient;
    }

}

class Client {
    private long id;
    private String name;
    private PrintWriter out;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public Client(long id, String name, PrintWriter out, String key) {
        this.id = id;
        this.name = name;
        this.out = out;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}