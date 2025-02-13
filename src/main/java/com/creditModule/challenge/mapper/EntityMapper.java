package com.creditModule.challenge.mapper;

import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.LoanInstallmentDTO;
import com.creditModule.challenge.entities.Loan;
import com.creditModule.challenge.entities.LoanInstallment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntityMapper {

	@Mapping(source = "customer.id", target = "customerId")
	LoanDTO toLoanDTO(Loan loan);

	@Mapping(source = "loan.id", target = "loanId")
	LoanInstallmentDTO toLoanInstallmentDTO(LoanInstallment installment);
}
