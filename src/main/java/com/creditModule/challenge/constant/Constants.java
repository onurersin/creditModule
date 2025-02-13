package com.creditModule.challenge.constant;

import java.math.BigDecimal;
import java.util.List;

public class Constants {
	public static final List<Integer> VALID_INSTALLMENTS = List.of(6, 9, 12, 24);
	public static final BigDecimal LOWER_INTEREST_RATE = BigDecimal.valueOf(0.1d);
	public static final BigDecimal UPPER_INTEREST_RATE = BigDecimal.valueOf(0.5d);
	public static final Integer MAX_ALLOWED_DUE_DATE = 3;
	public static final Integer DEFAULT_DUE_DATE = 1;
	public static final BigDecimal PENALTY_AMOUNT = BigDecimal.valueOf(0.001d);
	public static final BigDecimal DISCOUNT_AMOUNT = BigDecimal.valueOf(0.001d);
	public static final String CLAIM_USER_ID = "userId";
	public static final String CLAIM_CUSTOMER_ID = "customerId";
	public static final String CLAIM_ROLE = "role";
	public static final String HEADER_AUTHORIZATION = "Authorization";
}
