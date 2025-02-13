package com.creditModule.challenge.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayLoanRequest {
	@NotNull(message = "loan ID cannot be null")
	private Long loanId;

	@NotNull(message = "amount to pay cannot be null")
	private BigDecimal amountToPay;
}
