import java.nio.ByteBuffer;
import java.util.BitSet;

public record DNSHeader(
    short identifier, boolean qr, byte opcode,
    boolean aa, boolean tc, boolean rd, boolean ra,
    byte z, RCode rcode, short qdCount, short anCount,
    short nsCount, short arCount) {

  private static final int BUFFER_CAPACITY = 12;
  private static final int FLAG_BITS_CAPACITY = 8;

  public ByteBuffer getHeader() {
    ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

    BitSet flags = new BitSet(FLAG_BITS_CAPACITY);

    BitSet opcodeBitSet = BitSet.valueOf(new byte[] {opcode});

    flags.set(7, this.qr);
    for (int i = 0; i < 4; i++) {
      flags.set(6 - i, opcodeBitSet.get(3 - i));
    }

    flags.set(0, rd);
    flags.set(1, tc);
    flags.set(2, aa);

    // Insert all into buffer
    buffer.putShort(identifier);

    buffer.put(flags.toByteArray());

    BitSet secondFlags = new BitSet(FLAG_BITS_CAPACITY);
    secondFlags.set(7, ra);
    // Set the reserved bits by DNSEC to 0 ALWAYS

    for (int i = 0; i < 3; i++)
      secondFlags.set(6 - i, false);

    // Cast opcode into bitset, set bits accordingly
    BitSet rCodeBitSet = BitSet.valueOf(new byte[] {opcode});

    for (int i = 0; i < 4; i++) {
      secondFlags.set(3 - i, rCodeBitSet.get(3 - i));
    }

    // Put rest of the information into byte array
    buffer.put(secondFlags.toByteArray());
    buffer.putShort(qdCount);
    buffer.putShort(anCount);
    buffer.putShort(nsCount);
    buffer.putShort(arCount);

    return buffer;
  }
}