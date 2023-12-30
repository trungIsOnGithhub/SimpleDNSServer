public enum DNSQuestionClass {
    IN((short)1),
    CS((short)2),
    CH((short)3),
    HS((short)4),
    ;

    final short value;

    DNSQuestionClass(short value) { this.value = value; }
    public short getValue() { return value; }
}