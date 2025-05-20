package utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtils {
    public static boolean isInternetAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500); // Google's DNS, 1.5s timeout
            return true;
        } catch (IOException e) {
            return false;
        }
    }
} 