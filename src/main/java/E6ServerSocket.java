import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final class E6ServerSocket {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234, 1);
        while (true) {
            Socket socket = serverSocket.accept();
            byte[] bytes = socket.getInputStream().readAllBytes();
            socket.getOutputStream().write(bytes);
            socket.close();
        }

    }
}
