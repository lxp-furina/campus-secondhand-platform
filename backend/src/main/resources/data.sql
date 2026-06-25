USE campus_secondhand;

SET @pwd = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c4923adc6c92';
SET @now = NOW();

INSERT INTO users (id, student_no, username, password, phone, email, avatar, role, status, created_at, updated_at) VALUES
(1, '20240001', '小明学长', @pwd, '13800138000', 'admin@campus.edu', NULL, 'ADMIN', 'ENABLED', @now, @now),
(2, '20240002', '小李', @pwd, '13800138001', 'xiaoli@campus.edu', NULL, 'USER', 'ENABLED', @now, @now),
(3, '20240003', '小王', @pwd, '13800138002', 'xiaowang@campus.edu', NULL, 'USER', 'ENABLED', @now, @now)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO categories (id, name, sort_order, created_at, updated_at) VALUES
(1, '教材书籍', 1, @now, @now),
(2, '数码电子', 2, @now, @now),
(3, '生活用品', 3, @now, @now),
(4, '运动户外', 4, @now, @now),
(5, '服装鞋帽', 5, @now, @now),
(6, '服务交易', 6, @now, @now)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO items (id, seller_id, category_id, title, description, price, condition_level, status, reject_reason, created_at, updated_at) VALUES
(1, 2, 1, '《高等数学》第七版 同济大学', '几乎全新，封面整洁，内页无笔记。原价45元，便宜出给学弟学妹。', 20.00, '九成新', 'ON_SALE', NULL, @now, @now),
(2, 2, 2, '罗技 MX Master 3S 无线鼠标', '去年双十一购入，使用不到半年，功能完好，配件齐全。原价799。', 399.00, '九成新', 'ON_SALE', NULL, @now, @now),
(3, 3, 3, '宜家 拉舍办公椅', '毕业出，九成新，黑色网布，带滚轮。宿舍神器，久坐不累。', 150.00, '八成新', 'ON_SALE', NULL, @now, @now),
(4, 3, 4, '迪卡侬跑步鞋 42码', '买来穿过3次，尺码不合适转售。原价299，现120出。', 120.00, '九成新', 'ON_SALE', NULL, @now, @now),
(5, 2, 5, '优衣库羽绒服 L码 卡其色', '冬天穿了一整个学期，保暖很好，无破损无污渍。原价599。', 180.00, '八成新', 'ON_SALE', NULL, @now, @now),
(6, 3, 6, '考研英语一对一线上辅导', '已上岸985研究生，英语85+，可提供学习规划和答疑。每周2次，每次1小时。', 100.00, '全新服务', 'ON_SALE', NULL, @now, @now)
ON DUPLICATE KEY UPDATE id=id;

INSERT INTO item_images (id, item_id, image_url, sort_order, created_at, updated_at) VALUES
(1, 1, '/uploads/seed001_textbook.png', 1, @now, @now),
(2, 2, '/uploads/seed002_digital.png', 1, @now, @now),
(3, 3, '/uploads/seed003_daily.png', 1, @now, @now),
(4, 4, '/uploads/seed004_sports.png', 1, @now, @now),
(5, 5, '/uploads/seed005_clothes.png', 1, @now, @now),
(6, 6, '/uploads/seed006_service.png', 1, @now, @now)
ON DUPLICATE KEY UPDATE id=id;
