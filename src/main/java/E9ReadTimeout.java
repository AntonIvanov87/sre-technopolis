import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final class E9ReadTimeout {

    public static void main(String[] args) throws IOException {
        ServerSocket acceptSock = new ServerSocket(1234);

        Socket clientSideSock = new Socket("127.0.0.1", 1234);
        clientSideSock.setSoTimeout(1000);

        try {
            clientSideSock.getInputStream().read();
        } catch (IOException e) {
            System.err.println("Client socket got:");
            e.printStackTrace();
        }

        Socket serverSideSock = acceptSock.accept();
        serverSideSock.setSoTimeout(1000);
        try {
            serverSideSock.getInputStream().read();
        } catch (IOException e) {
            System.err.println("Server socket got:");
            e.printStackTrace();
        }
    }

}
