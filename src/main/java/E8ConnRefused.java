import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

final class E8ConnRefused {

    public static void main(String[] args) throws IOException {
        new Socket("127.0.0.1", 1234);
    }

}
