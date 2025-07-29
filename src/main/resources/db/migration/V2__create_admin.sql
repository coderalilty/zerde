--
-- Дамп данных таблицы `users`
--

INSERT IGNORE INTO `users` (`id`, `authorities`, `email`, `name`, `password`,`password_temporary`) VALUES
    (1, 'ADMIN', 'admin@gmail.com', 'admin', '$2a$10$h/EXE0cJ8xvfpKcvqxWbMufEjtUT2QEtVRSd3H1LR8rv5kJkA6vnq',false);

--
-- Индексы сохранённых таблиц
--
