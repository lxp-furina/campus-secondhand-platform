USE campus_secondhand;

SET NAMES utf8mb4;

-- 演示账号密码均为 123456（SHA-256）
INSERT INTO users (student_no, username, password, phone, email, role, status) VALUES
('2021001', '小明', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '13800000001', 'xiaoming@campus.edu', 'USER', 'ENABLED'),
('2021002', '小红', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '13800000002', 'xiaohong@campus.edu', 'USER', 'ENABLED'),
('2021003', '小李', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '13800000003', 'xiaoli@campus.edu', 'USER', 'ENABLED')
ON DUPLICATE KEY UPDATE username = VALUES(username);

UPDATE users SET username = '哈哈哈', phone = '12345678900' WHERE student_no = '2026';

-- 清理旧测试物品图片
DELETE FROM item_images WHERE item_id IN (SELECT id FROM items WHERE seller_id = 2 AND title IN ('1231321', '123'));

-- 更新「哈哈哈」的发布
UPDATE items SET
  title = '高等数学（第七版）上下册',
  description = '大一用过一学期，无缺页无涂改，附习题册。南区宿舍可面交。',
  price = 25.00,
  condition_level = '九成新',
  category_id = 1,
  status = 'SOLD'
WHERE id = 1;

UPDATE items SET
  title = '线性代数复习笔记',
  description = '期末整理的重点笔记，扫描版+纸质版一起出，适合考前突击。',
  price = 12.00,
  condition_level = '八成新',
  category_id = 1,
  status = 'ON_SALE'
WHERE id = 2;

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT 1, 'http://localhost:8090/uploads/seed001_textbook.png', 0
WHERE NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = 1);

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT 2, 'http://localhost:8090/uploads/seed001_textbook.png', 0
WHERE NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = 2);

-- 其他用户发布的闲置
INSERT INTO items (seller_id, category_id, title, description, price, condition_level, status)
SELECT u.id, 2, '索尼 WH-1000XM4 耳机', '降噪效果很好，附带原装盒和数据线，电池续航正常。', 899.00, '九成新', 'ON_SALE'
FROM users u WHERE u.student_no = '2021001'
AND NOT EXISTS (SELECT 1 FROM items WHERE title = '索尼 WH-1000XM4 耳机');

INSERT INTO items (seller_id, category_id, title, description, price, condition_level, status)
SELECT u.id, 3, '宿舍护眼台灯', '三档调光，灯臂可调，毕业清仓。', 35.00, '正常使用', 'ON_SALE'
FROM users u WHERE u.student_no = '2021002'
AND NOT EXISTS (SELECT 1 FROM items WHERE title = '宿舍护眼台灯');

INSERT INTO items (seller_id, category_id, title, description, price, condition_level, status)
SELECT u.id, 4, '尤尼克斯羽毛球拍', '送球拍袋和两桶球，适合入门练习。', 168.00, '八成新', 'ON_SALE'
FROM users u WHERE u.student_no = '2021003'
AND NOT EXISTS (SELECT 1 FROM items WHERE title = '尤尼克斯羽毛球拍');

INSERT INTO items (seller_id, category_id, title, description, price, condition_level, status)
SELECT u.id, 5, '优衣库连帽卫衣 L码', '洗过两次，无明显污渍，适合春秋穿搭。', 59.00, '九成新', 'ON_SALE'
FROM users u WHERE u.student_no = '2021001'
AND NOT EXISTS (SELECT 1 FROM items WHERE title = '优衣库连帽卫衣 L码');

INSERT INTO items (seller_id, category_id, title, description, price, condition_level, status)
SELECT u.id, 6, '高数一对一答疑（2小时）', '数学系学长，可线上或图书馆面授，时间可约。', 30.00, '全新', 'ON_SALE'
FROM users u WHERE u.student_no = '2021002'
AND NOT EXISTS (SELECT 1 FROM items WHERE title = '高数一对一答疑（2小时）');

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT i.id, 'http://localhost:8090/uploads/seed002_digital.png', 0 FROM items i
WHERE i.title = '索尼 WH-1000XM4 耳机' AND NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = i.id);

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT i.id, 'http://localhost:8090/uploads/seed003_daily.png', 0 FROM items i
WHERE i.title = '宿舍护眼台灯' AND NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = i.id);

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT i.id, 'http://localhost:8090/uploads/seed004_sports.png', 0 FROM items i
WHERE i.title = '尤尼克斯羽毛球拍' AND NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = i.id);

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT i.id, 'http://localhost:8090/uploads/seed005_clothes.png', 0 FROM items i
WHERE i.title = '优衣库连帽卫衣 L码' AND NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = i.id);

INSERT INTO item_images (item_id, image_url, sort_order)
SELECT i.id, 'http://localhost:8090/uploads/seed006_service.png', 0 FROM items i
WHERE i.title = '高数一对一答疑（2小时）' AND NOT EXISTS (SELECT 1 FROM item_images WHERE item_id = i.id);

-- 收藏
INSERT INTO favorites (user_id, item_id)
SELECT u.id, i.id FROM users u, items i
WHERE u.student_no = '2026' AND i.title = '索尼 WH-1000XM4 耳机'
ON DUPLICATE KEY UPDATE user_id = user_id;

INSERT INTO favorites (user_id, item_id)
SELECT u.id, i.id FROM users u, items i
WHERE u.student_no = '2026' AND i.title = '尤尼克斯羽毛球拍'
ON DUPLICATE KEY UPDATE user_id = user_id;

-- 订单：哈哈哈 作为买家
INSERT INTO orders (order_no, item_id, buyer_id, seller_id, amount, status)
SELECT 'O20260617001', i.id, b.id, i.seller_id, i.price, 'PENDING'
FROM items i, users b
WHERE i.title = '索尼 WH-1000XM4 耳机' AND b.student_no = '2026'
AND NOT EXISTS (SELECT 1 FROM orders WHERE order_no = 'O20260617001');

INSERT INTO orders (order_no, item_id, buyer_id, seller_id, amount, status)
SELECT 'O20260617002', i.id, b.id, i.seller_id, i.price, 'PROCESSING'
FROM items i, users b
WHERE i.title = '宿舍护眼台灯' AND b.student_no = '2026'
AND NOT EXISTS (SELECT 1 FROM orders WHERE order_no = 'O20260617002');

-- 订单：哈哈哈 作为卖家（已完成，需先临时上架再标记已售出）
UPDATE items SET status = 'ON_SALE' WHERE id = 1;

INSERT INTO orders (order_no, item_id, buyer_id, seller_id, amount, status)
SELECT 'O20260617003', 1, b.id, s.id, 25.00, 'COMPLETED'
FROM users b, users s
WHERE b.student_no = '2021001' AND s.student_no = '2026'
AND NOT EXISTS (SELECT 1 FROM orders WHERE order_no = 'O20260617003');

UPDATE items SET status = 'SOLD' WHERE id = 1;

-- 订单：已取消
INSERT INTO orders (order_no, item_id, buyer_id, seller_id, amount, status)
SELECT 'O20260617004', i.id, b.id, i.seller_id, i.price, 'CANCELLED'
FROM items i, users b
WHERE i.title = '优衣库连帽卫衣 L码' AND b.student_no = '2021003'
AND NOT EXISTS (SELECT 1 FROM orders WHERE order_no = 'O20260617004');

-- 留言
INSERT INTO messages (item_id, user_id, parent_id, content)
SELECT i.id, u.id, NULL, '耳机还能再便宜一点吗？可以南区当面验货。'
FROM items i, users u
WHERE i.title = '索尼 WH-1000XM4 耳机' AND u.student_no = '2026'
AND NOT EXISTS (
  SELECT 1 FROM messages m WHERE m.item_id = i.id AND m.content = '耳机还能再便宜一点吗？可以南区当面验货。'
);

INSERT INTO messages (item_id, user_id, parent_id, content)
SELECT i.id, u.id, NULL, '笔记是手写还是打印的？'
FROM items i, users u
WHERE i.id = 2 AND u.student_no = '2021002'
AND NOT EXISTS (
  SELECT 1 FROM messages m WHERE m.item_id = i.id AND m.content = '笔记是手写还是打印的？'
);

INSERT INTO messages (item_id, user_id, parent_id, content)
SELECT m.item_id, u.id, m.id, '纸质手写版，附赠电子版扫描件。'
FROM messages m, users u, items i
WHERE m.content = '笔记是手写还是打印的？' AND u.student_no = '2026' AND i.id = m.item_id
AND NOT EXISTS (
  SELECT 1 FROM messages r WHERE r.parent_id = m.id AND r.content = '纸质手写版，附赠电子版扫描件。'
);

-- 举报（管理端待处理）
INSERT INTO reports (item_id, reporter_id, reason, status)
SELECT i.id, u.id, '描述与实物不符，疑似虚假宣传。', 'PENDING'
FROM items i, users u
WHERE i.title = '高数一对一答疑（2小时）' AND u.student_no = '2021003'
AND NOT EXISTS (
  SELECT 1 FROM reports r WHERE r.item_id = i.id AND r.status = 'PENDING'
);
