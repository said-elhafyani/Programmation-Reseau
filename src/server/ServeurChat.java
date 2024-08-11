package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread {
    private int nombreClient;
    private List<Convertation> conversations = new ArrayList<Convertation>();
     public static void main(String[] args) {
       new ServeurChat().start();
    }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Démarrage de Serveur");
            while (true) {
                Socket socket = serverSocket.accept();
                ++nombreClient;
                Convertation convertation = new Convertation(socket,nombreClient);
                conversations.add(convertation);
                convertation.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Convertation extends Thread {
        protected Socket socket;
        protected int numeroClient;
        public Convertation(Socket socket, int nombreClient) {
            this.socket = socket;
            this.numeroClient = nombreClient;
        }

        void broadcastMessage(String message,Socket socketClient,int numeroClientC)  {
            try{
                for (Convertation conversation : conversations) {
                    if(conversation.socket != socketClient) {
                        if(conversation.numeroClient == numeroClientC || numeroClientC == -1) {
                            PrintWriter printWriter = new PrintWriter(conversation.socket.getOutputStream(),true);
                            printWriter.println(message);
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
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
                    if(line.contains("=>")){
                        String[] requestParams = line.split("=>");
                        if(requestParams.length == 2){
                            String message = requestParams[1];
                            int numeroClientC = Integer.parseInt(requestParams[0]);
                            broadcastMessage(message,socket,numeroClientC);
                        }else{
                            broadcastMessage(line,socket,-1);
                        }
                    }else{
                        broadcastMessage(line,socket,-1);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}



