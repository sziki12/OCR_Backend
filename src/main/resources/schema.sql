DROP TABLE IF EXISTS Items;
DROP TABLE IF EXISTS Receipts;

CREATE TABLE IF NOT EXISTS receipts (
    receipt_id SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    date_of_purchase TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    item_id SERIAL PRIMARY KEY,
    receipt_id INT,
    name VARCHAR NOT NULL,
    total_cost INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_receipt
        FOREIGN KEY(receipt_id)
            REFERENCES receipts(receipt_id)
);