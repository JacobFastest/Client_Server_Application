package http;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The below class acts as a server for a user to fetch an html file from
 * 
 * @author Jacob Fast
 * @version 1.0 2024-03-13
 */
public class httpServer {
    public static void main(String[] args) throws IOException {
        // Check if the user input the correct number of arguments, if not exit
        if (args.length != 1) {
            System.err.println("Usage: java <port number>");
            System.exit(1);
        }

        // Store the portNumber in a local variable
        int portNumber = Integer.parseInt(args[0]);
        // Int to remember the number of connections served
        int numConnections = 0;

        // Below loop creates a server socket, with the given port number, and
        // waits for a user to connect to it
        // Once a user connects they can attempt to fetch a file
        while (true) {
            // Print out the number of connections served
            System.out.println(numConnections
                + " connections served. Accepting new client...");

            // The below try with attempts to create a new server socket from
            // the given port number.
            // It also create a PrintWriter and BufferedReader for writing and
            // reading to the socket.
            try (ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket
                    .getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));) {

                // Use for storing the message sent by the client
                String inputLine;
                inputLine = in.readLine();

                // Split up input string into parts based on where the spaces
                // occur
                // Used to check if the Client formated the request correctly
                String[] arr = inputLine.split("\\s{1,}");
                // If the client formatted the request correctly check to see if
                // the server has the file
                if (arr.length == 3 && arr[0].equals("GET") && arr[2].equals(
                    "HTTP/1.0")) {
                    String workingDirectory = System.getProperty("user.dir"); // Gets
                                                                              // path
                                                                              // for
                                                                              // file
                    // Created a file instance based on the Clients request and
                    // the directory that server is in
                    File f = new File(workingDirectory, arr[1]);
                    // If the file exists and is not a directory send to the
                    // Client
                    // If not tell the
                    if (f.exists() && !f.isDirectory()) {
                        // Create a scanner to read the file
                        Scanner fileReader = new Scanner(f);
                        // Tell the Client we found the file
                        out.println("HTTP/1.0 200 OK");
                        // Get the file length and send it to the Client
                        long fileLength = f.length();
                        out.println("Content-Length: " + Long.toString(
                            fileLength));
                        out.println(System.lineSeparator());

                        // While the are still lines in the file read them and
                        // send them to the Client
                        while (fileReader.hasNextLine()) {
                            out.println(fileReader.nextLine());
                        }
                        // Close the scanner
                        fileReader.close();
                    }
                    // Tells the Client that the file could not be found
                    else {
                        out.println("HTTP/1.0 404 Not Found");
                    }
                }
                // If the client didn't format the request correctly tell them
                else {
                    out.println("HTTP/1.0 400 Bad Request");
                }

            }
            // Catch the case in which a port is already in use
            catch (IOException e) {
                System.out.println(
                    "Exception caught when trying to listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
            numConnections++; // After the end of the connection increase the
                              // number of connections
        }
    }
}
