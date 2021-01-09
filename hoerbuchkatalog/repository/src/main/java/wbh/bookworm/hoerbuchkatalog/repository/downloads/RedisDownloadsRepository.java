package wbh.bookworm.hoerbuchkatalog.repository.downloads;

import org.springframework.data.repository.CrudRepository;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;

public interface RedisDownloadsRepository extends CrudRepository<Downloads, String> {
}
