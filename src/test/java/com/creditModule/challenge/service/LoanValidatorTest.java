package com.creditModule.challenge.service;

import com.creditModule.challenge.entities.Customer;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.util.SecurityUtil;
import com.creditModule.challenge.validator.LoanValidatorImpl;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LoanValidatorTest {

	private LoanValidatorImpl loanValidator;
	private MockedStatic<SecurityUtil> securityUtilMock;

	private static final Integer INVALID_INSTALLMENT_NUMBER = 15;
	private static final Integer VALID_INSTALLMENT_NUMBER = 6;
	private static final BigDecimal INVALID_INTEREST_RATE = BigDecimal.valueOf(0.01);
	private static final BigDecimal VALID_INTEREST_RATE = BigDecimal.valueOf(0.2);
	private static final BigDecimal LOAN_AMOUNT = BigDecimal.valueOf(1000);
	private static final BigDecimal CREDIT_LIMIT = BigDecimal.valueOf(10000);
	private static final BigDecimal USED_CREDIT_LIMIT = BigDecimal.valueOf(9000);
	private static final Long CUSTOMER_ID = 1L;

	@BeforeEach
	void setUp() {
		loanValidator = new LoanValidatorImpl();
		securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
	}

	@AfterEach
	void tearDown() {
		securityUtilMock.close();
	}

	@Test
	public void testValidateLoanRequest_whenCustomerNotAuthorized_shouldThrowException() {
		//arrange
		LoanRequest request = new LoanRequest();
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(customer.getId())).thenReturn(false);

		//act assert
		assertThrows(AccessDeniedException.class, () -> loanValidator.validateLoanRequest(request, customer));

	}

	@Test
	public void testValidateLoanRequest_whenInvalidInstallments_shouldThrowException() {
		//arrange
		LoanRequest request = new LoanRequest();
		request.setNumberOfInstallments(INVALID_INSTALLMENT_NUMBER);
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(customer.getId())).thenReturn(true);

		//act assert
		assertThrows(ValidationException.class, () -> loanValidator.validateLoanRequest(request, customer));

	}

	@Test
	public void testValidateLoanRequest_whenInvalidInterestRate_shouldThrowException() {
		//arrange
		LoanRequest request = new LoanRequest();
		request.setNumberOfInstallments(VALID_INSTALLMENT_NUMBER);
		request.setInterestRate(INVALID_INTEREST_RATE);
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(customer.getId())).thenReturn(true);

		//act assert
		assertThrows(ValidationException.class, () -> loanValidator.validateLoanRequest(request, customer));

	}

	@Test
	public void testValidateLoanRequest_whenInvalidCreditLimit_shouldThrowException() {
		//arrange
		LoanRequest request = new LoanRequest();
		request.setLoanAmount(LOAN_AMOUNT);
		request.setNumberOfInstallments(VALID_INSTALLMENT_NUMBER);
		request.setInterestRate(VALID_INTEREST_RATE);
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		customer.setUsedCreditLimit(USED_CREDIT_LIMIT);
	    customer.setCreditLimit(CREDIT_LIMIT);
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(customer.getId())).thenReturn(true);

		//act assert
		assertThrows(ValidationException.class, () -> loanValidator.validateLoanRequest(request, customer));

	}

	@Test
	public void testValidateLoanRequest_whenValid_doNothing() {
		//arrange
		LoanRequest request = new LoanRequest();
		request.setLoanAmount(LOAN_AMOUNT);
		request.setNumberOfInstallments(VALID_INSTALLMENT_NUMBER);
		request.setInterestRate(VALID_INTEREST_RATE);
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		customer.setUsedCreditLimit(USED_CREDIT_LIMIT);
		customer.setCreditLimit(CREDIT_LIMIT.multiply(BigDecimal.valueOf(2)));
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(customer.getId())).thenReturn(true);

		//act assert
		assertDoesNotThrow(() -> loanValidator.validateLoanRequest(request, customer));
	}
}
