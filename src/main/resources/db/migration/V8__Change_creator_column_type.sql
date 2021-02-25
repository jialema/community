-- H2
-- alter table QUESTION alter column CREATOR BIGINT not null;
-- alter table COMMENT alter column COMMENTATOR BIGINT not null;

-- MySQL
alter table QUESTION modify CREATOR BIGINT not null;
alter table COMMENT modify COMMENTATOR BIGINT not null;