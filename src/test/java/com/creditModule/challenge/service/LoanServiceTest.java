package com.creditModule.challenge.service;

import com.creditModule.challenge.constant.Constants;
import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.PaidLoanDTO;
import com.creditModule.challenge.entities.Customer;
import com.creditModule.challenge.entities.Loan;
import com.creditModule.challenge.entities.LoanInstallment;
import com.creditModule.challenge.exception.ResourceNotFoundException;
import com.creditModule.challenge.mapper.EntityMapper;
import com.creditModule.challenge.repository.CustomerRepository;
import com.creditModule.challenge.repository.LoanInstallmentRepository;
import com.creditModule.challenge.repository.LoanRepository;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.request.PayLoanRequest;
import com.creditModule.challenge.sevice.LoanServiceImpl;
import com.creditModule.challenge.util.SecurityUtil;
import com.creditModule.challenge.validator.LoanValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private EntityMapper entityMapper;

	@Mock
	private LoanValidator loanValidator;

	@Mock
	private LoanInstallmentRepository loanInstallmentRepository;

	@InjectMocks
	private LoanServiceImpl loanService;

	private MockedStatic<SecurityUtil> securityUtilMock;

	private static final Long ID = 1L;
	private static final Long CUSTOMER_ID = 1L;
	private static final BigDecimal LOAN_AMOUNT = BigDecimal.valueOf(1000);
	private static final BigDecimal INTEREST_RATE = BigDecimal.valueOf(0.2);
	private static final Integer INSTALLMENT_NUMBER = 6;
	private static final BigDecimal CREDIT_LIMIT = BigDecimal.valueOf(10000);
	private static final BigDecimal USED_CREDIT_LIMIT = BigDecimal.valueOf(6000);

	@BeforeEach
	void setUp() {
		securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
	}

	@AfterEach
	void tearDown() {
		securityUtilMock.close();
	}

	@Test
	public void testCreateLoan_whenCustomerNotFound_shouldThrowException() {
		//arrange
		LoanRequest request = LoanRequest.builder().customerId(CUSTOMER_ID).build();
		when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

		//act assert
		assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(request));
	}

	@Test
	public void testCreateLoan_whenValid_shouldReturnLoanDTO() {
		//arrange
		LoanRequest request = LoanRequest.builder()
				.customerId(CUSTOMER_ID)
				.loanAmount(LOAN_AMOUNT)
				.interestRate(INTEREST_RATE)
				.numberOfInstallments(INSTALLMENT_NUMBER)
				.build();
		Customer customer = Customer.builder()
				.id(CUSTOMER_ID)
				.usedCreditLimit(USED_CREDIT_LIMIT)
				.creditLimit(CREDIT_LIMIT)
				.build();
		when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
		doNothing().when(loanValidator).validateLoanRequest(any(), any());
		when(loanRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		//act
		loanService.createLoan(request);

		//assert
		ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
		verify(entityMapper).toLoanDTO(loanCaptor.capture());
		Loan capturedLoan = loanCaptor.getValue();
		assertEquals(Boolean.FALSE, capturedLoan.isPaid());
		assertEquals(INSTALLMENT_NUMBER, capturedLoan.getInstallments().size());
		assertEquals(LOAN_AMOUNT.multiply(BigDecimal.ONE.add(INTEREST_RATE)), capturedLoan.getLoanAmount());
		verify(loanRepository, times(1)).save(any(Loan.class));
		verify(customerRepository, times(1)).save(any(Customer.class));
	}

	@Test
	public void testGetLoansByCustomerId_whenCustomerNotAuthorized_shouldThrowsException() {
		//arrange
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(anyLong())).thenReturn(false);

		//act assert
		assertThrows(AccessDeniedException.class, () -> loanService.getLoansByCustomerId(CUSTOMER_ID, 6, Boolean.FALSE));
	}

	@Test
	public void testGetLoansByCustomerId_whenCustomerNotAuthorized_shouldReturnLoanDto() {
		//arrange
		Loan loan = Loan.builder()
				.id(ID)
				.loanAmount(LOAN_AMOUNT)
				.numberOfInstallment(INSTALLMENT_NUMBER)
				.isPaid(false)
				.build();
		List<Loan> loanList = List.of(loan);
		LoanDTO loanDTO = LoanDTO.builder()
				.loanAmount(LOAN_AMOUNT)
				.numberOfInstallment(INSTALLMENT_NUMBER)
				.build();
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(anyLong())).thenReturn(true);
		when(loanRepository.findAll(any(Specification.class))).thenReturn(loanList);
		when(entityMapper.toLoanDTO(any(Loan.class))).thenReturn(loanDTO);
		//act
		 List<LoanDTO> loanDTOList = loanService.getLoansByCustomerId(CUSTOMER_ID, 6, Boolean.FALSE);

		 //assert
		 assertEquals(LOAN_AMOUNT, loanDTOList.get(0).getLoanAmount());
		 assertEquals(INSTALLMENT_NUMBER, loanDTOList.get(0).getNumberOfInstallment());
		 assertEquals(1, loanDTOList.size());

		 verify(entityMapper, times(loanDTOList.size())).toLoanDTO(any(Loan.class));
	}

	@Test
	public void testGetInstallmentsByLoanId_whenLoanNotFound_shouldThrowException() {
		//arrange
		when(loanRepository.findById(anyLong())).thenReturn(Optional.empty());

		//act assert
		assertThrows(ResourceNotFoundException.class, () -> loanService.getInstallmentsByLoanId(1L));
	}

	@Test
	public void testGetInstallmentsByLoanId_whenNotAuthorized_shouldThrowsException() {
		//arrange
		Loan loan = new Loan();
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		loan.setCustomer(customer);
		when(loanRepository.findById(anyLong())).thenReturn(Optional.of(loan));
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(anyLong())).thenReturn(false);

		//act assert
		assertThrows(AccessDeniedException.class, () -> loanService.getInstallmentsByLoanId(ID));
	}

	@Test
	public void testPayLoan_whenLoanNotFound_shouldThrowException() {
		//arrange
		PayLoanRequest payLoanRequest = PayLoanRequest.builder().loanId(ID).build();
		when(loanRepository.findById(anyLong())).thenReturn(Optional.empty());

		//act assert
		assertThrows(ResourceNotFoundException.class, () -> loanService.payLoan(payLoanRequest));
	}

	@Test
	public void testPayLoan_whenNotAuthorized_shouldThrowsException() {
		//arrange
		PayLoanRequest payLoanRequest = PayLoanRequest.builder().loanId(ID).build();
		Loan loan = new Loan();
		Customer customer = new Customer();
		customer.setId(CUSTOMER_ID);
		loan.setCustomer(customer);
		when(loanRepository.findById(anyLong())).thenReturn(Optional.of(loan));
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(anyLong())).thenReturn(false);

		//act assert
		assertThrows(AccessDeniedException.class, () -> loanService.payLoan(payLoanRequest));
	}

	@Test
	public void testPayLoan_whenValid_shouldReturnDto(){
		//arrange
		PayLoanRequest payLoanRequest = PayLoanRequest.builder().loanId(ID)
				.amountToPay(LOAN_AMOUNT.multiply(BigDecimal.valueOf(2)))
				.build();
		Loan loan = new Loan();
		Customer customer = new Customer();
		customer.setUsedCreditLimit(USED_CREDIT_LIMIT);
		customer.setId(CUSTOMER_ID);
		loan.setCustomer(customer);
		when(loanRepository.findById(anyLong())).thenReturn(Optional.of(loan));
		securityUtilMock.when(() -> SecurityUtil.isAuthorized(anyLong())).thenReturn(true);
		LoanInstallment loanInstallment = LoanInstallment.builder()
				.amount(LOAN_AMOUNT)
				.dueDate(LocalDate.now().withDayOfMonth(Constants.DEFAULT_DUE_DATE).plusMonths(1))
				.isPaid(true)
				.build();
		when(loanInstallmentRepository.findByLoanIdAndIsPaidOrderByDueDate(any(),anyBoolean())).thenReturn(List.of(loanInstallment));
		//act
		PaidLoanDTO paidLoanDTO = loanService.payLoan(payLoanRequest);

		//assert
		assertTrue(paidLoanDTO.isLoanFullyPaid());
		assertEquals(1, paidLoanDTO.getInstallmentsPaid());
	}
}
