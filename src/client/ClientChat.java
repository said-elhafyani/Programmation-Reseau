package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientChat extends Application {
    private PrintWriter printWriter;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
          primaryStage.setTitle("Client Chat");
          BorderPane borderpane = new BorderPane();
          Label labelhost = new Label("Host:");
          TextField textFieldhost = new TextField("localhost");
          Label labelport = new Label("Port:");
          TextField textFieldport = new TextField("1234");
          Button buttonConnecter = new Button("Connecter");

          HBox hbox = new HBox();
          hbox.setSpacing(10);
          hbox.setPadding(new Insets(10, 10, 10, 10));
          hbox.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
          hbox.getChildren().addAll(labelhost, textFieldhost, labelport, textFieldport, buttonConnecter);
          borderpane.setTop(hbox);
          VBox vbox = new VBox();
          vbox.setSpacing(10);
          vbox.setPadding(new Insets(10, 10, 10, 10));
          ObservableList<String> listModel = FXCollections.observableArrayList();
          ListView<String> listview = new ListView<>(listModel);
          vbox.getChildren().add(listview);
          borderpane.setCenter(vbox);

          Label labelmessage = new Label("Message:");
          TextField textFieldmessage = new TextField();
          textFieldmessage.setPrefSize(350,30);
          Button buttonEnvoyer = new Button("Envoyer");
          HBox hbox1 = new HBox();
          hbox1.setSpacing(10);
          hbox1.setPadding(new Insets(10, 10, 10, 10));
          hbox1.getChildren().addAll(labelmessage, textFieldmessage, buttonEnvoyer);
          borderpane.setBottom(hbox1);
          Scene scene = new Scene(borderpane,500,400);
          primaryStage.setScene(scene);
          primaryStage.show();

          buttonConnecter.setOnAction((event)->{
            String host = textFieldhost.getText();
            int port = Integer.parseInt(textFieldport.getText());
              try {
                  Socket socket = new Socket(host,port);
                  InputStream inputStream = socket.getInputStream();
                  OutputStream outputStream = socket.getOutputStream();
                  InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                  BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                  printWriter = new PrintWriter(outputStream,true);
                  new Thread(()->{

                      while(true){

                          try {
                             String response = bufferedReader.readLine();
                              Platform.runLater(()->{

                                  listModel.add(response);
                              });
                          } catch (IOException e) {
                              throw new RuntimeException(e);
                          }



                      }
                  }).start();
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
          });

          buttonEnvoyer.setOnAction((event)->{
              String message = textFieldmessage.getText();
              printWriter.println(message);
          });
    }
}
