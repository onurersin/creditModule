package com.creditModule.challenge.controller;

import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.LoanInstallmentDTO;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.request.PayLoanRequest;
import com.creditModule.challenge.dto.PaidLoanDTO;
import com.creditModule.challenge.sevice.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@AllArgsConstructor
public class LoanController {

	private final LoanService loanService;

	@PostMapping("/create")
	public ResponseEntity<LoanDTO> createLoan(@Valid @RequestBody LoanRequest request) {
		LoanDTO createdLoan = loanService.createLoan(request);
		return ResponseEntity.ok(createdLoan);
	}

	@PostMapping("/pay")
	public ResponseEntity<PaidLoanDTO> payLoan(@Valid @RequestBody PayLoanRequest request) {
		PaidLoanDTO response = loanService.payLoan(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<LoanDTO>> getLoansByCustomerId(@PathVariable Long customerId,
															  @RequestParam(required = false) Integer numberOfInstallment,
															  @RequestParam(required = false) Boolean isPaid) {
		List<LoanDTO> loans = loanService.getLoansByCustomerId(customerId, numberOfInstallment, isPaid);
		return ResponseEntity.ok(loans);
	}

	@GetMapping("/{loanId}/installments")
	public ResponseEntity<List<LoanInstallmentDTO>> getInstallmentsByLoanId(@PathVariable Long loanId) {
		List<LoanInstallmentDTO> installments = loanService.getInstallmentsByLoanId(loanId);
		return ResponseEntity.ok(installments);
	}
}
