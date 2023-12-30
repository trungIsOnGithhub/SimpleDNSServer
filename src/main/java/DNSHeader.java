import java.nio.ByteBuffer;
import java.util.Arrays;

public class DNSHeader {
  private short[] header = new short[6];

  public DNSHeader(short id, boolean qr, short opcode, boolean aa, boolean tc,
        boolean rd, boolean ra, byte z, byte rcode, short qdcount,
        short ancount, short nscount, short arcount) {}
  public DNSHeader() {}

  public DNSHeader getHeader() {
    return new DNSHeader(getID(), getQR(), getOPCODE(), getAA(), getTC(),
        getRD(), getRA(), getZ(), getRCODE(), getQDCOUNT(),
        getANCOUNT(), getNSCOUNT(), getARCOUNT());

  }

  public void setHeader(short[] header) { this.header = header; }

  public short getID() { return header[0]; }
  public void setID(short ID) { this.header[0] = ID; }

  public boolean getQR() { return ((header[1] & 0x8000) >>> 0x000F) == 1; }
  public void setQR(boolean QR) {
    this.header[1] = (short)(header[1] | (short)(QR ? 0x8000 : 0x0000));
  }

  public short getOPCODE() { return (short)((this.header[1] & 0x7800) >>> 8); }

  public void setOPCODE(byte OPCODE) {

    this.header[1] = (short)(header[1] | (short)(OPCODE << 8));

  }

  public boolean getAA() { return ((header[1] & 0x0400) >>> 10) == 1; }
  public void setAA(boolean AA) {
    this.header[1] = (short)(header[1] | (short)(AA ? 0x0400 : 0x0000));
  }

  public boolean getTC() { return ((header[1] & 0x0200) >>> 9) == 1; }
  public void setTC(boolean TC) {
    this.header[1] = (short)(header[1] | (short)(TC ? 0x0200 : 0x0000));
  }

  public boolean getRD() { return ((header[1] & 0x0100) >>> 8) == 1; }
  public void setRD(boolean RD) {
    this.header[1] = (short)(header[1] | (short)(RD ? 0x0100 : 0x0000));
  }

  public boolean getRA() { return ((header[1] & 0x0080) >>> 7) == 1; }
  public void setRA(boolean RA) {
    this.header[1] = (short)(header[1] | (short)(RA ? 0x0080 : 0x0000));
  }

  public byte getZ() { return (byte)(header[1] & 0x0070); }
  public void setZ(byte Z) { this.header[1] = (short)(header[1] | Z); }

  public byte getRCODE() { return (byte)(header[1] & 0x000F); }
  public void setRCODE(byte RCODE) {
    this.header[1] = (short)(header[1] | RCODE);
  }

  public short getQDCOUNT() { return header[2]; }
  public void setQDCOUNT(short QDCOUNT) { this.header[2] = QDCOUNT; }

  public short getANCOUNT() { return header[3]; }
  public void setANCOUNT(short ANCOUNT) { this.header[3] = ANCOUNT; }

  public short getNSCOUNT() { return header[4]; }
  public void setNSCOUNT(short NSCOUNT) { this.header[4] = NSCOUNT; }

  public short getARCOUNT() { return header[5]; }
  public void setARCOUNT(short ARCOUNT) { this.header[5] = ARCOUNT; }

  public byte[] toBytes() {

    ByteBuffer buffer = ByteBuffer.allocate(12);

    for (short value : header) {
      buffer.putShort(value);
    }
    return buffer.array();
  }

  // @Override
  // public String toString() {

  //   return "DNSHeader{"

  //       + "header=" + Arrays.toString(header) +

  //       ", header=" + Integer.toBinaryString(header[1]) + ", ID=" + getID() +

  //       ", QR=" + getQR() + ", OPCODE=" + getOPCODE() + ", AA=" + getAA() +

  //       ", TC=" + getTC() + ", RD=" + getRD() + ", RA=" + getRA() +

  //       ", Z=" + getZ() + ", RCODE=" + getRCODE() +

  //       ", QDCOUNT=" + getQDCOUNT() + ", ANCOUNT=" + getANCOUNT() +

  //       ", NSCOUNT=" + getNSCOUNT() + ", ARCOUNT=" + getARCOUNT() + '}';
  // }
}