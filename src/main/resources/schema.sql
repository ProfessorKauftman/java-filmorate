
create TABLE IF NOT EXISTS users (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(70) NOT NULL,
    email VARCHAR(254) NOT NULL,
    login VARCHAR(70) NOT NULL,
    birthday DATE
);

create TABLE IF NOT EXISTS friendship (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    status BOOLEAN,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(70) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS mpa_rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(70) NOT NULL UNIQUE
);

create TABLE IF NOT EXISTS films (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(254) NOT NULL,
    description VARCHAR(254) NOT NULL,
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    rating_id INTEGER NOT NULL REFERENCES mpa_rating(rating_id) ON update CASCADE
);

create TABLE IF NOT EXISTS film_genre (
    film_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON delete CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS likes (
    user_id INTEGER NOT NULL,
    film_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON delete CASCADE
);
