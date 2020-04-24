DROP DATABASE IF EXISTS Ecco
-- Create Database
CREATE DATABASE Ecco

GO

-- Create Tables
USE Ecco;

CREATE TABLE tblRole (
	fldRoleId INTEGER IDENTITY(1,1) PRIMARY KEY,
	fldRoleName VARCHAR(15) NOT NULL,
)

CREATE TABLE tblIdCard (
	fldIdCardId INTEGER PRIMARY KEY, 
	fldBalance FLOAT NOT NULL,
	fldRoleId INTEGER FOREIGN KEY REFERENCES tblRole(fldRoleId) NOT NULL
)

CREATE TABLE tblUser (
	fldUserId INTEGER IDENTITY(1,1) PRIMARY KEY,
	fldFullName VARCHAR(30) NOT NULL,
	fldBirthDate DATE NOT NULL,
	fldIdCardId INTEGER FOREIGN KEY REFERENCES tblIdCard(fldIdCardId) NOT NULL
)

CREATE TABLE tblReceipt (
	fldReceiptId INTEGER IDENTITY(1,1) PRIMARY KEY,
	fldCustomerId INTEGER FOREIGN KEY REFERENCES tblUser(fldUserId) NOT NULL,
	fldTotalPrice FLOAT NOT NULL,
	fldDate DATETIME NOT NULL
)

CREATE TABLE tblSupplier (
	fldSupplierId INTEGER IDENTITY(1,1) PRIMARY KEY,
	fldName VARCHAR(30) NOT NULL,
)

CREATE TABLE tblProduct (
	fldProductId INTEGER PRIMARY KEY,
	fldPrice INTEGER NOT NULL,
	fldName VARCHAR(30) NOT NULL,
	fldStock INTEGER NOT NULL,
	fldSupplierId INTEGER FOREIGN KEY REFERENCES tblSupplier(fldSupplierId) NOT NULL,
	fldImagePath VARCHAR(50),
	fldReorderLimit INTEGER NOT NULL
)

CREATE TABLE tblProductReceipt (
	fldProductReceiptId INTEGER IDENTITY(1,1) PRIMARY KEY,
	fldQuantity INTEGER NOT NULL,
	fldProductId INTEGER FOREIGN KEY REFERENCES tblProduct(fldProductId) NOT NULL,
	fldReceiptId INTEGER FOREIGN KEY REFERENCES tblReceipt(fldReceiptId) NOT NULL
)

-- Create logins for server access

CREATE LOGIN eccoUser WITH PASSWORD = 'whoYouAuthenticatingAs123!';


-- Create users for database access

CREATE USER eccoUser FOR LOGIN eccoUser;



-- Create database roles

CREATE ROLE applicationUser;

ALTER ROLE applicationUser ADD MEMBER eccoUser;

-- grant the role permission to execute functions and procedures on the database
GRANT EXECUTE ON DATABASE::Ecco TO applicationUser;
-- grant the role permission to insert into tables of the database
GRANT INSERT ON DATABASE::Ecco TO applicationUser;
-- grant the role permission to select from database
GRANT SELECT ON DATABASE::Ecco TO applicationUser;
-- grant the role permission to delete a product from the product table
GRANT DELETE ON OBJECT::tblProduct TO applicationUser;
-- grant the role permission to update tables
GRANT UPDATE ON OBJECT::tblProduct TO applicationUser;
-- grant the role permission to update tables
GRANT UPDATE ON OBJECT::tblIdCard TO applicationUser;



-- Create Dummy Data

INSERT INTO tblRole VALUES('Customer');
INSERT INTO tblRole VALUES('Employee');

INSERT INTO tblIdCard VALUES(3546,500,1);
INSERT INTO tblIdCard VALUES(4951,2000,1);
INSERT INTO tblIdCard VALUES(9865,0,1);
INSERT INTO tblIdCard VALUES(2222,0,2);
INSERT INTO tblIdCard VALUES(1111,1000,2);

INSERT INTO tblUser VALUES('Jacob Brodersen Bonefeld','1995-06-28',3546);
INSERT INTO tblUser VALUES('Kasper Schou Jørgensen','1993-03-26',4951);
INSERT INTO tblUser VALUES('Robert Huldgaard Skaar','1994-09-29',9865);
INSERT INTO tblUser VALUES('Frank Hansen','1980-10-15',2222);
INSERT INTO tblUser VALUES('Karsten Skov','1980-10-15',1111);

INSERT INTO tblSupplier VALUES('Bilka');
INSERT INTO tblSupplier VALUES('Kohberg');
INSERT INTO tblSupplier VALUES('Coca Cola');
INSERT INTO tblSupplier VALUES('Home Made');


INSERT INTO tblProduct VALUES(3,15,'Cup Noodle',7,1,'Resources/Images/cupNoodles.png',5);
INSERT INTO tblProduct VALUES(5,20,'Coca Cola',30,3,'Resources/Images/cocaCola.png',15);
INSERT INTO tblProduct VALUES(6,20,'Faxe Kondi',27,3,'Resources/Images/faxeKondi.png',15);
INSERT INTO tblProduct VALUES(7,20,'Apple Juice',15,3,'Resources/Images/appleJuice.png',10);
INSERT INTO tblProduct VALUES(8,20,'Orange Juice',12,1,'Resources/Images/orangeJuice.png',10);

GO
	
USE Ecco
-- Creating Receipts

DECLARE @cnt INT = 0;
DECLARE @randomDate DATETIME;
DECLARE @FromDate DATETIME;
DECLARE @ToDate   DATETIME;
DECLARE @Seconds INT;
DECLARE @Random INT;
DECLARE @NumberOfReceipts INT;

SET @FromDate = '2019-01-01 08:22:13';
SET @ToDate = '2020-12-31 17:56:31';
SET @NumberOfReceipts = 10000;

WHILE @cnt < @NumberOfReceipts
BEGIN

	-- Generates a random date
	SET @seconds = DATEDIFF(SECOND, @FromDate, @ToDate);
	SET @Random = ROUND(((@Seconds-1) * RAND()), 0);
	SET @randomDate = DATEADD(SECOND, @Random, @FromDate);
	
                                    -- CustomerID       |       Total Price        |   Date      
   INSERT INTO tblReceipt VALUES(FLOOR(RAND()*(5-1+1))+1,FLOOR(RAND()*(100-10+1))+10,@randomDate);

   SET @cnt = @cnt + 1;

END;

-- Creating ProductReceipt


DECLARE @count INT = 0;

WHILE @count < @NumberOfReceipts * 3
BEGIN
                                          -- Quantity          |       Product ID               | receipt id
   INSERT INTO tblProductReceipt VALUES(FLOOR(RAND()*(5-1+1))+1,FLOOR(RAND()*(8-3+1))+3,FLOOR(RAND()*(@NumberOfReceipts-1+1))+1);

   SET @count = @count + 1;

END;

GO

USE Ecco

CREATE PROCEDURE [dbo].[storeReceipt](@costumerId int,@totalPrice decimal(18, 0),@date datetime,@recieptId int OUTPUT)

AS
		BEGIN
		INSERT INTO tblReceipt(fldCustomerId,fldTotalPrice,fldDate) VALUES (@costumerId,@totalPrice,@date)

		SELECT @recieptId= fldReceiptId FROM tblReceipt WHERE fldCustomerId = @costumerId AND fldTotalPrice = @totalPrice AND fldDate = @date
		RETURN
		END
GO


CREATE PROCEDURE [dbo].[storeSale](@currentReceiptID int,@productId int,@quantity int)

AS
		BEGIN
		INSERT INTO tblProductReceipt (fldProductId,fldQuantity,fldReceiptId) VALUES (@productId,@quantity,@currentReceiptID)
		END


