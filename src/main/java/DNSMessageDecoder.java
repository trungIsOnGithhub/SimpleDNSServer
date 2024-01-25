import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DNSMessageDecoder {

    public static DNSMessage decode(byte[] buffer) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);

        DNSHeaderSection header = decodeHeader(byteBuffer);
        DNSQuestionSection question = decodeQuestion(byteBuffer, header.questionCount());
        DNSAnswerSection answer = decodeAnswer(byteBuffer, header.answerCount());

        return new DNSMessage(header, question, answer);
    }

    private static DNSHeaderSection decodeHeader(ByteBuffer byteBuffer) {
        // https://stackoverflow.com/questions/14713102/what-does-and-0xff-do
        final int packetIdentifier = byteBuffer.getShort() & 0xFFFF;

        byte firstBitMask = byteBuffer.get();

        final int questionOrReponse = (firstBitMask >> 7) & 1;
        final int operationCode = (firstBitMask >> 3) & 0b1111;
        final int authoritativeAnswer = (firstBitMask >> 2) & 1;
        final int truncation = (firstBitMask >> 1) & 1;
        final int recursionDesired = firstBitMask & 1;

        byte secondBitMask = byteBuffer.get();

        final int recursionAvailable = (secondBitMask >> 7) & 1;
        final int reserved = (secondBitMask >> 4) & 0b111;
        final int error = secondBitMask & 0b1111;

        final int questionCount = byteBuffer.getShort();
        final int answerCount = byteBuffer.getShort();
        final int nameserverCount = byteBuffer.getShort();
        final int additionalRecordCount = byteBuffer.getShort();

        return new DNSHeaderSection(
            packetIdentifier,
            DNSHeaderSection.QueryOrResponse.fromValue(questionOrReponse).orElseThrow(),
            operationCode,
            authoritativeAnswer,
            truncation,
            recursionDesired,
            recursionAvailable,
            reserved,
            error,
            questionCount,
            answerCount,
            nameserverCount,
            additionalRecordCount
        );
    }

    private static DNSQuestionSection decodeQuestion(ByteBuffer byteBuffer, int numberOfQuestions) {
        return new DNSQuestionSection(
            IntStream.range(0, numberOfQuestions)
                .mapToObj(i -> {
                    String labels = decodeLabels(byteBuffer);

                    final int queryType = byteBuffer.getShort();
                    final int queryClass = byteBuffer.getShort();

                    return new DNSQuestionSection.DNSQuestion(
                        labels,
                        DNSMessage.Type.fromValue(queryType).orElseThrow(),
                        DNSMessage.ClassType.fromValue(queryClass).orElseThrow()
                    );
                }).collect(Collectors.toList())
        );
    }

    private static DNSAnswerSection decodeAnswer(ByteBuffer byteBuffer, int numberOfAnswers) {
        List<DNSAnswerSection.DNSRecord> records = IntStream.range(0, numberOfAnswers)
            .mapToObj(i -> {
                String labels = decodeLabels(byteBuffer);

                int queryType = byteBuffer.getShort();
                int queryClass = byteBuffer.getShort();
                int ttl = byteBuffer.getInt();
                int dataLength = byteBuffer.getShort();

                byte[] data = new byte[dataLength];

                byteBuffer.get(data);
                return new DNSAnswerSection.DNSRecord(
                    labels,
                    DNSMessage.Type.fromValue(queryType).orElseThrow(),
                    DNSMessage.ClassType.fromValue(queryClass).orElseThrow(),
                    ttl,
                    data
                );
            })
            .collect(Collectors.toList());

        return new DNSAnswerSection(records);
    }

    private static String decodeLabels(ByteBuffer byteBuffer) {
        List<String> labels = new ArrayList<>();
        int labelLength;

        do {
            // 0b mean in binary form, get 8 bit for label
            labelLength = byteBuffer.get() & 0b11111111;

            if ((labelLength >> 6) == 0b11) { // get label length
                int position = ((labelLength & 0b00111111) << 8) | (byteBuffer.get() & 0b11111111);

                labels.add(decodeLabels(byteBuffer.duplicate().position(position)));
            }
            else if (0 < labelLength) {
                // auto convert to standard charset utf 8
                String label = new String(byteBuffer.array(), byteBuffer.position(), labelLength, StandardCharsets.UTF_8);

                byteBuffer.position(byteBuffer.position() + label.length());

                labels.add(label);
            }

        } while (0 < labelLength && (labelLength >> 6) != 0b11);

        return String.join(".", labels); // join to become domain
    }
}