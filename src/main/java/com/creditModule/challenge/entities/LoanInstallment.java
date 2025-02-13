package com.creditModule.challenge.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "\"loan_installment\"") // Escaping the reserved keyword
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanInstallment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "loan_id", nullable = false)
	private Loan loan;

	@Column(nullable = false)
	private BigDecimal amount;

	private BigDecimal paidAmount;

	@Column(nullable = false)
	private LocalDate dueDate;

	private LocalDate paymentDate;

	private boolean isPaid;
}
