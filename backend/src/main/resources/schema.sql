USE campus_secondhand;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_no VARCHAR(64),
  username VARCHAR(64) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(32),
  email VARCHAR(128),
  avatar VARCHAR(512),
  role VARCHAR(32) NOT NULL DEFAULT 'USER',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME,
  updated_at DATETIME,
  UNIQUE KEY uk_student_no (student_no),
  KEY idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  seller_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  condition_level VARCHAR(64),
  status VARCHAR(32) NOT NULL DEFAULT 'ON_SALE',
  reject_reason VARCHAR(512),
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_seller (seller_id),
  KEY idx_category (category_id),
  KEY idx_status (status),
  KEY idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS item_images (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_id BIGINT NOT NULL,
  image_url VARCHAR(512) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_item (item_id),
  KEY idx_item_sort (item_id, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_user_item (user_id, item_id),
  KEY idx_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT,
  content TEXT,
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_item (item_id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(64) NOT NULL,
  item_id BIGINT NOT NULL,
  buyer_id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME,
  updated_at DATETIME,
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_buyer (buyer_id),
  KEY idx_seller (seller_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  item_id BIGINT NOT NULL,
  reporter_id BIGINT NOT NULL,
  handler_id BIGINT,
  reason VARCHAR(512),
  result_note VARCHAR(512),
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME,
  updated_at DATETIME,
  KEY idx_item (item_id),
  KEY idx_reporter (reporter_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
