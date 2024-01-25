import java.util.Arrays;
import java.util.Optional;

enum TestEnum {
    Value1(0),
    Value2(1);

    public final int value;

    TestEnum(int value) {
        this.value = value;
    }
    public static Optional<TestEnum> fromValue(int value) {
        return Arrays.stream(values())
            .filter(type -> type.value == value)
            .findFirst();
    }
}

public class BitsetTest {
    public static void main(String[] args) {
        TestEnum te = TestEnum.Value1;

        System.out.println(te);

        var nullableValue = TestEnum.fromValue(1);

        if (nullableValue.isPresent() && nullableValue.get() == TestEnum.Value2) {
            System.out.println("We 're right");
        }
    }
}