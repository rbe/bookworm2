package wbh.bookworm.shared.domain;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;

/**
 * ValueObject
 */
public final class MandantenId extends DomainSingleValueObject<String, String> {

    private static final long serialVersionUID = -383384716454607009L;

    public MandantenId(final String value) {
        super(value);
    }

    @Override
    public boolean checkValue(final String s) {
        return super.checkValue(s);
    }

}
