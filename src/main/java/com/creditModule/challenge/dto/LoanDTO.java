package com.creditModule.challenge.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDTO {
	private Long id;
	private Long customerId;
	private BigDecimal loanAmount;
	private Integer numberOfInstallment;
	private LocalDate createDate;
	private boolean isPaid;
	private List<LoanInstallmentDTO> installments;
}
