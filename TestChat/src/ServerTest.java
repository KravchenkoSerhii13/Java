import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerTest {
    private static Set<String> names = new HashSet<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(59001);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectInputStream inputStream;
        private final ObjectOutputStream outputStream;
        private String name;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
//                    outputStream.writeObject("Submit Name: ");
                    name = (String) inputStream.readObject();
                    if (name == null) return;
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }
                System.out.println("MESSAGE: " + name + " has joined");
                outputStream.writeObject("MESSAGE: " + name + " has joined");

                while (true) {
                    String message = (String) inputStream.readObject();
                    System.out.println("Received from " + name + ": " + message);
                    outputStream.writeObject(name + ": " + message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) { }
            }
        }
    }
}