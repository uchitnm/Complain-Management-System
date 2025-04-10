-- Update users table
ALTER TABLE users 
MODIFY COLUMN worker_category VARCHAR(50),
ADD COLUMN last_active TIMESTAMP;

-- Update complaints table
ALTER TABLE complaints 
MODIFY COLUMN category ENUM('PLUMBER', 'CARPENTER', 'ELECTRICIAN', 'OTHER'),
MODIFY COLUMN status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED'),
ADD COLUMN worker_comments TEXT,
ADD COLUMN last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
