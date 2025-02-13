
DROP TABLE IF EXISTS "user";


CREATE TABLE "customer" (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            surname VARCHAR(255) NOT NULL,
                            credit_limit DECIMAL(15,2) NOT NULL DEFAULT 0,
                            used_credit_limit DECIMAL(15,2) NOT NULL DEFAULT 0
);

CREATE TABLE "loan" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        loan_amount DECIMAL(15,2) NOT NULL,
                        number_of_installment INT NOT NULL,
                        create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        is_paid BOOLEAN DEFAULT FALSE
);

CREATE TABLE "loan_installment" (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    loan_id BIGINT NOT NULL,
                                    amount DECIMAL(15,2) NOT NULL,
                                    paid_amount DECIMAL(15,2) DEFAULT 0,
                                    due_date DATE NOT NULL,
                                    payment_date DATE NULL,
                                    is_paid BOOLEAN DEFAULT FALSE
);

CREATE TABLE "user" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        create_date DATE NOT NULL,
                        update_date DATE NOT NULL
);


INSERT INTO "user"  (id, customer_id, role, password, create_date, update_date) VALUES
                                                                                    (1, 101, 'ADMIN', '$2a$12$xgyCFASr99gBu5qP2NUlr.KT3sKkkLLi7otT9wO9BUw61YhT2hXhq', '2025-02-10', '2025-02-10'),
                                                                                    (2, 102, 'ADMIN', '$2a$12$xgyCFASr99gBu5qP2NUlr.KT3sKkkLLi7otT9wO9BUw61YhT2hXhq', '2025-02-10', '2025-02-10'),
                                                                                    (3, 103, 'CUSTOMER', '$2a$12$mw/N02Re1eR6/LMtDgUOs.g5XQEE1hgrQqSGrOJLuS7LKZ..yQEW.', '2025-02-10', '2025-02-10'),
                                                                                    (4, 104, 'CUSTOMER', '$2a$12$mw/N02Re1eR6/LMtDgUOs.g5XQEE1hgrQqSGrOJLuS7LKZ..yQEW.', '2025-02-10', '2025-02-10');

INSERT INTO "customer" (id, name, surname, credit_limit, used_credit_limit)
VALUES (103, 'David', 'Williams', 150000, 50000);

INSERT INTO "customer" (id, name, surname, credit_limit, used_credit_limit)
VALUES (104, 'Michael', 'Brown', 350000, 60000);