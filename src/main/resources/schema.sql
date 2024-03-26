
CREATE TABLE IF NOT EXISTS Receipt (
                                       receipt_id SERIAL PRIMARY KEY,
                                       description VARCHAR NOT NULL,
                                       date_of_purchase TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS App_User (
                                       user_id SERIAL PRIMARY KEY,
                                       user_name VARCHAR NOT NULL,
                                       login VARCHAR NOT NULL,
                                       password VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS Image (
                                        image_id SERIAL PRIMARY KEY,
                                        name VARCHAR NOT NULL,
                                        receipt_id INT,
                                        CONSTRAINT fk_receipt
                                            FOREIGN KEY(receipt_id)
                                                REFERENCES receipt(receipt_id)
);

CREATE TABLE IF NOT EXISTS Item (
                                        item_id SERIAL PRIMARY KEY,
                                        receipt_id INT,
                                        name VARCHAR NOT NULL,
                                        total_cost INT NOT NULL,
                                        quantity INT NOT NULL,
                                        CONSTRAINT fk_receipt
                                            FOREIGN KEY(receipt_id)
                                                REFERENCES receipt(receipt_id)
);