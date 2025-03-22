package repository;


import model.Post;
import model.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findAllByUser_Id(Long userId);
    List<Post> findByStatus(PostStatus status);
    List<Post> findByTitleContaining(String title);
    List<Post> findByCreatedAt(LocalDateTime createdAt);
}
