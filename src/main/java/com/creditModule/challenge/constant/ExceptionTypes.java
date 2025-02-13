package com.creditModule.challenge.constant;

import lombok.Getter;

@Getter
public enum ExceptionTypes {
	CUSTOMER_NOT_FOUND("Customer with ID %s not found."),
	LOAN_NOT_FOUND("Loan not found for ID: %s."),
	USER_NOT_FOUND("User with ID %s not found."),
	NOT_AUTHORIZED_TO_CUSTOMER("You are not authorized to access this operation."),
	INVALID_CREDENTIALS("Invalid credentials"),
	NUMBER_OF_INSTALLMENT_VALIDATION("Number of installments can only be 6, 9, 12, 24."),
	INTEREST_RATE_VALIDATION("Interest rate can be between 0.1 – 0.5."),
	CREDIT_LIMIT_VALIDATION("Customer does not have enough credit limit for this loan.");

	private final String message;

	ExceptionTypes(String message) {
		this.message = message;
	}
}

