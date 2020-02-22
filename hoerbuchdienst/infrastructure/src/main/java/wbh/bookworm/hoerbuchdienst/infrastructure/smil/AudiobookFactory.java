/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.infrastructure.smil;

import wbh.bookworm.hoerbuchdienst.infrastructure.smil.NccReader.Field;

import org.springframework.stereotype.Component;
import org.w3c.smil10.Audio;
import org.w3c.smil10.Meta;
import org.w3c.smil10.Par;
import org.w3c.smil10.Ref;
import org.w3c.smil10.Seq;
import org.w3c.smil10.Smil;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class AudiobookFactory {

    private final Path baseDirectory;

    private final SmilReader smilReader;

    public AudiobookFactory(final Path baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.smilReader = new SmilReader();
    }

    public Audiobook fromDirectory(final String name) {
        final Audiobook audiobook = new Audiobook();
        final Path audiobookDirectory = baseDirectory.resolve(name);
        fromNcc(audiobook, audiobookDirectory);
        fromSmil(audiobook, audiobookDirectory);
        return audiobook;
    }

    private void fromNcc(final Audiobook audiobook, final Path directory) {
        try (final InputStream nccHtml = Files.newInputStream(directory.resolve("ncc.html"))) {
            final NccReader nccReader = new NccReader(nccHtml);
            audiobook.setFormat(nccReader.get(Field.FORMAT));
            audiobook.setTocItems(nccReader.get(Field.TOC_ITEMS));
            audiobook.setTitle(nccReader.get(Field.TITLE));
            audiobook.setPublisher(nccReader.get(Field.PUBLISHER));
            audiobook.setDate(nccReader.get(Field.DATE));
            audiobook.setAuthor(nccReader.get(Field.CREATOR));
            audiobook.setLanguage(nccReader.get(Field.LANGUAGE));
            audiobook.setTotalTime(SmilTimeHelper.parseDuration(nccReader.get(Field.TOTAL_TIME)));
            audiobook.setSetInfo(nccReader.get(Field.SET_INFO)
                    .replace(" of ", " von "));
            audiobook.setSourceDate(nccReader.get(Field.SOURCE_DATE));
            final String source = nccReader.get(Field.SOURCE);
            if (source.startsWith("ISBN-")) {
                audiobook.setIsbn(source.substring(5));
            }
            final String sourcePublisher = nccReader.get(Field.SOURCE_PUBLISHER);
            if (sourcePublisher.startsWith("Verlag: ")) {
                audiobook.setSourcePublisher(sourcePublisher.substring(8));
            } else {
                audiobook.setSourcePublisher(sourcePublisher);
            }
            audiobook.setNarrator(nccReader.get(Field.NARRATOR));
            audiobook.setAudioformat(nccReader.get(Field.AUDIOFORMAT));
            audiobook.setCompression(nccReader.get(Field.COMPRESSION));
        } catch (IOException e) {
            throw new AudiobookFactoryException(e);
        }
    }

    private void fromSmil(final Audiobook audiobook, final Path directory) {
        try (final InputStream masterSmil = Files.newInputStream(directory.resolve("master.smil"))) {
            final Smil smil = smilReader.from(masterSmil);
            final List<Track> tracks = new ArrayList<>();
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
                            audiobook.setDuration(SmilTimeHelper.parseDuration(meta.getContent()));
                            break;
                    }
                }
            }
            for (final JAXBElement<?> jaxbElement : smil.getBody().getBodyContent()) {
                if (jaxbElement.getName().getLocalPart().equals("ref")) {
                    tracks.add(parseTitleFromRef((Ref) jaxbElement.getValue(), directory));
                }
            }
            audiobook.setTracks(tracks);
        } catch (IOException e) {
            throw new AudiobookFactoryException(e);
        }
    }

    private Track parseTitleFromRef(final Ref ref, final Path directory) {
        final int hashtag = ref.getSrc().indexOf('#');
        final String nlzt = ref.getSrc().substring(0, hashtag);
        try (final InputStream stream = Files.newInputStream(directory.resolve(nlzt))) {
            final Smil smil = smilReader.from(stream);
            final Track track = new Track();
            track.setSource(ref.getSrc());
            for (final Object headContentObj : smil.getHead().getContent()) {
                if (Meta.class.isAssignableFrom(headContentObj.getClass())) {
                    final Meta nlztMeta = (Meta) headContentObj;
                    switch (nlztMeta.getName()) {
                        case "ncc:timeInThisSmil":
                            track.setTimeInThisSmil(SmilTimeHelper.parseDuration(nlztMeta.getContent()));
                            break;
                        case "ncc:totalElapsedTime":
                            track.setTotalTimeElapsed(SmilTimeHelper.parseDuration(nlztMeta.getContent()));
                            break;
                        case "title":
                            track.setTitle(nlztMeta.getContent());
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
                                            track.add(audioclip);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return track;
        } catch (IOException e) {
            throw new AudiobookFactoryException(e);
        }
    }

}
