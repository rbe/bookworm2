/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.smil10.Audio;
import org.w3c.smil10.Meta;
import org.w3c.smil10.Par;
import org.w3c.smil10.Ref;
import org.w3c.smil10.Seq;
import org.w3c.smil10.Smil;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolverException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audioclip;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiotrack;

import aoc.mikrokosmos.objectstorage.api.ObjectStorageException;

@Singleton
@CacheConfig("audiobookRepository")
class AudiobookMapperImpl implements AudiobookMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookMapperImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final SmilReader smilReader;

    private final Locker locker;

    @Inject
    AudiobookMapperImpl(@Named("ByConfiguration") final AudiobookStreamResolver audiobookStreamResolver) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        smilReader = new SmilReader();
        locker = new Locker();
    }

    @Cacheable
    @Override
    public Audiobook audiobook(final String titelnummer) {
        Objects.requireNonNull(titelnummer);
        Audiobook audiobook;
        try {
            audiobook = createAudiobook(titelnummer, audiobookStreamResolver);
            // TODO Audiobook 'null' created
            if (null == audiobook) {
                throw new IllegalStateException();
            }
            LOGGER.debug("Audiobook '{}' created", audiobook.getIdentifier());
        } catch (AudiobookMapperException e) {
            audiobook = null;
            LOGGER.error("", e);
        }
        return audiobook;
    }

    Audiobook createAudiobook(final String titelnummer, final AudiobookStreamResolver audiobookStreamResolver) {
        Objects.requireNonNull(titelnummer);
        Objects.requireNonNull(audiobookStreamResolver);
        LOGGER.trace("Acquiring lock for {}", titelnummer);
        final Lock lock = locker.lock(titelnummer);
        lock.lock();
        try {
            LOGGER.trace("Lock for {} acquired, creating audiobook", titelnummer);
            final Audiobook audiobook = new Audiobook();
            audiobook.setTitelnummer(titelnummer);
            read(audiobook);
            return audiobook;
        } finally {
            lock.unlock();
            locker.putBack(titelnummer, lock);
        }
    }

    private void read(Audiobook audiobook) {
        final String titelnummer = audiobook.getTitelnummer();
        try {
            fromNcc(audiobook, audiobookStreamResolver.nccHtmlStream(titelnummer));
            final List<Ref> refs = fromMasterSmil(audiobook, audiobookStreamResolver.masterSmilStream(titelnummer));
            for (final Ref ref : refs) {
                final String filename = filenameFromSrc(ref);
                final InputStream refStream = audiobookStreamResolver.trackAsStream(titelnummer, filename);
                audiobook.addAudiotrack(fromRef(titelnummer, ref, refStream));
            }
        } catch (ObjectStorageException e) {
            throw new AudiobookMapperException(e);
        } catch (AudiobookStreamResolverException | AudiobookMapperException e) {
            LOGGER.error("", e);
        }

    }

    String filenameFromSrc(final Ref ref) {
        final String src = ref.getSrc();
        final int hashtag = src.indexOf('#');
        return src.substring(0, -1 < hashtag ? hashtag : src.length());
    }

    private void fromNcc(final Audiobook audiobook, final InputStream nccHtmlStream) {
        final NccReader nccReader = new NccReader(nccHtmlStream);
        audiobook.setFormat(nccReader.get(NccReader.Field.FORMAT));
        audiobook.setTocItems(nccReader.get(NccReader.Field.TOC_ITEMS));
        audiobook.setTitle(nccReader.get(NccReader.Field.TITLE));
        audiobook.setPublisher(nccReader.get(NccReader.Field.PUBLISHER));
        audiobook.setDate(nccReader.get(NccReader.Field.DATE));
        audiobook.setAuthor(nccReader.get(NccReader.Field.CREATOR));
        audiobook.setLanguage(nccReader.get(NccReader.Field.LANGUAGE));
        final String nccTotalTime = nccReader.get(NccReader.Field.TOTAL_TIME);
        final Optional<Duration> maybeTotalTime = SmilTimeHelper.parseDuration(nccTotalTime);
        if (maybeTotalTime.isPresent()) {
            audiobook.setTotalTime(maybeTotalTime.get());
        } else {
            LOGGER.warn("Hörbuch '{}' hat keine totalTime", audiobook.getTitelnummer());
        }
        audiobook.setSetInfo(nccReader.get(NccReader.Field.SET_INFO)
                .replace(" of ", " von "));
        audiobook.setSourceDate(nccReader.get(NccReader.Field.SOURCE_DATE));
        final String source = nccReader.get(NccReader.Field.SOURCE);
        if (null != source && source.startsWith("ISBN-")) {
            audiobook.setIsbn(source.substring(5));
        }
        final String sourcePublisher = nccReader.get(NccReader.Field.SOURCE_PUBLISHER);
        if (null != sourcePublisher && sourcePublisher.startsWith("Verlag: ")) {
            audiobook.setSourcePublisher(sourcePublisher.substring(8));
        } else {
            audiobook.setSourcePublisher(sourcePublisher);
        }
        final String narrator = nccReader.get(NccReader.Field.NARRATOR);
        if (null != narrator && narrator.startsWith("Sprecher: ")) {
            audiobook.setNarrator(narrator.substring(10));
        } else {
            audiobook.setNarrator(narrator);
        }
        audiobook.setAudioformat(nccReader.get(NccReader.Field.AUDIOFORMAT));
        audiobook.setCompression(nccReader.get(NccReader.Field.COMPRESSION));
    }

    private List<Ref> fromMasterSmil(final Audiobook audiobook, final InputStream masterSmilStream) {
        final Smil smil = smilReader.from(masterSmilStream);
        final List<Ref> refs = new ArrayList<>();
        for (final Object obj : smil.getHead().getContent()) {
            if (Meta.class.isAssignableFrom(obj.getClass())) {
                final Meta meta = (Meta) obj;
                switch (meta.getName()) {
                    case "dc:identifier" -> audiobook.setIdentifier(meta.getContent());
                    case "dc:title" -> audiobook.setTitle(meta.getContent());
                    case "ncc:timeInThisSmil" -> SmilTimeHelper.parseDuration(meta.getContent()).ifPresent(audiobook::setTimeInThisSmil);
                }
            }
        }
        for (final JAXBElement<?> jaxbElement : smil.getBody().getBodyContent()) {
            if (jaxbElement.getName().getLocalPart().equals("ref")) {
                refs.add((Ref) jaxbElement.getValue());
            }
        }
        return refs;
    }

    private Audiotrack fromRef(final String titelnummer, final Ref ref, final InputStream refStream) {
        final Smil smil = smilReader.from(refStream);
        final Audiotrack audiotrack = new Audiotrack();
        audiotrack.setSource(ref.getSrc());
        for (final Object headContentObj : smil.getHead().getContent()) {
            if (Meta.class.isAssignableFrom(headContentObj.getClass())) {
                final Meta nlztMeta = (Meta) headContentObj;
                switch (nlztMeta.getName()) {
                    case "ncc:timeInThisSmil" -> SmilTimeHelper.parseDuration(nlztMeta.getContent()).ifPresent(audiotrack::setTimeInThisSmil);
                    case "ncc:totalElapsedTime" -> SmilTimeHelper.parseDuration(nlztMeta.getContent()).ifPresent(audiotrack::setTotalTimeElapsed);
                    case "title" -> audiotrack.setTitle(nlztMeta.getContent());
                    // ignore default -> throw new IllegalStateException("Unexpected value: " + nlztMeta.getName());
                }
            }
        }
        for (final JAXBElement<?> bodyContentElement : smil.getBody().getBodyContent()) {
            if (bodyContentElement.getName().getLocalPart().equals("seq")) {
                final Seq seq1 = (Seq) bodyContentElement.getValue();
                for (final JAXBElement<?> seq1ContentElement : seq1.getSeqContent()) {
                    if (seq1ContentElement.getName().getLocalPart().equals("par")) {
                        final Par par = (Par) seq1ContentElement.getValue();
                        for (final JAXBElement<?> parElement : par.getParContent()) {
                            if (parElement.getName().getLocalPart().equals("seq")) {
                                final Seq seq2 = (Seq) parElement.getValue();
                                for (final JAXBElement<?> seq2ContentElement : seq2.getSeqContent()) {
                                    if (seq2ContentElement.getName().getLocalPart().equals("audio")) {
                                        final Audio audio = (Audio) seq2ContentElement.getValue();
                                        final Optional<Duration> maybeBegin = SmilTimeHelper.parseClipNpt(audio.getClipBegin());
                                        final Optional<Duration> maybeEnd = SmilTimeHelper.parseClipNpt(audio.getClipEnd());
                                        if (maybeBegin.isEmpty()|| maybeEnd.isEmpty()) {
                                            LOGGER.warn("Hörbuch '{}' Track 'title={}/src={}' hat keinen Beginn {} oder Ende {}",
                                                    titelnummer, audiotrack.getTitle(), audiotrack.getSource(), maybeBegin, maybeEnd);
                                        }
                                        final Audioclip audioclip = new Audioclip(audio.getSrc(),
                                                maybeBegin.orElse(Duration.ZERO),
                                                maybeEnd.orElse(Duration.ZERO));
                                        audiotrack.add(audioclip);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return audiotrack;
    }

    private static class Locker {

        private static final Logger LOGGER = LoggerFactory.getLogger(Locker.class);

        private final Map<String, Lock> identLocks;

        Locker() {
            identLocks = new ConcurrentHashMap<>(10);
        }

        Lock lock(final String ident) {
            synchronized (identLocks) {
                final Lock lock;
                final boolean identLockExists = identLocks.containsKey(ident);
                LOGGER.trace("identLockExists={}", identLockExists);
                if (identLockExists) {
                    lock = identLocks.get(ident);
                    LOGGER.trace("Using existing lock {} for ident {}", lock, ident);
                } else {
                    lock = new ReentrantLock();
                    identLocks.put(ident, lock);
                    LOGGER.trace("Created new lock {} for ident {}", lock, ident);
                }
                LOGGER.trace("Returning lock {} for ident {}", lock, ident);
                return lock;
            }
        }

        void putBack(final String ident, final Lock lock) {
            synchronized (identLocks) {
                LOGGER.trace("Putting back lock {} for ident {}", lock, ident);
                identLocks.put(ident, lock);
            }
        }

    }

}
