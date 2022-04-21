package com.example.idatt2106_2022_05_backend.service;

import com.example.idatt2106_2022_05_backend.dto.Review.NewReviewDto;
import com.example.idatt2106_2022_05_backend.dto.Review.ReviewDto;
import com.example.idatt2106_2022_05_backend.model.Review;
import com.example.idatt2106_2022_05_backend.model.User;
import com.example.idatt2106_2022_05_backend.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    /*
     * - get reviews by ad_id - get reviews by user_id
     */

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    private ModelMapper modelMapper = new ModelMapper();

    private void validate(NewReviewDto newReviewDto) {
        if (newReviewDto.getDescription().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description exceeds maximum lenght");

        }
        if (0 > newReviewDto.getRating() && newReviewDto.getRating() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating outside of scale");
        }
    }

    private Review createNewReview(NewReviewDto newReviewDto) {
        Review review = new Review();
        validate(newReviewDto);

        review.setDescription(newReviewDto.getDescription());
        review.setRating(newReviewDto.getRating());
        review.setUser(userService.getbyId(newReviewDto.getUser_id()));

        // TODO: set ad

        reviewRepository.save(review);
        return review;
    }

    public List<ReviewDto> getReviewsByUserId(long id) {
        User user = userService.getbyId(id); // TODO: might not be ReviewDto????
        return reviewRepository.getAllByUser(user).stream().map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }
}
