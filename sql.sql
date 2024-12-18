use

CREATE TABLE post
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      TEXT         NOT NULL,
    content    LONGTEXT     NOT NULL,
    thumbnail  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
);