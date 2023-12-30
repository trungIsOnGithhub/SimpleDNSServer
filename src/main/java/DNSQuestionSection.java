import java.util.List;

public record DNSQuestionSection(List<DNSQuestion> questions) {
    public record DNSQuestion(String labels, DNSMessage.Type type, DNSMessage.ClassType classType) {}
}