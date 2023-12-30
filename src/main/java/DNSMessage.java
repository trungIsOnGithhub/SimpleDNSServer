import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DNSMessage {
    private DNSHeader header;
    private int questionCount;
    private int answerCount;
    private int authorityCount;
    private int additionalCount;
    private List<DNSQuestion> questions = new ArrayList<>();
    private DNSQuestion question;

    public DNSMessage(DNSHeader header, DNSQuestion question) {
        this.header = header;
        this.question = question;
        addQuestion(question);
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(512);

        buffer.put(header.toBytes());
        buffer.put(question.toBytes());

        return buffer.array();
    }

    public void addQuestion(DNSQuestion question) {
        this.questions.add(question);
        ++this.questionCount;
    }

    public static DNSMessage parseFromBuffer(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        short[] h = new short[6];

        buffer.order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(h);

        DNSHeader dnsHeader = new DNSHeader();

        dnsHeader.setHeader(h);

        DNSHeader header = dnsHeader.getHeader();

        DNSQuestion dnsQuestion = DNSQuestion.parse(buffer);

        int questionCount = buffer.getShort(),
            answerCount = buffer.getShort(),
            authorityCount = buffer.getShort(),
            additionalCount = buffer.getShort();

        DNSMessage dnsMessage = new DNSMessage(header, dnsQuestion);

        return dnsMessage;
    }

//   @Override
//   public String toString() {
//     return "DNSMessage{"
//         + "header=" + header + ", questionCount=" + questionCount +
//         ", answerCount=" + answerCount + ", authorityCount=" + authorityCount +
//         ", additionalCount=" + additionalCount + ", question=" + question + '}';
//   }
}