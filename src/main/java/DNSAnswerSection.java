import java.util.stream.Collectors;

public class SimpleDNSResponse implements DNSResponseRetriever {

    public static final int NO_ERROR = 0;
    public static final int NOT_IMPLEMENTED = 4;

    @Override
    public DNSMessage getResponseMessage(DNSMessage questionMessage) {
        DNSAnswerSection responseAnswer = new DNSAnswerSection(
            questionMessage.question().questions().stream()
                .map(question -> new DNSAnswerSection.DNSRecord(
                    question.labels(),
                    DNSMessage.Type.A,
                    DNSMessage.ClassType.INTERNET,
                    60,
                    new byte[]{8, 8, 8, 8}
                ))
                .collect(Collectors.toList())
        );
        DNSHeaderSection responseHeader = new DNSHeaderSection(
            questionMessage.header().packetIdentifier(),
            DNSHeaderSection.QueryOrResponse.RESPONSE,
            questionMessage.header().operationCode(),
            0,
            0,
            0,
            0,
            0,
            questionMessage.isStandardQuery() ? NO_ERROR : NOT_IMPLEMENTED,
            questionMessage.header().questionCount(),
            responseAnswer.records().size(),
            0,
            0
        );
        return new DNSMessage(responseHeader, questionMessage.question(), responseAnswer);
    }
}