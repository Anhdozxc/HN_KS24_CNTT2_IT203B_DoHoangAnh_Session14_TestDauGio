CREATE DATABASE IF NOT EXISTS Test;
USE Test;
-- 1. Tạo bảng tài khoản
CREATE TABLE Accounts (
AccountId VARCHAR(10) PRIMARY KEY,
FullName NVARCHAR(50),
Balance DECIMAL(18, 2)
);
-- 2. Chèn dữ liệu mẫu
INSERT INTO Accounts VALUES ('ACC01', 'Nguyen Van A', 5000), ('ACC02', 'Tran Thi B',
2000);
-- 3. Tạo Procedure cập nhật số dư (Dùng cho CallableStatement)
DELIMITER $$
CREATE PROCEDURE sp_UpdateBalance (
// id VARCHAR(10),
// Amount DECIMAL(18, 2)
)
AS
BEGIN
UPDATE Accounts SET Balance = Balance + @Amount WHERE AccountId = @Id;
END //
DELIMITER //