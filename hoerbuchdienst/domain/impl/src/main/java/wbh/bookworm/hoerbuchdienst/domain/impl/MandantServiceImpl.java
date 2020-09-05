package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;

import wbh.bookworm.hoerbuchdienst.domain.ports.MandantService;
import wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository.MandantRepository;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

final class MandantServiceImpl implements MandantService {

    private final MandantRepository mandantRepository;

    @Inject
    MandantServiceImpl(final MandantRepository mandantRepository) {
        this.mandantRepository = mandantRepository;
    }

    @Override
    public boolean kannBestellen(final MandantenId mandantenId, final Hoerernummer hoerernummer) {
        return false;
    }

}
