import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final InetSocketAddress forwardAddress = retrieveForwardAddress(args);
        final int UDP_PORT = 2053;
        try (
            final DatagramSocket serverSocket = new DatagramSocket(UDP_PORT)
        ) {
            serverSocket.setReuseAddress(true);
            while(true) {
                final byte[] buf = new byte[512];
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                serverSocket.receive(packet);

                final DNSMessage questionMessage = DNSMessageDecoder.decode(buf);
                System.out.println("OriginalRequest(" + packet.getSocketAddress() + ") : " + questionMessage);

                final DNSResponseRetriever responseRetriever = createDNSResponseRetriever(forwardAddress, serverSocket);
                final DNSMessage responseMessage = responseRetriever.getResponseMessage(questionMessage);
                System.out.println("FinalResponse(" + packet.getSocketAddress() + ") : " + responseMessage);

                byte[] bufResponse = DNSMessageEncoder.encode(responseMessage);
                DatagramPacket responsePacket = new DatagramPacket(bufResponse, bufResponse.length, packet.getSocketAddress());
                serverSocket.send(responsePacket);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static InetSocketAddress retrieveForwardAddress(String[] args) {
        Map<String, String> arguments = parseArgs(args);
        System.out.println("Arguments = " + arguments);
        if (arguments.containsKey("--resolver")) {
            String[] resolverAddress = arguments.get("--resolver").split(":");
            return new InetSocketAddress(
                resolverAddress[0],
                Integer.parseInt(resolverAddress[1])
            );
        }
        return null;
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                arguments.put(args[i], args[i + 1]);
                i++;
            } else {
                arguments.put(args[i], "");
            }
        }
        return arguments;
    }

    private static DNSResponseRetriever createDNSResponseRetriever(InetSocketAddress forwardAddress, DatagramSocket serverSocket) {
        if (forwardAddress != null) {
            return new ForwardedDNSResponse(serverSocket, forwardAddress);
        } else {
            return new SimpleDNSResponse();
        }
    }
}