package com.codefellowship.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codefellowship.models.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post findPostById(Long post_id);
}