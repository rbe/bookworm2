package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

public final class MyHashCodeImpl {

    private static final int HI_BYTE_SHIFT = 0;

    private static final int LO_BYTE_SHIFT = 8;

    private MyHashCodeImpl() {
        throw new AssertionError();
    }

    public static int hashCode(byte[] value) {
        int h = 0;
        int length = value.length >> 1;
        for (int i = 0; i < length; i++) {
            h = 31 * h + getChar(value, i);
        }
        return h;
    }

    private static char getChar(byte[] val, int index) {
        assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
        index <<= 1;
        return (char) (((val[index++] & 0xff) << HI_BYTE_SHIFT) |
                ((val[index] & 0xff) << LO_BYTE_SHIFT));
    }

    private static int length(byte[] value) {
        return value.length >> 1;
    }

}
