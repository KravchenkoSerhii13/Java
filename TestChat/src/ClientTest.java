import javax.security.auth.login.CredentialException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
    static ObjectOutputStream outputStream;
    static ObjectInputStream inputStream;
    static JTextArea textArea;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("25.57.221.253", 59001);
            System.out.println("Connected to server.");

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            InitiatingProgram();
            CreatingWindow();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String serverResponse = (String) inputStream.readObject();
                textArea.append(serverResponse + "\n");
                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void InitiatingProgram() throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("Enter a name");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(200, 150);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter Name:");
        JTextField textField = new JTextField(10);
        JButton submit = new JButton("Submit");

        submit.addActionListener(e -> {
            String message = textField.getText();
            try {
                outputStream.writeObject(message);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(label);
        panel.add(textField);
        panel.add(submit);
        frame.add(BorderLayout.CENTER, panel);
        frame.setVisible(true);
    }

    static void CreatingWindow(){
        JFrame frame = new JFrame("Chat Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter Text");
        JTextField textField = new JTextField(10);
        JButton send = new JButton("Send");

        send.addActionListener(e -> {
            String message = textField.getText();
            textField.setText("");
            try {
                if(!message.isEmpty()) outputStream.writeObject(message);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        panel.add(label);
        panel.add(textField);
        panel.add(send);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        frame.getContentPane().add(BorderLayout.SOUTH, panel);
        frame.getContentPane().add(scrollPane);
        frame.setVisible(true);
    }
}