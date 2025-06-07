CREATE TABLE IF NOT EXISTS locked_slots (
            id int auto_increment primary key,
            room_name varchar(255) NOT NULL,
            locked_from varchar(255) NOT NULL,
            locked_to varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
