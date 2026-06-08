CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         birth_date DATE NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       price INTEGER NOT NULL CHECK (price >= 0),
                       publication_status VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT chk_publication_status
                           CHECK (publication_status IN ('UNPUBLISHED', 'PUBLISHED'))
);

CREATE TABLE book_authors (
                              book_id BIGINT NOT NULL,
                              author_id BIGINT NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (book_id, author_id),
                              CONSTRAINT fk_book_authors_book
                                  FOREIGN KEY (book_id) REFERENCES books(id),
                              CONSTRAINT fk_book_authors_author
                                  FOREIGN KEY (author_id) REFERENCES authors(id)
);