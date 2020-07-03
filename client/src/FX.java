import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.control.TextArea;

import javafx.geometry.Orientation;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FX extends Application implements TCPConnectionListener {

    private static final String IP_ADDRES = "127.0.0.1";
    private static final int  PORT = 5000;
    private final TCPConnection tcpConnection;
    private final TextArea textArea = new TextArea();
    private final TextField textField = new TextField();

    private final TextField nick = new TextField("Введите ник");

    public FX() throws IOException {
        tcpConnection = new TCPConnection(this, IP_ADDRES, PORT);
    }

    public static void main(String[] args) {
        launch(args);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new FX();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {

        textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        Send();
                    }
                }
        );

        textArea.setPrefColumnCount(45);
        textArea.setPrefRowCount(26);
        textField.setPrefColumnCount(43);
        nick.setPrefColumnCount(45);
        FlowPane root = new FlowPane(Orientation.HORIZONTAL, 5, 5, textField, textArea, nick);
        Scene scene = new Scene(root, 500, 500);

        stage.setScene(scene);
        stage.setTitle("Chat in JavaFX");
        stage.show();

    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Соединение готово...");
    }

    @Override
    public void onString(TCPConnection tcpConnection, String s) {
        printMessage(s);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Ошибка соединения "+e);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Соединение закрыто");
    }

    private synchronized void printMessage(String s) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(s + "\n");
                //textArea.(textArea.().getLength());
            }
        });
    }

    public void Send() {
        String s = textField.getText();   // из поля для ввода получаем текст
        if (s.equals("")) return;       //если поле пустое то прекращаем метод
        textField.setText(null);          // очищаем поле
        tcpConnection.sendMessage(nick.getText()+": "+s); //передаем для отправки все клиентам.
    }
}