package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server started");
        Socket socket = serverSocket.accept();
        System.out.println("connexion d'un client : " + socket.getRemoteSocketAddress());
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        System.out.println("J' attend que le client envoie un octet");
        int nb = inputStream.read(); // Lire un octet
        System.out.println("J' ai rescu un nombre  "+nb);
        int res = nb *5;
        System.out.println("J' envoie la réponse de " + res);

        outputStream.write(res);
        socket.close();
    }
}
