package nl.hu.inno.thuusbezorgd.domain;

public enum OrderStatus {
    Received,
    InPreparation,
    ReadyForDelivery,
    Underway,
    Delivered,
    Disputed,
    Resolved;

    public OrderStatus next() {
        return switch (this) {
            case Received -> InPreparation;
            case InPreparation -> ReadyForDelivery;
            case ReadyForDelivery -> Underway;
            case Underway -> Delivered;
            case Delivered -> Disputed;
            case Disputed -> Resolved;
        };
    }
}