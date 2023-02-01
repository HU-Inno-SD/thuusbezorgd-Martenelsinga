package presentation;

import application.DeliveryService;
import common.messages.AddDeliveryCommand;
import data.DeliveryRepository;
import data.RiderRepository;
import domain.*;

import dto.DeliveryDTO;
import exception.DeliveryNotFoundException;
import infrastructure.DeliveryPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryRepository deliveries;
//    private final ReviewRepository reviews;
    private final DeliveryPublisher publisher;
    private final RiderRepository riders;

    public DeliveryController(DeliveryService deliveryService, DeliveryRepository deliveries, RiderRepository riders) {
        this.deliveryService = deliveryService;
        this.deliveries = deliveries;
//        this.reviews = reviews;
        this.publisher = new DeliveryPublisher();
        this.riders = riders;
    }

    @RabbitListener(queues = "deliveryQueue")
    public void fixDelivery(AddDeliveryCommand command){
        this.deliveryService.scheduleDelivery(command.getOrderId(), command.getAddress());
    }


    @GetMapping("/user}")
    public List<DeliveryDTO> deliveries(@RequestBody Long userId) {
        List<Delivery> found = deliveries.findByOrder_UserId(userId);
        List<DeliveryDTO> dtos = new ArrayList<>();
        for(Delivery d : found){
            dtos.add(new DeliveryDTO(d.getId(), d.isCompleted(), d.getRider()));
        }
        return dtos;
    }

    @GetMapping("{id}")
    public DeliveryDTO getDelivery(@PathVariable long id) throws DeliveryNotFoundException {
        Optional<Delivery> delivery = this.deliveries.findById(id);

        if (delivery.isEmpty()) {
            throw new DeliveryNotFoundException("Delivery not found");
        }
        DeliveryDTO dto = new DeliveryDTO(delivery.get().getId(), delivery.get().isCompleted(), delivery.get().getRider());
        return dto;
    }

    @PostMapping("/new/")
    public void addDelivery(@RequestBody Delivery delivery) {
        this.deliveries.save(delivery);
    }

    @PostMapping("/riders/new")
    public void addRider(@RequestBody Rider rider){
        this.riders.save(rider);
    }


//    public record ReviewDTO(String delivery, String reviewerName, int rating) {
//        public static ReviewDTO fromReview(DeliveryReview review) {
//            return new ReviewDTO(review.getDelivery().getId().toString(), review.getUser().getName(), review.getRating().toInt());
//        }
//    }

//    public record PostedReviewDTO(int rating) {
//
//    }

//    @GetMapping("/{id}/reviews")
//    public ResponseEntity<List<ReviewDTO>> getDishReviews(@PathVariable("id") long id) {
//        Optional<Delivery> d = this.deliveries.findById(id);
//        if (d.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<DeliveryReview> reviews = this.reviews.findDeliveryReviews(d.get());
//        return ResponseEntity.ok(reviews.stream().map(ReviewDTO::fromReview).toList());
//    }
//
//    @PostMapping("/{id}/reviews")
//    @Transactional
//    public ResponseEntity<ReviewDTO> postReview(User user, @PathVariable("id") long id, @RequestBody PostedReviewDTO reviewDTO) {
//        Optional<Delivery> found = this.deliveries.findById(id);
//        if (found.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        DeliveryReview review = new DeliveryReview(found.get(), ReviewRating.fromInt(reviewDTO.rating()), user);
//        reviews.save(review);
//
//        return ResponseEntity.ok(ReviewDTO.fromReview(review));
//    }
}