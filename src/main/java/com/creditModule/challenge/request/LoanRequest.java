package com.creditModule.challenge.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequest {

	@NotNull(message = "Customer ID cannot be null")
	private Long customerId;

	@NotNull(message = "Loan amount cannot be null")
	private BigDecimal loanAmount;

	@NotNull(message = "Interest rate cannot be null")
	private BigDecimal interestRate;

	@NotNull(message = "Number of installments cannot be null")
	private Integer numberOfInstallments;
}
