import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ForwardedDNSResponse implements DNSResponseRetriever {

    private final DatagramSocket serverSocket;
    private final InetSocketAddress forwardAddress;

    public ForwardedDNSResponse(DatagramSocket serverSocket, InetSocketAddress forwardAddress) {
        this.serverSocket = serverSocket;
        this.forwardAddress = forwardAddress;
    }

    @Override
    public DNSMessage getResponseMessage(DNSMessage questionMessage) throws IOException {
        List<DNSMessage> responses = new ArrayList<>();

        for (var question : questionMessage.question().questions()) {
            DNSMessage responseMessage = getResponseForQuestion(questionMessage.header(), question);

            responses.add(responseMessage);
        }

        List<DNSAnswerSection.DNSRecord> answers = responses.stream()
            .flatMap(r -> r.answer().records().stream())
            .collect(Collectors.toList());

        return new DNSMessage(
            cloneWithSpecifiedAnswers(questionMessage.header(), responses),
            questionMessage.question(),
            new DNSAnswerSection(answers)
        );
    }

    private DNSMessage getResponseForQuestion(DNSHeaderSection headerQuestion, DNSQuestionSection.DNSQuestion question) throws IOException {
        DNSMessage message = new DNSMessage(
            cloneForOneQuestion(headerQuestion),
            new DNSQuestionSection(Collections.singletonList(question)),
            new DNSAnswerSection(Collections.emptyList())
        );

        System.out.println("Forward(" + forwardAddress + ") : " + message);

        final byte[] queryBuffer = DNSMessageEncoder.encode(message);

        DatagramPacket packet = new DatagramPacket(queryBuffer, queryBuffer.length, forwardAddress);

        serverSocket.send(packet);

        final byte[] responseBuffer = new byte[512];
        final DatagramPacket responseFromForward = new DatagramPacket(responseBuffer, responseBuffer.length);

        serverSocket.receive(responseFromForward);

        DNSMessage responseMessage = DNSMessageDecoder.decode(responseFromForward.getData());

        System.out.println("Receive(" + responseFromForward.getSocketAddress() + ") : " + responseMessage);

        return responseMessage;
    }

    private DNSHeaderSection cloneForOneQuestion(DNSHeaderSection header) {
        return new DNSHeaderSection(
            new Random().nextInt(1, Short.MAX_VALUE * 2),
            DNSHeaderSection.QueryOrResponse.QUERY,
            header.operationCode(),
            header.authoritativeAnswer(),
            header.truncation(),
            header.recursionDesired(),
            header.recursionAvailable(),
            header.reserved(),
            header.error(),
            1, 0, 0, 0
        );
    }

    private DNSHeaderSection cloneWithSpecifiedAnswers(DNSHeaderSection header, List<DNSMessage> answers) {
        List<DNSHeaderSection> responseHeaders = answers.stream().map(DNSMessage::header).toList();

        return new DNSHeaderSection(
            header.packetIdentifier(),
            DNSHeaderSection.QueryOrResponse.RESPONSE,
            header.operationCode(),
            responseHeaders.stream().map(DNSHeaderSection::authoritativeAnswer).findFirst().orElse(header.authoritativeAnswer()),
            responseHeaders.stream().map(DNSHeaderSection::truncation).findFirst().orElse(header.truncation()),
            header.recursionDesired(),
            responseHeaders.stream().map(DNSHeaderSection::recursionAvailable).findFirst().orElse(header.recursionAvailable()),
            responseHeaders.stream().map(DNSHeaderSection::reserved).findFirst().orElse(header.reserved()),
            responseHeaders.stream().map(DNSHeaderSection::error).findFirst().orElse(header.error()),
            header.questionCount(),
            (int) answers.stream().mapToLong(m -> m.answer().records().size()).sum(),
            header.nameserverCount(),
            header.additionalRecordCount()
        );
    }
}