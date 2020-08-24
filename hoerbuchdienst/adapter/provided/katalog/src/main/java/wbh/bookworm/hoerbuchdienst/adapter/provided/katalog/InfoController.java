/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import javax.inject.Inject;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.Post;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;
import wbh.bookworm.hoerbuchdienst.adapter.provided.api.HoerbuchNichtGefundenException;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookInfoDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.KatalogService;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.PlaylistDTO;
import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.TrackInfoDTO;
import wbh.bookworm.hoerbuchdienst.sharding.shared.AudiobookShardRedirector;
import wbh.bookworm.hoerbuchdienst.sharding.shared.CORS;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@Controller(InfoController.BASE_URL)
public class InfoController {

    static final String BASE_URL = "info";

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);

    private final KatalogService katalogService;

    private final AudiobookShardRedirector audiobookShardRedirector;

    @Inject
    public InfoController(final KatalogService katalogService,
                          final AudiobookShardRedirector audiobookShardRedirector) {
        this.katalogService = katalogService;
        this.audiobookShardRedirector = audiobookShardRedirector;
    }

    @Options(uri = "audiobook")
    public HttpResponse<String> optionsAudiobookInfo(final HttpRequest<?> httpRequest) {
        return optionsResponse(httpRequest);
    }

    @Post(uri = "audiobook", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<AudiobookInfoAntwortDTO> audiobookInfo(final HttpRequest<?> httpRequest, @Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(audiobookAnfrageDTO.getTitelnummer(),
                () -> {
                    try {
                        final AudiobookInfoDTO audiobookInfoDTO = katalogService.audiobookInfo(audiobookAnfrageDTO.getHoerernummer(),
                                audiobookAnfrageDTO.getTitelnummer());
                        return AudiobookMapper.INSTANCE.convert(audiobookInfoDTO);
                    } catch (Exception e) {
                        throw new HoerbuchNichtGefundenException(String.format("Hörbuch %s nicht gefunden", audiobookAnfrageDTO.getTitelnummer()), e);
                    }
                },
                dto -> CORS.response(httpRequest, dto),
                null, String.format("%s/audiobook", BASE_URL),
                httpRequest);
    }

    @Options(uri = "playlist")
    public HttpResponse<String> optionsPlaylist(final HttpRequest<?> httpRequest) {
        return optionsResponse(httpRequest);
    }

    @Post(uri = "playlist", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<PlaylistAntwortDTO> playlist(final HttpRequest<?> httpRequest, @Body final AudiobookAnfrageDTO audiobookAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(audiobookAnfrageDTO.getTitelnummer(),
                () -> {
                    try {
                        final PlaylistDTO playlist = katalogService.playlist(audiobookAnfrageDTO.getHoerernummer(),
                                audiobookAnfrageDTO.getTitelnummer());
                        return PlaylistMapper.INSTANCE.convert(playlist);
                    } catch (Exception e) {
                        throw new BusinessException("", e);
                    }
                },
                dto -> CORS.response(httpRequest, dto),
                null, String.format("%s/playlist", BASE_URL),
                httpRequest);
    }

    @Options(uri = "track")
    public HttpResponse<String> optionsTrack(final HttpRequest<?> httpRequest) {
        return optionsResponse(httpRequest);
    }

    @Post(uri = "track", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<TrackInfoAntwortDTO> track(final HttpRequest<?> httpRequest, @Body final TrackAnfrageDTO trackAnfrageDTO) {
        return audiobookShardRedirector.withLocalOrRedirect(trackAnfrageDTO.getTitelnummer(),
                () -> {
                    LOGGER.debug("Hörer '{}' Hörbuch '{}': Rufe Track-Info '{}' mit Wasserzeichen ab",
                            trackAnfrageDTO.getHoerernummer(),
                            trackAnfrageDTO.getTitelnummer(),
                            trackAnfrageDTO.getIdent());
                    try {
                        final TrackInfoDTO trackInfoDTO = katalogService.trackInfo(trackAnfrageDTO.getHoerernummer(),
                                trackAnfrageDTO.getTitelnummer(),
                                trackAnfrageDTO.getIdent());
                        return TrackMapper.INSTANCE.convert(trackInfoDTO);
                    } catch (Exception e) {
                        throw new BusinessException("", e);
                    }
                },
                dto -> CORS.response(httpRequest, dto),
                null, String.format("%strack", BASE_URL),
                httpRequest);
    }

    @Mapper
    public interface AudiobookMapper {

        AudiobookMapper INSTANCE = Mappers.getMapper(AudiobookMapper.class);

        AudiobookInfoAntwortDTO convert(AudiobookInfoDTO audiobookInfoDTO);

    }

    @Mapper
    public interface PlaylistMapper {

        PlaylistMapper INSTANCE = Mappers.getMapper(PlaylistMapper.class);

        PlaylistAntwortDTO convert(PlaylistDTO playlistDTO);

    }

    @Mapper
    public interface TrackMapper {

        TrackMapper INSTANCE = Mappers.getMapper(TrackMapper.class);

        TrackInfoAntwortDTO convert(TrackInfoDTO trackInfoDTO);

    }

}
