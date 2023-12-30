import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.nio.ByteBuffer;

import java.util.List;
import java.util.Arrays;

public class Main {
  public static void main(String[] args){
    final int PORT_UDP = 2053;
    final short SHORT_ZERO = (short)0;
    final byte BYTE_ZERO = (byte)0;

    try(
      DatagramSocket serverSocket = new DatagramSocket(PORT_UDP)
    ) {
      while (true) {
        final byte[] buf = new byte[512]; // maximum 512 byte for a packet

        final DatagramPacket packet = new DatagramPacket(buf, buf.length);

        serverSocket.receive(packet);

        System.out.println("Received data " + packet);

        DNSHeader header = new DNSHeader();

        header.setID((short)1234);
        header.setQR(true);
        header.setQDCOUNT((short)1);

        DNSQuestion questions = new DNSQuestion(
          "codecrafters.io", DNSQuestionType.A.getValue(),
          DNSQuestionClass.IN.getValue()
        );

        DNSMessage message = new DNSMessage(header, questions);

        final byte[] bufResponse = message.toBytes();

        DatagramPacket responsePacket = new DatagramPacket(
          bufResponse, bufResponse.length,
          packet.getAddress(), packet.getPort()
        );

        serverSocket.send(responsePacket);

        System.out.println("Sent data " + responsePacket);

        // final var header = new DNSHeader(
        //   (short)1234, true, BYTE_ZERO, false, false, false, false, BYTE_ZERO,
        //   RCode.NO_ERROR, SHORT_ZERO, SHORT_ZERO, SHORT_ZERO, SHORT_ZERO
        // );

        // final var questions = new DNSQuestion("codecrafters.io",
        //     DNSQuestionType.A.getValue(), DNSQuestionClass.IN.getValue());

        // final var message = new DNSMessage(header.get(), questions);

        // final byte[] bufResponse = message.array();

        // final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length, packet.getSocketAddress());

        // serverSocket.send(packetResponse);
      }
    } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
    }
  }
}
