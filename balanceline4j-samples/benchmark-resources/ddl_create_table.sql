/*==============================================================*/
/* Table: CUSTOMER                                              */
/*==============================================================*/
CREATE TABLE CUSTOMER  (
   IDCUSTOMER 			NUMBER                       	NOT NULL,
   NAME       			VARCHAR2(255)                   NOT NULL,
   ADDRESS     			VARCHAR2(255)                   NULL,
   PHONE       			VARCHAR2(255)                   NULL
);

ALTER TABLE CUSTOMER
   ADD CONSTRAINT PK_CUSTOMER PRIMARY KEY (IDCUSTOMER)
      USING INDEX;