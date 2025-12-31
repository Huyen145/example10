package com.nguyenthithuhuyen.example10.payload.request;

import com.nguyenthithuhuyen.example10.entity.enums.PaymentMethod;
import lombok.Data;

@Data
public class BillRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private String note;
}
