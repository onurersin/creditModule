package com.creditModule.challenge.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "\"user\"") // Escaping the reserved keyword
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long customerId;

	@Column(nullable = false)
	private String role;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private LocalDate createDate;

	@Column(nullable = false)
	private LocalDate updateDate;

}
