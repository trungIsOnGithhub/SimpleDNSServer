public enum DNSQuestionType {
    A((short)1),
    NS((short)2),
    MD((short)3),
    MF((short)4),
    CNAME((short)5),
    SOA((short)6),
    MB((short)7),
    MG((short)8),
    MR((short)9),
    NULL((short)10),
    WKS((short)11),
    PTR((short)12),
    HINFO((short)13),
    MINFO((short)14),
    MX((short)15),
    TXT((short)16),
    AXFR((short)252),
    MAILB((short)253),
    MAILA((short)254),
    ALL((short)255);

    private final short value;

    DNSQuestionType(short value) { this.value = value; }

    public short getValue() { return value; }
}