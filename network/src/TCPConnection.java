import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;     //сокет для TCP соединения.
    private final Thread thread;     //поток который будет слушать соединение.
    private final BufferedReader in; //для чтения потока.
    private final BufferedWriter out;//для записи в поток
    private final TCPConnectionListener tcpConnectionListener;

    public TCPConnection(Socket socket, TCPConnectionListener tcpConnectionListener) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.tcpConnectionListener = tcpConnectionListener;

        //thread = new Thread() ;
        //thread.start();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TCPConnection.this.tcpConnectionListener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()) {
                        String message = in.readLine();
                        TCPConnection.this.tcpConnectionListener.onString(TCPConnection.this, message);
                    }
                } catch (IOException e) {
                    TCPConnection.this.tcpConnectionListener.onException(TCPConnection.this, e);
                }
                finally {
                    TCPConnection.this.tcpConnectionListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        thread.start();
        //String message = in.readLine();
        try {
            String message = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TCPConnection(TCPConnectionListener tcpConnectionListener, String ip, int port) throws IOException{
        this(new Socket(ip, port), tcpConnectionListener);
    }

    public synchronized void sendMessage(String value) {
        try{
            out.write(value+"\r\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void disconnect() {
        thread.interrupt();
        try{
            socket.close();
        } catch (IOException e) {
            tcpConnectionListener.onException(TCPConnection.this, e);
        }
    }
    @Override
    public String toString() {
        return "Message: "+socket.getInetAddress()+": "+socket.getPort();
    }

}
