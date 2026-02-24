package com.thiago.leiloa_api.domain.notification;

public final class NotificationTypeCodes {

    public static final String USER_REGISTERED = "USER_REGISTERED";
    public static final String AUCTION_PUBLISHED = "AUCTION_PUBLISHED";
    public static final String NEW_BID = "NEW_BID";
    public static final String OUT_BID = "OUT_BID";
    public static final String TIME_REMAINING = "TIME_REMAINING";
    public static final String AUCTION_FINISHED = "AUCTION_FINISHED";

    public static final String PAYMENT_CREATED = "PAYMENT_CREATED";
    public static final String PAYMENT_APPROVED = "PAYMENT_APPROVED";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String PAYMENT_CANCELLED = "PAYMENT_CANCELLED";
    public static final String PAYMENT_EXPIRED = "PAYMENT_EXPIRED";

    private NotificationTypeCodes() {}
}