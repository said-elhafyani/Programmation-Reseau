package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread {
    private int nombreClient;
     public static void main(String[] args) {
       new ServeurMT().start();
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Démarrage de Serveur");
            while (true) {
                Socket socket = serverSocket.accept();
                ++nombreClient;
                 new Convertation(socket,nombreClient).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Convertation extends Thread {
        private Socket socket;
        private int numeroClient;
        public Convertation(Socket socket, int nombreClient) {
            this.socket = socket;
            this.numeroClient = nombreClient;
        }
        @Override
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(isr);

                OutputStream output = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(output, true);
                String IP = socket.getRemoteSocketAddress().toString();
                System.out.println("Connexion du Client numéro : " + numeroClient+" IP="+IP);
                pw.println("Bien venue vous etes le client numéro : "+numeroClient);
                while (true){
                    String line = br.readLine();
                    System.out.println("Le client "+IP+" a envoyé une requete "+line);
                    pw.println(line.length());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}



