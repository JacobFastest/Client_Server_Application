package http;

import java.io.*;
import java.net.*;

/**
 * The below class has the user input the hostname and port number they are
 * trying to connect to.
 * Once they connect they can they fetch a html file from the Server.
 * After the client receives the file it ends the connection to the server.
 * 
 * @author Jacob Fast
 * @version 1.0 2024-03-13
 */
public class httpClient {

    public static void main(String[] args) throws IOException {
        // Check if the user input the correct number of arguments, if not exit
        if (args.length != 2) {
            System.err.println(
                "Usage: java httpClient <host name/IPAddress> <port number>");
            System.exit(1);
        }

        // Store the hostName and port number into local variables
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // The below try with, attempts to create a new socket from the given
        // hostName and portnumber
        // It also makes Printwriter to write to the socket and BufferedReader
        // to read from the socket
        // Similarly it makes a BufferedReader on the System's input which
        // allows
        // the user to input the name of the file
        try (Socket httpSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(httpSocket.getOutputStream(),
                true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                httpSocket.getInputStream()));) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                System.in));

            // Below string is used to store what the server sends
            String fromServer;

            // Ask the user for the name of the file
            System.out.println("Enter the name of the html file:");
            String html = stdIn.readLine(); // Get the input

            // Add correct arguments that the server is expectings
            String get = "GET " + "/" + html + " HTTP/1.0";
            System.out.println("Sent out the request: "); // This is used to show what the Client is sending to the server
            System.out.println(get);
            
            out.println(get); // Send the message to the server

            // The below while loops reads the response from the server and
            // based on the response from the server does the correct action
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Received response from server:");  
                System.out.println(fromServer); // Used to tell the user what the server has sent back
                
                // If the response is Bad Request tell the user that their input
                // file name was improperly formatted
                if (fromServer.equals("HTTP/1.0 400 Bad Request")) {
                    System.out.println(
                        "The html request was improperly formatted.");
                    break;
                }
                // If the response is 404 tell the user that the inputed file
                // could not be found
                else if (fromServer.equals("HTTP/1.0 404 Not Found")) {
                    System.out.println("The file could not be found on the server!");
                    break;
                }
                // If the server found the file either create a new file with
                // the same information or if the file already exists on the
                // user's machine do nothing
                else if (fromServer.equals("HTTP/1.0 200 OK")) {
                    // Below loop prints out the content length and the empty line which should not be apart of the fiel
                    for (int i = 0; i < 2; i++) {
                        fromServer = in.readLine();
                        System.out.println(fromServer);
                    }

                    // Make an instance of the file with the given name 
                    File file = new File(html);
                    // Check if the file already exists on the user's machine in the directory they are in. 
                    // If it does do nothing
                    if (!file.isFile()) {
                        // Create the new file
                        file.createNewFile();
                        // Make a FileWrite to write to the new file
                        FileWriter fr = new FileWriter(file, true);
                        // Read the lines from the server and add them to the file
                        while ((fromServer = in.readLine()) != null) {
                            fr.write(fromServer + System.lineSeparator());
                        }
                        // Close the file writer 
                        fr.close();
                    }
                    else {
                        System.out.println("A file with the same name already exists in the current directory.");
                        System.out.println("Double check the name of the file you want to copy.");
                        break;
                    }
                }
            }

            System.out.println("Closing I/O and quitting..."); // Tell the user
                                                               // that the
                                                               // session is
                                                               // over
        }
        // Catch possible exceptions thrown by try block
        // This exception occurs when the code can't find the inputed hostname
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        // This exception occurs when the I/O from the socket is suddenly
        // disconnected
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                + hostName);
            System.exit(1);
        }

    }
}
