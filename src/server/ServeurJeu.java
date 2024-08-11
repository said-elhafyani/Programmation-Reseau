package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeu extends Thread {
    private int nombreClient=0;
    private boolean isAlive=true;
    private int nombreSecret;
    private String gagnant;
    private boolean fin;

    public static void main(String[] args) {
        new ServeurJeu().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            nombreSecret = new Random().nextInt(1000);
            System.out.println("le nombre secret est : "+nombreSecret);
            System.out.println("Démarrage de Serveur");
            while (isAlive) {
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
                pw.println("Devinez le nombre secret ......? ");

                while (true){
                    String line = br.readLine();
                    int nombre=0;
                    boolean correctFormatRequest = false;
                    try{
                        nombre = Integer.parseInt(line);
                        correctFormatRequest = true;
                    }catch (NumberFormatException e){
                        correctFormatRequest = false;
                    }

                    System.out.println("Client " + IP+" Tentative avec le nombre : "+nombre);
                 if(correctFormatRequest){
                     if(fin == false){
                         if(nombre > nombreSecret){
                             pw.println("votre nombre est superieur au nombre secret");
                         }else if(nombre < nombreSecret){
                             pw.println("votre nombre est inferieur au nombre secret");
                         }else{
                             pw.println("BRAVO, voud avez gagné");
                             gagnant = IP;
                             System.out.println("BRAVO au gagnant, IP client : "+IP);
                             fin = true;
                         }
                     }else{
                         pw.println("Jeu termine, le gagnant est : "+gagnant);
                     }
                 }else{
                     pw.println("Format de nombre est incorrect");
                 }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}



