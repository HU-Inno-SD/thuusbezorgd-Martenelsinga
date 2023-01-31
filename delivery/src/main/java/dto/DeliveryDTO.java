package dto;

import domain.Rider;

public class DeliveryDTO {
    private Long id;
    private boolean completed;
    private Rider rider;

    public DeliveryDTO(Long id, boolean completed, Rider rider) {
        this.id = id;
        this.completed = completed;
        this.rider = rider;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }
}
