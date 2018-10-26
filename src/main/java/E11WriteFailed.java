import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final class E11WriteFailed {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket acceptSock = new ServerSocket(1234);

        Socket clientSideSock = new Socket("127.0.0.1", 1234);

        Socket serverSideSock = acceptSock.accept();

        // Close acceptSock only, won't affect serverSideSock, can be used for graceful shutdown.
        acceptSock.close();

        clientSideSock.getOutputStream().write("Hello!".getBytes());
        System.out.println("First attempt succeeded");

        // Send RST to the client
        serverSideSock.close();
        System.out.println("Server closed socket");

        try {
            clientSideSock.getOutputStream().write("Hello?".getBytes());
            System.out.println("Second attempt succeeded");
        } catch (IOException e) {
            System.out.println("Client got:");
            e.printStackTrace(System.out);
        }
    }

}
