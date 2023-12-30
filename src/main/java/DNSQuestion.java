import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public record DNSQuestion(String domainName,
            short questionType, short questionClass) {

    public static DNSQuestion parse(ByteBuffer buffer) {
        // ByteBuffer buffer = ByteBuffer.wrap(data, offset, data.length - offset);
        StringBuilder domainBuilder = new StringBuilder();
        byte labelLength;
        // short questionNr = DNSHeader.getQDCOUNT();

        while ((labelLength = buffer.get()) > 0) {
            if (!domainBuilder.isEmpty()) {
                domainBuilder.append(".");
            }

            byte[] labelBytes = new byte[labelLength];

            buffer.get(labelBytes);

            domainBuilder.append(
                new String(labelBytes)
            );
        }

        short qType = buffer.getShort();
        short qClass = buffer.getShort();

        return new DNSQuestion(domainBuilder.toString(), qType, qClass);
    }

    public ByteBuffer get() {
        byte[] domainBytes = encodeDomainName(domainName);

        ByteBuffer buffer = ByteBuffer.allocate(domainBytes.length + 4);

        buffer.put(domainBytes);
        buffer.putShort((short)questionType);
        buffer.putShort((short)questionClass);

        return buffer;
    }

    public byte[] encodeDomainName(String domain) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String label : domain.split("\\.")) {
            outputStream.write((byte)label.length());
            outputStream.writeBytes(label.getBytes(StandardCharsets.UTF_8));
        }
        outputStream.write(0); // Terminating null byte

        return outputStream.toByteArray();
    }
}