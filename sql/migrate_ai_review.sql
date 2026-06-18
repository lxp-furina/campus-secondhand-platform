USE campus_secondhand;

ALTER TABLE items
  ADD COLUMN reject_reason VARCHAR(500) NULL AFTER status;

ALTER TABLE items DROP CHECK ck_items_status;

ALTER TABLE items
  ADD CONSTRAINT ck_items_status CHECK (status IN ('PENDING_REVIEW','ON_SALE','REJECTED','OFF_SHELF','SOLD'));
