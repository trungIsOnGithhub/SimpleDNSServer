import java.util.Arrays;
import java.util.Optional;

public record DNSMessage(
    DNSHeaderSection header,
    DNSQuestionSection question,
    DNSAnswerSection answer
) {
    public enum Type {
        A(1),
        CNAME(5);
        public final int value;

        Type(int value) {
            this.value = value;
        }

        public static Optional<Type> fromValue(int value) {
            return Arrays.stream(values())
                .filter(type -> type.value == value)
                .findFirst();
        }
    }

    public enum ClassType {
        INTERNET(1),
        CSNET(2),
        CHAOS(3),
        HESIOD(4);
        public final int value;

        ClassType(int value) {
            this.value = value;
        }

        public static Optional<ClassType> fromValue(int value) {
            return Arrays.stream(values())
                .filter(type -> type.value == value)
                .findFirst();
        }
    }

    public boolean isStandardQuery() {
        return header().operationCode() == 0;
    }
}