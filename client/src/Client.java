import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDRES = "127.0.0.1"; //для ip
    private static final int  PORT = 5000;               //для номера порта
    private static final int WIDTH= 600;                 //ширина окна
    private static final int HEIGHT= 400;                //высота окна
    private final JTextArea jTextArea = new JTextArea();
    private final JTextField nick = new JTextField("Введите ник...");
    private final JTextField message = new JTextField();
    private final TCPConnection tcpConnection;

    public Client() throws IOException {
        message.addActionListener(this);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        add(jTextArea, BorderLayout.CENTER);
        add(nick, BorderLayout.SOUTH);
        add(message, BorderLayout.NORTH);
        setVisible(true);
        tcpConnection = new TCPConnection(this, IP_ADDRES, PORT);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Client();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String s = message.getText();   // из поля для ввода получаем текст
        if (s.equals("")) return;       //если поле пустое то прекращаем метод
        message.setText(null);          // очищаем поле
        tcpConnection.sendMessage(nick.getText()+": "+s); //передаем для отправки все клиентам.

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
                jTextArea.append(s+"\n");
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            }
        });
    }
}
