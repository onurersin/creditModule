package com.creditModule.challenge.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaidLoanDTO {
	private int installmentsPaid;
	private BigDecimal totalAmountPaid;
	private boolean loanFullyPaid;
}
