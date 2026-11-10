DROP SCHEMA IF EXISTS libis;

CREATE SCHEMA libis;

USE libis;

-- =====================================
-- MEMBERS TABLE
-- =====================================

CREATE TABLE members (
                         member_id INT AUTO_INCREMENT PRIMARY KEY,
                         member_number VARCHAR(20) NOT NULL UNIQUE,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         first_name VARCHAR(50) NOT NULL,
                         last_name VARCHAR(50) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         phone_number VARCHAR(20),
                         password VARCHAR(100) NOT NULL,
                         role VARCHAR(20) NOT NULL,
                         CONSTRAINT chk_member_role
                             CHECK (role IN ('member', 'librarian'))
);

-- =====================================
-- BOOKS TABLE
-- =====================================

CREATE TABLE books (
                       book_id INT AUTO_INCREMENT PRIMARY KEY,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       title VARCHAR(150) NOT NULL,
                       author VARCHAR(100) NOT NULL,
                       genre VARCHAR(50) NOT NULL,
                       synopsis TEXT NULL
);

-- =====================================
-- LOANS TABLE
-- =====================================

CREATE TABLE loans (
                       loan_id INT AUTO_INCREMENT PRIMARY KEY,
                       member_id INT NOT NULL,
                       book_id INT NOT NULL,
                       borrowed_on DATE NOT NULL,
                       returned_on DATE NULL,

                       CONSTRAINT fk_loans_member
                           FOREIGN KEY (member_id)
                               REFERENCES members(member_id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_loans_book
                           FOREIGN KEY (book_id)
                               REFERENCES books(book_id)
                               ON DELETE CASCADE
);

SELECT * FROM members;


USE libis;

INSERT INTO members (
    member_number,
    username,
    first_name,
    last_name,
    email,
    phone_number,
    password,
    role
)
VALUES
    ('Lib-1001', 'Kaelo', 'Kaelo', 'Selome', 'Kaelo@gmail.com', '+26773442232', 'Kaelo12345', 'member'),
    ('Lib-1002', 'Tom', 'Tom', 'Doe', 'Tom@gmail.com', '+26774658897', 'password', 'member'),
    ('Lib-1003', 'John', 'John', 'Doe', 'John@gmail.com', '+26779885644', 'password', 'member'),
    ('Lib-1004', 'Bob', 'Bob', 'Doe', 'Bob@gmail.com', '+26778332243', 'password', 'member'),
    ('Lib-1005', 'R_Doe', 'Rasta', 'Doe', 'Rasta@gmail.com', '+26771232345', 'password', 'member');

-- Verify inserted records
SELECT * FROM members;



USE libis;

INSERT INTO books (
    isbn,
    title,
    author,
    genre,
    synopsis
)
VALUES
    (
        '978-0-000-00001-1',
        '48 Laws of Power',
        'Robert Greene',
        'Self-Help',
        'A guide to power dynamics drawn from history, strategy and philosophy.'
    ),
    (
        '978-0-000-00002-2',
        '1984',
        'George Orwell',
        'Dystopian Fiction',
        'A man struggles against a totalitarian state that watches and controls everything.'
    ),
    (
        '978-0-000-00003-3',
        'The Catcher in the Rye',
        'J.D. Salinger',
        'Fiction',
        'A teenager drifts through New York after leaving his prep school.'
    ),
    (
        '978-0-000-00004-4',
        'The Great Gatsby',
        'F. Scott Fitzgerald',
        'Classic',
        'A mysterious millionaire chases a lost love in Jazz Age Long Island.'
    ),
    (
        '978-0-000-00005-5',
        'The Hobbit',
        'J.R.R. Tolkien',
        'Fantasy',
        'A reluctant hobbit joins a band of dwarves on a quest to reclaim a treasure from a dragon.'
    ),
    (
        '978-0-000-00006-6',
        'Fahrenheit 451',
        'Ray Bradbury',
        'Dystopian Fiction',
        'In a future where books are burned, a fireman begins to question his job.'
    ),
    (
        '978-0-000-00007-7',
        'The Alchemist',
        'Paulo Coelho',
        'Fiction',
        'A shepherd boy travels from Spain to Egypt in search of treasure and his personal legend.'
    ),
    (
        '978-0-000-00008-8',
        'Brave New World',
        'Aldous Huxley',
        'Dystopian Fiction',
        'A engineered society trades freedom for comfort and stability.'
    ),
    (
        '978-0-000-00009-9',
        'Clean Code',
        'Robert C. Martin',
        'Software Engineering',
        'A handbook on writing readable, maintainable code.'
    ),
    (
        '978-0-000-00010-0',
        'To Kill a Mockingbird',
        'Harper Lee',
        'Classic',
        'A lawyer defends a wrongly accused man in the racially divided American South.'
    );

-- Verify inserted books
SELECT * FROM books
ORDER BY book_id;

USE libis;

INSERT INTO loans (
    member_id,
    book_id,
    borrowed_on,
    returned_on
)
VALUES
-- Kaelo: 3 open loans
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1001'),
    1, '2026-10-12', NULL
),
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1001'),
    3, '2026-10-15', NULL
),
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1001'),
    5, '2026-10-18', NULL
),

-- Tom: 1 returned, 1 open
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1002'),
    2, '2026-10-01', '2026-10-14'
),
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1002'),
    4, '2026-10-20', NULL
),

-- John: 1 returned loan
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1003'),
    6, '2026-10-08', '2026-10-22'
),

-- Bob: 1 open loan
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1004'),
    7, '2026-10-25', NULL
),

-- Rasta: 1 returned loan
(
    (SELECT member_id FROM members
     WHERE member_number = 'Lib-1005'),
    9, '2026-10-05', '2026-10-19'
);

-- Verify inserted loans
SELECT
    l.loan_id,
    m.member_number,
    m.username,
    b.title,
    l.borrowed_on,
    l.returned_on,
    CASE
        WHEN l.returned_on IS NULL THEN 'Open'
        ELSE 'Returned'
        END AS status
FROM loans l
         JOIN members m ON l.member_id = m.member_id
         JOIN books b ON l.book_id = b.book_id
ORDER BY l.loan_id;


select * from loans;


SELECT COUNT(*) FROM members;
SELECT COUNT(*) FROM books;
SELECT COUNT(*) FROM loans;


SHOW CREATE TABLE loans;

SELECT
    (SELECT COUNT(*) FROM members) AS members,
    (SELECT COUNT(*) FROM books)   AS books,
    (SELECT COUNT(*) FROM loans)   AS loans;



INSERT INTO members (
    member_number,
    username,
    first_name,
    last_name,
    email,
    phone_number,
    password,
    role
)
VALUES
    ('Lib-9001', 'Micheal', 'Micheal', 'Rasesa', 'librarian@libis.com', '+26770000000', 'Admin12345', 'librarian');

select * from members;
select * from books;