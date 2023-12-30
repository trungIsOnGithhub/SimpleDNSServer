public enum RCode {
    NO_ERROR((byte)0),
    FORMAT_ERROR((byte)1),
    SERVER_FAILURE((byte)2),
    NAME_ERROR((byte)3),
    NOT_IMPLEMENTED((byte)4),
    REFUSED((byte)5),
    ;

    private final byte value;
    RCode(byte b) { this.value = b; }
    public byte value() { return value; }
}