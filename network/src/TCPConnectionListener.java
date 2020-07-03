public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection); //- соединение готово.
    void onString(TCPConnection tcpConnection, String s); //-соединение принимает строчку, добавим в качестве аргумента саму строчку.
    void onException(TCPConnection tcpConnection, Exception e); //- соединение вызвало исключение, для обработчика добавим саму ошибку.
    void onDisconnect(TCPConnection tcpConnection); //- соединение прервано.

}
