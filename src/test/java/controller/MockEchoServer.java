package controller;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A mock Server that can be created and run in a separate thread on the
 * local machine. It accepts a connection from the Client during testing and
 * echos back whatever it receives.
 */
public class MockEchoServer implements Runnable {
    private volatile boolean running = true;

    @Override
    public void run() {
        ServerSocket socket;
        Socket clientSocket;

        try {
            //Create the ServerSocket on the local machine
            socket = new ServerSocket(10219);

            //Infinitely accept new client connections and spin off their
            // handling into new threads
            while (running) {
                clientSocket = socket.accept();
                Thread t = new Thread(new MockEchoHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            System.out.println("MockEchoServer failed");
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

    class MockEchoHandler implements Runnable {
        Socket clientSocket;
        BufferedReader inputStream;
        PrintWriter outputStream;
        String line;

        private MockEchoHandler(Socket client) throws IOException {
            clientSocket = client;
            inputStream = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            outputStream = new PrintWriter(
              clientSocket.getOutputStream(), true);
        }
        @Override
        public void run() {
            try {
                //Echo back anything received from the Client until the
                // connection is killed
                while ((line = inputStream.readLine()) != null) {
                    outputStream.println(line);
                }

                //Close all of the resources for the client connection
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("A MockEchoHandler failed");
            }
        }
    }
}
