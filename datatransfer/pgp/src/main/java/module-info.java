module wbh.bookworm.datatransfer.pgp {
    requires bouncy.gpg;
    requires com.fasterxml.jackson.annotation;
    requires org.bouncycastle.pg;
    requires org.bouncycastle.provider;

    exports aoc.crypto.pgp;
}
