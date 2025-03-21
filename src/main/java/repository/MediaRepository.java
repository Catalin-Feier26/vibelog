package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import model.Media;
import model.MediaType;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media,Long>{
    List<Media> findMediaByFormat(String format);
    List<Media> findMediaBySize(Long size);
    List<Media> findMediaByType(MediaType type);
    Media findMediaByUrl(String url);
}
