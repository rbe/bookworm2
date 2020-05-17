package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import javax.inject.Inject;
import javax.inject.Named;

import io.micronaut.test.annotation.MicronautTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
class ObjectStorageAudiobookStreamResolverImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectStorageAudiobookStreamResolverImplTest.class);

    private static final String TITLENUMMER = "32909";

    @Inject
    @Named("objectstorage")
    private AudiobookStreamResolver audiobookStreamResolver;

}
