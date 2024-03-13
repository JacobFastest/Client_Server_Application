/**
 * 
 */
package http;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * 
 */
public class httpClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println(
                "Usage: java httpClient <host name/IPAddress> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (Socket httpSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(httpSocket.getOutputStream(),
                true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                httpSocket.getInputStream()));) {

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(
                System.in));
            String fromUser;
            String fromServer;

            Scanner s = new Scanner(System.in);
            System.out.println("Enter the name of the html file:");
            String html = s.nextLine();
            String get = "GET " + "/" + html + " HTTP/1.0";
            System.out.println("Client: " + get);
            out.println(get);

            boolean fileFound = false;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("HTTP/1.0 400 Bad Request")) {
                    System.out.println(
                        "The html request was improperly formatted");
                    break;
                }
                else if (fromServer.equals("HTTP/1.0 404 Not Found")) {
                    System.out.println("The file could not be found");
                    break;
                }
                else if (fromServer.equals("HTTP/1.0 200 OK")) {
                    int linen = 0;
                    while (linen < 2) {
                        fromServer = in.readLine();
                        System.out.println("server: " + fromServer);
                        linen++;
                    }

                    File file = new File(html);
                    if (file.isFile()) {
                        break;
                    }
                    file.createNewFile();
                    FileWriter fr = new FileWriter(file, true);
                    // should check if file already exists

                    while ((fromServer = in.readLine()) != null) {
                        fr.write(fromServer + System.lineSeparator());
                    }

                    fr.close();
                }
            }
            System.out.println("Closing I/O and quitting...");
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                + hostName);
            System.exit(1);
        }

    }
}
