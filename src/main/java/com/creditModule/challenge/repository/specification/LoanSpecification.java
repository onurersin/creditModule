package com.creditModule.challenge.repository.specification;

import com.creditModule.challenge.entities.Loan;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecification {

	private static final String CUSTOMER = "customer";
	private static final String ID = "id";
	private static final String NUMBER_OF_INSTALLMENT = "numberOfInstallment";
	private static final String IS_PAID = "isPaid";

	public static Specification<Loan> byCustomerIdAndFilters(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
		return (Root<Loan> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			Predicate predicate = cb.equal(root.get(CUSTOMER).get(ID), customerId);

			if (numberOfInstallments != null) {
				predicate = cb.and(predicate, cb.equal(root.get(NUMBER_OF_INSTALLMENT), numberOfInstallments));
			}

			if (isPaid != null) {
				predicate = cb.and(predicate, cb.equal(root.get(IS_PAID), isPaid));
			}

			return predicate;
		};
	}
}
