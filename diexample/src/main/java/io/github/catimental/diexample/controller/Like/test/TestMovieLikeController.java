package io.github.catimental.diexample.controller.Like.test;

import io.github.catimental.diexample.DTO.Like.LikeUpsertRequest;
import io.github.catimental.diexample.Service.Like.MovieLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/likes")
public class TestMovieLikeController {

    private final MovieLikeService movieLikeService;

    public TestMovieLikeController(MovieLikeService movieLikeService) {
        this.movieLikeService = movieLikeService;
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<Void> upsert(
            @RequestParam Long memberId,
            @RequestBody LikeUpsertRequest req,
            @PathVariable Long movieId
    ) {
        movieLikeService.upsert(memberId, movieId, req.like());
     
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> remove(
            @RequestParam Long memberId,
            @PathVariable Long movieId
    ) {
        movieLikeService.remove(memberId, movieId);
        return ResponseEntity.ok().build();
    }
}