import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class Server implements TCPConnectionListener{

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private Server(){
        System.out.println("Сервер запущен....");
        try (ServerSocket serverSocket = new ServerSocket(5000)){
            while(true) {
                try{
                    new TCPConnection(serverSocket.accept(), this);
                }catch (IOException e){
                    System.out.println("Ошибка соединения: "+ e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void sendAllConnect(String s){
        System.out.println("Разослонное всем клиентам сообщение: " + s);
        final int size = connections.size();
        for (int i = 0; i<size;i++) connections.get(i).sendMessage(s);
        write(s, true);
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAllConnect("Подключенный клиент" + tcpConnection);
    }

    @Override
    public synchronized void onString (TCPConnection tcpConnection, String s) {
        sendAllConnect(s);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Произошла ошибка в соединении: " + e);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAllConnect("Клиент отключился" + tcpConnection);
    }

    public static void main(String[] args){
        write("", false);
        new Server();
    }

    public static void write(String log,Boolean l){
        try(FileWriter writer = new FileWriter("log.txt", l))
        {
            // запись всей строки
            writer.append(log + '\n');
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }


}
