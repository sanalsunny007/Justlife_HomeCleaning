CREATE TABLE IF NOT EXISTS vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cleaner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    work_start TIME NOT NULL,
    work_end TIME NOT NULL,
    vehicle_id BIGINT,
    CONSTRAINT fk_cleaner_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date_time DATETIME NOT NULL,
    end_date_time DATETIME NOT NULL,
    duration_hours INT NOT NULL,
    required_cleaner_count INT NOT NULL,
    customer_name VARCHAR(255),
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS booking_cleaner (
    booking_id BIGINT,
    cleaner_id BIGINT,
    PRIMARY KEY (booking_id, cleaner_id),
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_cleaner FOREIGN KEY (cleaner_id) REFERENCES cleaner(id)
);
