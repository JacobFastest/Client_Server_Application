package http;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class httpServer {
    public static void main(String[] args)
        throws IOException,
        InterruptedException {

        if (args.length != 1) {
            System.err.println("Usage: java <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        int numConnections = 0;

        while (true) {
            System.out.println(numConnections + " connections served. Accepting new client...");
            
            try (ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket
                    .getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));) {

                String inputLine, outputLine;

                inputLine = in.readLine();
                // Use delimiting method used in class to break up input string
                String[] arr = inputLine.split("\\s{1,}");
                if (arr.length == 3 && arr[0].equals("GET") && arr[2].equals(
                    "HTTP/1.0")) {
                    // Check if file is on computer if not throw 404
                    // Might need to add path to arr[1]
                    String workingDirectory = System.getProperty("user.dir");
                    File f = new File(workingDirectory, arr[1]);
                    if (f.exists() && !f.isDirectory()) {
                        Scanner fileReader = new Scanner(f);
                        out.println("HTTP/1.0 200 OK");
                        long fileLength = f.length();
                        out.println("Content-Length: " + Long.toString(
                            fileLength));
                        out.println(System.lineSeparator());

                        while (fileReader.hasNextLine()) {
                            out.println(fileReader.nextLine());
                        }
                        fileReader.close();
                    }
                    else {
                        out.println("HTTP/1.0 404 Not Found");
                    }
                }
                else {
                    out.println("HTTP/1.0 400 Bad Request");
                }

            }
            catch (IOException e) {
                System.out.println(
                    "Exception caught when trying to listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
            numConnections++;
        }
    }
}
