CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       user_name VARCHAR(50) NOT NULL,
                       user_surname VARCHAR(50) NOT NULL,
                       date_of_birth DATE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(60) NOT NULL,
                       gender VARCHAR(10) NOT NULL,
                       description TEXT,
                       photo_id INTEGER,
                       refresh_token VARCHAR(512) UNIQUE,
                       refresh_token_expiry TIMESTAMP
);

CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        description TEXT,
--                         time TIME NOT NULL,
--                         date DATE NOT NULL,
                        time TIMESTAMP WITH TIME ZONE,
                        city VARCHAR(100) NOT NULL,
                        max_amount_of_people INTEGER NOT NULL,
                        sport_type VARCHAR(50) NOT NULL,
                        creator_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE event_participants (
                                    event_id INTEGER REFERENCES events(id) ON DELETE CASCADE,
                                    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                                    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
                                    PRIMARY KEY (event_id, user_id)
);

CREATE TABLE notifications (
                               id SERIAL PRIMARY KEY,
                               user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               time TIMESTAMP DEFAULT NOW(),
                               type VARCHAR(50) NOT NULL,
                               event_id INTEGER REFERENCES events(id) ON DELETE CASCADE,
                               other_user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
                               is_read BOOLEAN DEFAULT FALSE
);

CREATE TABLE ratings (
                         id SERIAL PRIMARY KEY,
                         from_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         to_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         event_id INTEGER REFERENCES events(id) ON DELETE SET NULL,
                         rating DOUBLE PRECISION NOT NULL,
                         comment TEXT
);

CREATE TABLE friends (
                         user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         friend_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                         status VARCHAR(20) DEFAULT 'accepted',
                         created_at TIMESTAMP DEFAULT NOW(),
                         PRIMARY KEY (user_id, friend_id),
                         CHECK (user_id != friend_id)
    );