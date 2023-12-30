import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

        System.out.println("Received data");
    
        final byte[] bufResponse = new byte[512];

        final var headr = new PacketHeader(
          (short)1234, true, BYTE_ZERO, false, false, false, false, BYTE_ZERO,
          RCode.NO_ERROR, SHORT_ZERO, SHORT_ZERO, SHORT_ZERO, SHORT_ZERO
        );

        final byte[] bufResponse2 = header.getHeader().array();

        final DatagramPacket packetResponse = new DatagramPacket(bufResponse, bufResponse.length, packet.getSocketAddress());

        serverSocket.send(packetResponse);
      }

    } catch (IOException e) {

        System.out.println("IOException: " + e.getMessage());

    }
  }
}
