import java.nio.ByteBuffer;

public class ByteBufferTest {
    public static void main(String[] args) {
        byte numByte = 4;

        int indicator = (numByte >> 2) & 1;

        System.out.println(indicator);
    }
}