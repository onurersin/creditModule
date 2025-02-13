package com.creditModule.challenge.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "\"loan\"") // Escaping the reserved keyword
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "customerId", nullable = false)
	private Customer customer;

	@Column(nullable = false)
	private BigDecimal loanAmount;

	@Column(nullable = false)
	private Integer numberOfInstallment;

	@Column(nullable = false, updatable = false)
	private LocalDate createDate;

	@Column(nullable = false)
	private boolean isPaid;

	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
	private List<LoanInstallment> installments;

}
