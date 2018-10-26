import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final class E10ReadOfClosed {

    public static void main(String[] args) throws IOException {
        ServerSocket acceptSock = new ServerSocket(1234);

        Socket clientSideSock = new Socket("127.0.0.1", 1234);
        clientSideSock.setSoTimeout(1000);

        Socket serverSideSock = acceptSock.accept();
        serverSideSock.close();
        System.out.println("Server closed socket");
        System.out.println("Client read " + clientSideSock.getInputStream().read());
        clientSideSock.close();

        clientSideSock = new Socket("127.0.0.1", 1234);

        serverSideSock = acceptSock.accept();

        clientSideSock.close();
        System.out.println("Client closed socket");
        System.out.println("Server read "+ serverSideSock.getInputStream().read());

        serverSideSock.close();
    }

}
