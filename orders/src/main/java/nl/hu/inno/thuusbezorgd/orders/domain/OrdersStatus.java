package nl.hu.inno.thuusbezorgd.orders.domain;

public enum OrdersStatus {
    Received,
    InPreparation,
    ReadyForDelivery,
    Underway,
    Delivered,
    Disputed,
    Resolved;

    public OrdersStatus next() {
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