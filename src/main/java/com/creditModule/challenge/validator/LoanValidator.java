package com.creditModule.challenge.validator;

import com.creditModule.challenge.entities.Customer;
import com.creditModule.challenge.request.LoanRequest;

public interface LoanValidator {
	void validateLoanRequest(LoanRequest requestDTO, Customer customer);
}
