package com.creditModule.challenge.repository;

import com.creditModule.challenge.entities.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
	List<LoanInstallment> findByLoanIdAndIsPaidOrderByDueDate(Long loanId, boolean isPaid);

	List<LoanInstallment> findByLoanId(Long loanId);
}
