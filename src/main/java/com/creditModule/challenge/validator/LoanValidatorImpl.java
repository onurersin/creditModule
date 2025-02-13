package com.creditModule.challenge.validator;

import com.creditModule.challenge.constant.Constants;
import com.creditModule.challenge.constant.ExceptionTypes;
import com.creditModule.challenge.entities.Customer;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.util.SecurityUtil;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class LoanValidatorImpl implements LoanValidator {

	@Override
	public void validateLoanRequest(LoanRequest request, Customer customer) {

		if (!SecurityUtil.isAuthorized(customer.getId())) {
			throw new AccessDeniedException(ExceptionTypes.NOT_AUTHORIZED_TO_CUSTOMER.getMessage());
		}

		if (!Constants.VALID_INSTALLMENTS.contains(request.getNumberOfInstallments())) {
			throw new ValidationException(ExceptionTypes.NUMBER_OF_INSTALLMENT_VALIDATION.getMessage());
		}

		if (request.getInterestRate().compareTo(Constants.LOWER_INTEREST_RATE) < 0
				|| request.getInterestRate().compareTo(Constants.UPPER_INTEREST_RATE) > 0) {
			throw new ValidationException(ExceptionTypes.INTEREST_RATE_VALIDATION.getMessage());
		}

		BigDecimal totalLoanCost = request.getLoanAmount()
				.add(request.getLoanAmount()
						.multiply(request.getInterestRate()));

		BigDecimal newUsedCredit = customer.getUsedCreditLimit().add(totalLoanCost);
		if (newUsedCredit.compareTo(customer.getCreditLimit()) > 0) {
			throw new ValidationException(ExceptionTypes.CREDIT_LIMIT_VALIDATION.getMessage());
		}
	}
}
