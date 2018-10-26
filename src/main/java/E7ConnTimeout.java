import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

final class E7ConnTimeout {

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("100.99.28.0", 1234);
        Socket socket = new Socket();
        socket.connect(addr, 2000);
    }

}
