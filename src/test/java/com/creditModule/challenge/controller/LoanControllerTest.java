package com.creditModule.challenge.controller;


import com.creditModule.challenge.dto.LoanDTO;
import com.creditModule.challenge.dto.LoanInstallmentDTO;
import com.creditModule.challenge.dto.PaidLoanDTO;
import com.creditModule.challenge.request.LoanRequest;
import com.creditModule.challenge.request.PayLoanRequest;
import com.creditModule.challenge.sevice.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

	@InjectMocks
	private LoanController loanController;

	@Mock
	private LoanService loanService;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
	}

	@Test
	void testCreateLoan_whenRequestValid_resultOk() throws Exception {
		//arrange
		LoanDTO loanDTO = LoanDTO.builder()
				.loanAmount(BigDecimal.valueOf(1100)).isPaid(false)
				.build();
		LoanRequest loanRequest = LoanRequest.builder()
				.loanAmount(BigDecimal.valueOf(1000))
				.interestRate(BigDecimal.valueOf(0.1))
				.numberOfInstallments(6)
				.customerId(1L)
				.build();
		Mockito.when(loanService.createLoan(any(LoanRequest.class))).thenReturn(loanDTO);

		//act assert
		mockMvc.perform(post("/api/loans/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loanRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(loanDTO.getId()))
				.andExpect(jsonPath("$.loanAmount").value(loanDTO.getLoanAmount()))
				.andExpect(jsonPath("$.paid").value(loanDTO.isPaid()));
	}

	@Test
	void testCreateLoan_whenRequestIsNotValid_resultBadRequest() throws Exception {
		//arrange
		LoanRequest loanRequest = new LoanRequest();

		//act assert
		mockMvc.perform(post("/api/loans/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loanRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testPayLoan_whenRequestValid_resultOk() throws Exception {
		//arrange
		PaidLoanDTO paidLoanDTO = PaidLoanDTO.builder()
				.totalAmountPaid(BigDecimal.valueOf(200)).loanFullyPaid(false)
				.installmentsPaid(2)
				.build();
		PayLoanRequest request = PayLoanRequest.builder()
				.loanId(1L)
				.amountToPay(BigDecimal.valueOf(1200))
				.build();
		Mockito.when(loanService.payLoan(any(PayLoanRequest.class))).thenReturn(paidLoanDTO);

		//act assert
		mockMvc.perform(post("/api/loans/pay")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.installmentsPaid").value(paidLoanDTO.getInstallmentsPaid()))
				.andExpect(jsonPath("$.totalAmountPaid").value(paidLoanDTO.getTotalAmountPaid()))
				.andExpect(jsonPath("$.loanFullyPaid").value(paidLoanDTO.isLoanFullyPaid()));
	}

	@Test
	void testPayLoan_whenRequestIsNotValid_resultBadRequest() throws Exception {
		//arrange
		PayLoanRequest loanRequest = new PayLoanRequest();

		//act assert
		mockMvc.perform(post("/api/loans/pay")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loanRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testGetLoansByCustomerId_whenRequestValid_returnOk() throws Exception {

		//arrange
		LoanDTO loanDTO = LoanDTO.builder()
				.loanAmount(BigDecimal.valueOf(1100)).isPaid(false)
				.build();
		List<LoanDTO> loans = List.of(loanDTO);
		Mockito.when(loanService.getLoansByCustomerId(any(), any(), any())).thenReturn(loans);

		//act assert
		mockMvc.perform(get("/api/loans/customer/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1));
	}

	@Test
	void testGetInstallmentByLoanId_whenRequestValid_returnOk() throws Exception {

		//arrange
		LoanInstallmentDTO loanInstallmentDTO = new LoanInstallmentDTO();
		List<LoanInstallmentDTO> loanInstallments = List.of(loanInstallmentDTO);
		Mockito.when(loanService.getInstallmentsByLoanId(any())).thenReturn(loanInstallments);

		//act assert
		mockMvc.perform(get("/api/loans/1/installments")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(1));
	}
}
