package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MockMessageServer implements Runnable {
    private List<String> serverMessages;
    private volatile boolean running = true;

    @Override
    public void run() {
        ServerSocket socket;
        Socket clientSocket;

        try {
            //Create the ServerSocket on the local machine
            socket = new ServerSocket(10220);

            //Infinitely accept new client connections and spin off their
            // handling into new threads
            while (running) {
                clientSocket = socket.accept();
                Thread t = new Thread(new MockMessageHandler(clientSocket,
                  serverMessages));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("MockMessageServer failed");
        }
    }

    public void stopRunning() throws IOException {
        //Trip the run flag to stop the echo server and connect a dummy
        // socket to it to boot it out of a blocked state in accept()
        running = false;
        Socket dummy = new Socket(InetAddress.getLocalHost().getHostAddress()
          , 10219);
        dummy.close();
    }

    public void loadMessages(List<String> messages) {
        serverMessages = messages;
    }

    class MockMessageHandler implements Runnable {
        Socket clientSocket;
        BufferedReader inputStream;
        PrintWriter outputStream;
        List<String> messages;

        private MockMessageHandler(Socket client, List<String> messList)
          throws IOException {
            messages = messList;
            clientSocket = client;
            inputStream = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintWriter(
              clientSocket.getOutputStream(), true);
        }
        @Override
        public void run() {
            try {
                //Send out all the messages in the message list
                for (String m : messages) {
                    outputStream.println(m);
                }

                //Close all of the resources for the client connection
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("A MockMessageHandler failed");
            }
        }
    }
}
