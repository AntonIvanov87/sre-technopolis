import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

final class E11WriteFailedChannel {

    public static void main(String[] args) throws IOException {
        InetSocketAddress sockAddr = new InetSocketAddress(1234);

        ServerSocketChannel acceptChan = ServerSocketChannel.open();
        acceptChan.bind(sockAddr);

        SocketChannel clientSideChan = SocketChannel.open(sockAddr);

        SocketChannel serverSideChan = acceptChan.accept();

        // Close acceptSock only, won't affect serverSideChan, can be used for graceful shutdown.
        acceptChan.close();

        clientSideChan.write(ByteBuffer.wrap("Hello!".getBytes()));
        System.out.println("First attempt succeeded");

        // Send RST to the client
        serverSideChan.close();
        System.out.println("Server closed socket");

        try {
            clientSideChan.write(ByteBuffer.wrap("Hello?".getBytes()));
            System.out.println("Second attempt succeeded");
        } catch (IOException e) {
            System.out.println("Client got:");
            e.printStackTrace(System.out);
        }
    }

}
