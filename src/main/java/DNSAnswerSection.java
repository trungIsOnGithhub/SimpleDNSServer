import java.util.List;

public record DNSAnswerSection(List<DNSRecord> records) {
    public record DNSRecord(String name, DNSMessage.Type dataType, DNSMessage.ClassType dataClass, int ttl, byte[] data) {}
}