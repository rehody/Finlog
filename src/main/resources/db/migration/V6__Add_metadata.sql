alter table transaction_
    add column deleted bool not null default false;

alter table transaction_
    add column version bigint not null default 0;

alter table transaction_
    add column created_at timestamp without time zone not null default now();

alter table transaction_
    add column updated_at timestamp without time zone not null default now();

alter table transaction_
    add column deleted_at timestamp without time zone;


alter table user_
    rename soft_delete to deleted;

alter table user_
    add column version bigint not null default 0;

alter table user_
    add column created_at timestamp without time zone not null default now();

alter table user_
    add column updated_at timestamp without time zone not null default now();

alter table user_
    add column deleted_at timestamp without time zone;


update transaction_
set created_at = transaction_date;

update transaction_
set updated_at = created_at;

update user_
set created_at = registration_date;

update user_
set updated_at = created_at;