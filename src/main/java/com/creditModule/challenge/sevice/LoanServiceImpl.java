package com.creditModule.challenge.sevice;

import com.creditModule.challenge.constant.Constants;
import com.creditModule.challenge.constant.ExceptionTypes;
import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.LoanInstallmentDTO;
import com.creditModule.challenge.entities.Customer;
import com.creditModule.challenge.entities.Loan;
import com.creditModule.challenge.entities.LoanInstallment;
import com.creditModule.challenge.exception.ResourceNotFoundException;
import com.creditModule.challenge.mapper.EntityMapper;
import com.creditModule.challenge.repository.CustomerRepository;
import com.creditModule.challenge.repository.LoanInstallmentRepository;
import com.creditModule.challenge.repository.LoanRepository;
import com.creditModule.challenge.repository.specification.LoanSpecification;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.request.PayLoanRequest;
import com.creditModule.challenge.dto.PaidLoanDTO;
import com.creditModule.challenge.util.SecurityUtil;
import com.creditModule.challenge.validator.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

	private final LoanRepository loanRepository;
	private final CustomerRepository customerRepository;
	private final EntityMapper entityMapper;
	private final LoanValidator loanValidator;
	private final LoanInstallmentRepository loanInstallmentRepository;

	@Override
	@Transactional
	public LoanDTO createLoan(LoanRequest request) {
		Customer customer = customerRepository
				.findById(request.getCustomerId())
				.orElseThrow(() -> new ResourceNotFoundException(String.format(
						ExceptionTypes.CUSTOMER_NOT_FOUND.getMessage(),
						request.getCustomerId())));

		loanValidator.validateLoanRequest(request, customer);

		BigDecimal totalLoanAmount = request.getLoanAmount().multiply(BigDecimal.ONE.add(request.getInterestRate()));
		Loan loan = Loan.builder()
				.customer(customer)
				.loanAmount(totalLoanAmount)
				.numberOfInstallment(request.getNumberOfInstallments())
				.createDate(LocalDate.now())
				.isPaid(false)
				.build();

		List<LoanInstallment> installments = generateInstallments(loan, totalLoanAmount, request.getNumberOfInstallments());
		loan.setInstallments(installments);
		Loan savedLoan = loanRepository.save(loan);

		customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
		customerRepository.save(customer);

		return entityMapper.toLoanDTO(savedLoan);
	}

	@Override
	public List<LoanDTO> getLoansByCustomerId(Long customerId, Integer numberOfInstallment, Boolean isPaid) {
		if (!SecurityUtil.isAuthorized(customerId)) {
			throw new AccessDeniedException(ExceptionTypes.NOT_AUTHORIZED_TO_CUSTOMER.getMessage());
		}

		Specification<Loan> spec = LoanSpecification.byCustomerIdAndFilters(customerId, numberOfInstallment, isPaid);
		List<Loan> loans = loanRepository.findAll(spec);
		return loans.stream()
				.map(entityMapper::toLoanDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<LoanInstallmentDTO> getInstallmentsByLoanId(Long loanId) {
		Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ExceptionTypes.LOAN_NOT_FOUND.getMessage(), loanId)));

		if (!SecurityUtil.isAuthorized(loan.getCustomer().getId())) {
			throw new AccessDeniedException(ExceptionTypes.NOT_AUTHORIZED_TO_CUSTOMER.getMessage());
		}

		List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loanId);
		return installments.stream()
				.map(entityMapper::toLoanInstallmentDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public PaidLoanDTO payLoan(PayLoanRequest request) {
		Loan loan = loanRepository.findById(request.getLoanId())
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ExceptionTypes.LOAN_NOT_FOUND.getMessage(), request.getLoanId())));

		if (!SecurityUtil.isAuthorized(loan.getCustomer().getId())) {
			throw new AccessDeniedException(ExceptionTypes.NOT_AUTHORIZED_TO_CUSTOMER.getMessage());
		}
		Customer customer = loan.getCustomer();
		List<LoanInstallment> installments = loanInstallmentRepository
				.findByLoanIdAndIsPaidOrderByDueDate(request.getLoanId(), false);

		List<LoanInstallment> paidInstallment = getPaidInstallment(installments, request.getAmountToPay());
		boolean loanFullyPaid = installments.size() == paidInstallment.size();
		BigDecimal totalPaid = BigDecimal.ZERO;
		if (!paidInstallment.isEmpty()) {
			loanInstallmentRepository.saveAll(paidInstallment);
			if (loanFullyPaid) {
				loan.setPaid(true);
				loanRepository.save(loan);
			}
			totalPaid = paidInstallment.stream()
					.map(LoanInstallment::getPaidAmount)
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(totalPaid));
			customerRepository.save(customer);
		}
		return PaidLoanDTO.builder()
				.loanFullyPaid(loanFullyPaid)
				.totalAmountPaid(totalPaid)
				.installmentsPaid(paidInstallment.size())
				.build();
	}

	private List<LoanInstallment> generateInstallments(Loan loan, BigDecimal totalAmount, Integer numberOfInstallments) {
		List<LoanInstallment> installments = new ArrayList<>();
		BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
		LocalDate firstDueDate = LocalDate.now().plusMonths(Constants.DEFAULT_DUE_DATE).withDayOfMonth(1);

		for (int i = 0; i < numberOfInstallments; i++) {
			LoanInstallment installment = LoanInstallment.builder()
					.loan(loan)
					.amount(installmentAmount)
					.paidAmount(BigDecimal.ZERO)
					.dueDate(firstDueDate.plusMonths(i))
					.isPaid(false)
					.build();
			installments.add(installment);
		}
		return installments;
	}

	private List<LoanInstallment> getPaidInstallment(List<LoanInstallment> installments, BigDecimal remainingAmount) {
		List<LoanInstallment> paidInstallment = new ArrayList<>();

		for (LoanInstallment installment : installments) {
			LocalDate threeCalenderDateAfter = LocalDate.now().withDayOfMonth(Constants.DEFAULT_DUE_DATE).plusMonths(Constants.MAX_ALLOWED_DUE_DATE);
			if (!installment.getDueDate().isBefore(threeCalenderDateAfter)) {
				break;
			}

			BigDecimal installmentAmount = getInstallmentAmount(installment);
			if (remainingAmount.compareTo(installmentAmount) < 0) {
				break;
			}

			installment.setPaidAmount(installmentAmount);
			installment.setPaid(true);
			installment.setPaymentDate(LocalDate.now());
			remainingAmount = remainingAmount.subtract(installmentAmount);
			paidInstallment.add(installment);
		}
		return paidInstallment;
	}

	private BigDecimal getInstallmentAmount(LoanInstallment installment) {
		long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), installment.getDueDate());
		if (daysDifference > 0) {
			return installment.getAmount()
					.subtract(installment.getAmount()
							.multiply(Constants.DISCOUNT_AMOUNT)
							.multiply(BigDecimal.valueOf(daysDifference)));
		} else if (daysDifference < 0) {
			return installment.getAmount()
					.add(installment.getAmount()
							.multiply(Constants.PENALTY_AMOUNT)
							.multiply(BigDecimal.valueOf(daysDifference)));
		}
		return installment.getPaidAmount();
	}
}
