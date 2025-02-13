package com.creditModule.challenge.sevice;

import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.LoanInstallmentDTO;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.request.PayLoanRequest;
import com.creditModule.challenge.dto.PaidLoanDTO;

import java.util.List;

public interface LoanService {
	LoanDTO createLoan(LoanRequest request);

	List<LoanDTO> getLoansByCustomerId(Long customerId, Integer numberOfInstallment, Boolean isPaid);

	List<LoanInstallmentDTO> getInstallmentsByLoanId(Long loanId);

	PaidLoanDTO payLoan(PayLoanRequest request);
}
