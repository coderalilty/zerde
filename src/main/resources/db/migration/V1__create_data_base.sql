--
-- Структура таблицы `children`
--

CREATE TABLE IF NOT EXISTS `children` (
                                          `id` int auto_increment primary key,
                                          `age` int DEFAULT NULL,
                                          `first_name` varchar(255) DEFAULT NULL,
                                          `last_name` varchar(255) DEFAULT NULL,
                                          `middle_name` varchar(255) DEFAULT NULL,
                                          `diagnoses_id` int DEFAULT NULL,
                                          `app_groups_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `diagnoses`
--

CREATE TABLE IF NOT EXISTS `diagnoses` (
                                           `id` int auto_increment primary key,
                                           `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `lessons`
--

CREATE TABLE IF NOT EXISTS `lessons` (
                                         `id` int auto_increment primary key,
                                         `create_date` varchar(255) DEFAULT NULL,
                                         `lesson_name` varchar(255) DEFAULT NULL,
                                         `lesson_day` varchar(255) DEFAULT NULL,
                                         `group_type` varchar(255) DEFAULT NULL,
                                         `lesson_status` enum('CANCELLED','COMPLETED','RESERVED','SCHEDULED','EDITED','TEMPORARY') DEFAULT NULL,
                                         `lesson_type` enum('PERMANENT','TRIAL') DEFAULT NULL,
                                         `lesson_mark` enum('CAME','NOTCOME') DEFAULT NULL,
                                         `lesson_mark_2` enum('FORaREASON','WITHOUTaREASON') DEFAULT NULL,
                                         `update_date` varchar(255) DEFAULT NULL,
                                         `rooms_id` int DEFAULT NULL,
                                         `subjects_id` int DEFAULT NULL,
                                         `users_id` int DEFAULT NULL,
                                         `app_groups_id` int DEFAULT NULL,
                                         `document_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------
--
-- Структура таблицы `rooms`
--

CREATE TABLE IF NOT EXISTS `rooms` (
                                       `id` int auto_increment primary key,
                                       `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `subjects`
--

CREATE TABLE IF NOT EXISTS `subjects` (
                                          `id` int auto_increment primary key,
                                          `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `information`
--
CREATE TABLE IF NOT EXISTS `information` (
                                             `id` int auto_increment primary key,
                                             `text` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `teacher_subjects`
--

CREATE TABLE IF NOT EXISTS `teacher_subjects` (
                                                  `users_id` int auto_increment primary key,
                                                  `subjects_id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--
CREATE TABLE IF NOT EXISTS `users` (
                                       `id` int auto_increment primary key,
                                       `authorities` enum('ADMIN','TEACHER','USER') DEFAULT NULL,
                                       `email` varchar(255) DEFAULT NULL,
                                       `name` varchar(255) DEFAULT NULL,
                                       `surname` varchar(255) DEFAULT NULL,
                                       `lastname` varchar(255) DEFAULT NULL,
                                       `password` varchar(255) DEFAULT NULL,
                                       `phone` varchar(255) DEFAULT NULL,
                                       `password_temporary` BOOLEAN DEFAULT FALSE,
                                       `chat_id` BIGINT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `app_groups` (
                                       `id` int auto_increment primary key,
                                       `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS locked_slots (
                                            id int auto_increment primary key,
                                            lesson_day varchar(255) NOT NULL,
                                            room_name varchar(255) NOT NULL,
                                            locked_from varchar(255) NOT NULL,
                                            locked_to varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS tasks (
                                            id int auto_increment primary key,
                                            task_name varchar(255) NOT NULL,
                                            youtube_url varchar(255) NOT NULL,
                                            task_text varchar(255) NOT NULL,
                                            task_photo_url varchar(255) NOT NULL,
                                            task_audio varchar(255) NOT NULL,
                                            subjects_id int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE IF NOT EXISTS documents (
                                            id int auto_increment primary key,
                                            document_name varchar(255) DEFAULT NULL,
                                            file_path varchar(255) DEFAULT NULL,
                                            upload_date datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;