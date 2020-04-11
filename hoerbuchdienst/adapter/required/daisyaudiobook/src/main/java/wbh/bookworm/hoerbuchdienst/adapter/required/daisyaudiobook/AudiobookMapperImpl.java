/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Singleton;
import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.annotation.Property;
import lombok.extern.slf4j.Slf4j;
import org.w3c.smil10.Audio;
import org.w3c.smil10.Meta;
import org.w3c.smil10.Par;
import org.w3c.smil10.Ref;
import org.w3c.smil10.Seq;
import org.w3c.smil10.Smil;

import wbh.bookworm.hoerbuchdienst.domain.required.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.Audioclip;
import wbh.bookworm.hoerbuchdienst.domain.required.Audiotrack;

@Singleton
@CacheConfig("audiobook")
@Slf4j
public class AudiobookMapperImpl implements AudiobookMapper {

    @Property(name = "hoerbuchdienst.repository.uri")
    private Path baseDirectory;

    private final SmilReader smilReader;

    public AudiobookMapperImpl() {
        this.smilReader = new SmilReader();
    }

    private static void fromNcc(final Audiobook audiobook, final Path directory) {
        try (final InputStream nccHtml = Files.newInputStream(directory.resolve("ncc.html"))) {
            final NccReader nccReader = new NccReader(nccHtml);
            audiobook.setFormat(nccReader.get(NccReader.Field.FORMAT));
            audiobook.setTocItems(nccReader.get(NccReader.Field.TOC_ITEMS));
            audiobook.setTitle(nccReader.get(NccReader.Field.TITLE));
            audiobook.setPublisher(nccReader.get(NccReader.Field.PUBLISHER));
            audiobook.setDate(nccReader.get(NccReader.Field.DATE));
            audiobook.setAuthor(nccReader.get(NccReader.Field.CREATOR));
            audiobook.setLanguage(nccReader.get(NccReader.Field.LANGUAGE));
            audiobook.setTotalTime(SmilTimeHelper.parseDuration(nccReader.get(NccReader.Field.TOTAL_TIME)));
            audiobook.setSetInfo(nccReader.get(NccReader.Field.SET_INFO)
                    .replace(" of ", " von "));
            audiobook.setSourceDate(nccReader.get(NccReader.Field.SOURCE_DATE));
            final String source = nccReader.get(NccReader.Field.SOURCE);
            if (source.startsWith("ISBN-")) {
                audiobook.setIsbn(source.substring(5));
            }
            final String sourcePublisher = nccReader.get(NccReader.Field.SOURCE_PUBLISHER);
            if (sourcePublisher.startsWith("Verlag: ")) {
                audiobook.setSourcePublisher(sourcePublisher.substring(8));
            } else {
                audiobook.setSourcePublisher(sourcePublisher);
            }
            final String narrator = nccReader.get(NccReader.Field.NARRATOR);
            if (narrator.startsWith("Sprecher: ")) {
                audiobook.setNarrator(narrator.substring(10));
            } else {
                audiobook.setNarrator(narrator);
            }
            audiobook.setAudioformat(nccReader.get(NccReader.Field.AUDIOFORMAT));
            audiobook.setCompression(nccReader.get(NccReader.Field.COMPRESSION));
        } catch (IOException e) {
            throw new AudiobookMapperException(e);
        }
    }

    @Override
    @Cacheable
    public Audiobook from(final String titelnummer) {
        final Path audiobookDirectory = baseDirectory.resolve(String.format("%sKapitel", titelnummer));
        if (Files.exists(audiobookDirectory)) {
            final Audiobook audiobook = new Audiobook();
            audiobook.setTitelnummer(titelnummer);
            try {
                fromNcc(audiobook, audiobookDirectory);
                fromSmil(audiobook, audiobookDirectory);
                return audiobook;
            } catch (AudiobookMapperException e) {
                log.error("", e);
            }
        } else {
            log.error("Hörbuch {}: Verzeichnis existiert nicht", titelnummer);
        }
        return null;
    }

    private void fromSmil(final Audiobook audiobook, final Path directory) {
        try (final InputStream masterSmil = Files.newInputStream(directory.resolve("master.smil"))) {
            final Smil smil = smilReader.from(masterSmil);
            final List<Audiotrack> audiotracks = new ArrayList<>();
            for (final Object obj : smil.getHead().getContent()) {
                if (Meta.class.isAssignableFrom(obj.getClass())) {
                    final Meta meta = (Meta) obj;
                    switch (meta.getName()) {
                        case "dc:identifier":
                            audiobook.setIdentifier(meta.getContent());
                            break;
                        case "dc:title":
                            audiobook.setTitle(meta.getContent());
                            break;
                        case "ncc:timeInThisSmil":
                            audiobook.setTimeInThisSmil(SmilTimeHelper.parseDuration(meta.getContent()));
                            break;
                    }
                }
            }
            for (final JAXBElement<?> jaxbElement : smil.getBody().getBodyContent()) {
                if (jaxbElement.getName().getLocalPart().equals("ref")) {
                    audiotracks.add(parseTitleFromRef((Ref) jaxbElement.getValue(), directory));
                }
            }
            audiobook.setAudiotracks(audiotracks);
        } catch (IOException e) {
            throw new AudiobookMapperException(e);
        }
    }

    private Audiotrack parseTitleFromRef(final Ref ref, final Path directory) {
        final int hashtag = ref.getSrc().indexOf('#');
        final String nlzt = ref.getSrc().substring(0, hashtag);
        try (final InputStream stream = Files.newInputStream(directory.resolve(nlzt))) {
            final Smil smil = smilReader.from(stream);
            final Audiotrack audiotrack = new Audiotrack();
            audiotrack.setSource(ref.getSrc());
            for (final Object headContentObj : smil.getHead().getContent()) {
                if (Meta.class.isAssignableFrom(headContentObj.getClass())) {
                    final Meta nlztMeta = (Meta) headContentObj;
                    switch (nlztMeta.getName()) {
                        case "ncc:timeInThisSmil":
                            audiotrack.setTimeInThisSmil(SmilTimeHelper.parseDuration(nlztMeta.getContent()));
                            break;
                        case "ncc:totalElapsedTime":
                            audiotrack.setTotalTimeElapsed(SmilTimeHelper.parseDuration(nlztMeta.getContent()));
                            break;
                        case "title":
                            audiotrack.setTitle(nlztMeta.getContent());
                            break;
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
                                            final Audioclip audioclip = new Audioclip(audio.getSrc(),
                                                    SmilTimeHelper.parseClipNpt(audio.getClipBegin()),
                                                    SmilTimeHelper.parseClipNpt(audio.getClipEnd()));
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
        } catch (IOException e) {
            throw new AudiobookMapperException(e);
        }
    }

}
