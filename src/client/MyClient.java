package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) throws IOException {
        System.out.println("Je me connect qu serveur");
        Socket socket = new Socket("localhost",1234);
        System.out.println("Je me connect qu serveur11");
        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez entrer un nombre");
        int input = sc.nextInt();
        System.out.println("J' envoie le nomre "+input+" au serveur");
        os.write(input);
        System.out.println("J' attend la réponse du serveur ...");
        int response = is.read();
        System.out.println("Reponse du serveur est : "+response);


    }
}
