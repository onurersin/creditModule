package com.creditModule.challenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class LoanInstallmentDTO {
	private Long id;
	private Long loanId;
	private BigDecimal amount;
	private BigDecimal paidAmount;
	private LocalDate dueDate;
	private LocalDate paymentDate;
	private boolean isPaid;
}
