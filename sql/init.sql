DROP DATABASE IF EXISTS campus_secondhand;
CREATE DATABASE campus_secondhand DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE campus_secondhand;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_no VARCHAR(32) NOT NULL UNIQUE,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  phone VARCHAR(32),
  email VARCHAR(128),
  avatar VARCHAR(500),
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT ck_users_role CHECK (role IN ('USER','ADMIN')),
  CONSTRAINT ck_users_status CHECK (status IN ('ENABLED','DISABLED'))
) ENGINE=InnoDB;

CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL UNIQUE,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  title VARCHAR(120) NOT NULL,
  description TEXT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  condition_level VARCHAR(32) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ON_SALE',
  reject_reason VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_items_seller FOREIGN KEY (seller_id) REFERENCES users(id),
  CONSTRAINT fk_items_category FOREIGN KEY (category_id) REFERENCES categories(id),
  CONSTRAINT ck_items_price CHECK (price > 0),
  CONSTRAINT ck_items_status CHECK (status IN ('PENDING_REVIEW','ON_SALE','REJECTED','OFF_SHELF','SOLD'))
) ENGINE=InnoDB;

CREATE TABLE item_images (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_id BIGINT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_item_images_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_favorites_user_item (user_id, item_id),
  CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_favorites_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL UNIQUE,
  item_id BIGINT NOT NULL,
  buyer_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_item FOREIGN KEY (item_id) REFERENCES items(id),
  CONSTRAINT fk_orders_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),
  CONSTRAINT fk_orders_seller FOREIGN KEY (seller_id) REFERENCES users(id),
  CONSTRAINT ck_orders_amount CHECK (amount > 0),
  CONSTRAINT ck_orders_status CHECK (status IN ('PENDING','PROCESSING','COMPLETED','CANCELLED')),
  CONSTRAINT ck_orders_buyer_seller CHECK (buyer_id <> seller_id)
) ENGINE=InnoDB;

CREATE TABLE messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT,
  content VARCHAR(1000) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_messages_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
  CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_messages_parent FOREIGN KEY (parent_id) REFERENCES messages(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  item_id BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  handler_id BIGINT,
  reason VARCHAR(1000) NOT NULL,
  result_note VARCHAR(1000),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_reports_item FOREIGN KEY (item_id) REFERENCES items(id),
  CONSTRAINT fk_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(id),
  CONSTRAINT fk_reports_handler FOREIGN KEY (handler_id) REFERENCES users(id),
  CONSTRAINT ck_reports_status CHECK (status IN ('PENDING','REJECTED','ITEM_REMOVED'))
) ENGINE=InnoDB;

INSERT INTO users (student_no, username, password, role, status)
VALUES ('admin', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'ENABLED');

INSERT INTO categories (name, sort_order) VALUES
('教材资料', 1),
('数码电子', 2),
('生活用品', 3),
('运动户外', 4),
('服饰鞋包', 5),
('互助服务', 6);

DELIMITER //

CREATE PROCEDURE sp_dashboard_stats()
BEGIN
  SELECT
    (SELECT COUNT(*) FROM users) AS user_total,
    (SELECT COUNT(*) FROM items) AS item_total,
    (SELECT COUNT(*) FROM orders) AS order_total,
    (SELECT COUNT(*) FROM reports WHERE status = 'PENDING') AS pending_report_total;
END //

CREATE TRIGGER trg_orders_before_insert
BEFORE INSERT ON orders
FOR EACH ROW
BEGIN
  DECLARE v_status VARCHAR(20);
  DECLARE v_seller BIGINT;
  DECLARE v_price DECIMAL(10,2);

  SELECT status, seller_id, price INTO v_status, v_seller, v_price
  FROM items
  WHERE id = NEW.item_id;

  IF v_status IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '物品不存在';
  END IF;

  IF v_status <> 'ON_SALE' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '仅上架物品可下单';
  END IF;

  IF NEW.buyer_id = v_seller THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '不能购买自己发布的物品';
  END IF;

  SET NEW.seller_id = v_seller;
  SET NEW.amount = v_price;
END //

CREATE TRIGGER trg_orders_after_update
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
  IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
    UPDATE items SET status = 'SOLD' WHERE id = NEW.item_id;
  END IF;

  IF NEW.status = 'CANCELLED' AND OLD.status <> 'CANCELLED' THEN
    UPDATE items SET status = 'ON_SALE' WHERE id = NEW.item_id AND status = 'SOLD';
  END IF;
END //

DELIMITER ;
