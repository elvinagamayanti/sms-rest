-- File: src/test/resources/test-data.sql
-- Optional test data setup - letakkan di src/test/resources/

-- Insert test roles
INSERT INTO roles (id, name, display_name, description, created_at, updated_at) VALUES 
(1, 'ROLE_SUPERADMIN', 'Super Administrator', 'Full system access', NOW(), NOW()),
(2, 'ROLE_ADMIN_PUSAT', 'Admin Pusat', 'Central administrator', NOW(), NOW()),
(3, 'ROLE_OPERATOR_PUSAT', 'Operator Pusat', 'Central operator', NOW(), NOW()),
(4, 'ROLE_ADMIN_PROVINSI', 'Admin Provinsi', 'Province administrator', NOW(), NOW()),
(5, 'ROLE_OPERATOR_PROVINSI', 'Operator Provinsi', 'Province operator', NOW(), NOW()),
(6, 'ROLE_ADMIN_SATKER', 'Admin Satker', 'Work unit administrator', NOW(), NOW()),
(7, 'ROLE_OPERATOR_SATKER', 'Operator Satker', 'Work unit operator', NOW(), NOW());

-- Insert test provinces
INSERT INTO provinces (id, name, code, created_at, updated_at) VALUES 
(1, 'DKI Jakarta', '31', NOW(), NOW()),
(2, 'Jawa Barat', '32', NOW(), NOW()),
(3, 'Jawa Tengah', '33', NOW(), NOW()),
(4, 'Jawa Timur', '35', NOW(), NOW()),
(5, 'Banten', '36', NOW(), NOW());

-- Insert test deputis
INSERT INTO deputis (id, name, code, description, created_at, updated_at) VALUES 
(1, 'Deputi Statistik Sosial', 'D01', 'Deputi bidang statistik sosial', NOW(), NOW()),
(2, 'Deputi Statistik Produksi', 'D02', 'Deputi bidang statistik produksi', NOW(), NOW()),
(3, 'Deputi Statistik Distribusi', 'D03', 'Deputi bidang statistik distribusi', NOW(), NOW()),
(4, 'Deputi Metodologi dan Informasi Statistik', 'D04', 'Deputi bidang metodologi', NOW(), NOW());

-- Insert test direktorats
INSERT INTO direktorats (id, name, code, description, deputi_id, created_at, updated_at) VALUES 
(1, 'Direktorat Statistik Kependudukan dan Ketenagakerjaan', 'D01.01', 'Statistik demografi', 1, NOW(), NOW()),
(2, 'Direktorat Statistik Kesejahteraan Rakyat', 'D01.02', 'Statistik kesejahteraan', 1, NOW(), NOW()),
(3, 'Direktorat Statistik Tanaman Pangan', 'D02.01', 'Statistik pertanian', 2, NOW(), NOW()),
(4, 'Direktorat Statistik Industri', 'D02.02', 'Statistik industri', 2, NOW(), NOW());

-- Insert test satkers
INSERT INTO satkers (id, name, code, address, province_id, created_at, updated_at) VALUES 
(1, 'BPS Provinsi DKI Jakarta', '3100', 'Jakarta Pusat', 1, NOW(), NOW()),
(2, 'BPS Kota Jakarta Pusat', '3101', 'Jakarta Pusat', 1, NOW(), NOW()),
(3, 'BPS Kota Jakarta Utara', '3102', 'Jakarta Utara', 1, NOW(), NOW()),
(4, 'BPS Provinsi Jawa Barat', '3200', 'Bandung', 2, NOW(), NOW()),
(5, 'BPS Kota Bandung', '3201', 'Bandung', 2, NOW(), NOW());

-- Insert test users
INSERT INTO users (id, name, email, password, role_id, satker_id, is_active, created_at, updated_at) VALUES 
(1, 'Super Admin', 'admin@example.com', '$2a$10$7JqBvtbDyPs/Ap6s5Z8l6OhCqCJNGqHJKPP1hJJUhMFhUl9Oc5n7W', 1, 1, 1, NOW(), NOW()),
(2, 'Admin Pusat', 'admin.pusat@example.com', '$2a$10$7JqBvtbDyPs/Ap6s5Z8l6OhCqCJNGqHJKPP1hJJUhMFhUl9Oc5n7W', 2, 1, 1, NOW(), NOW()),
(3, 'Admin Jakarta', 'admin.jakarta@example.com', '$2a$10$7JqBvtbDyPs/Ap6s5Z8l6OhCqCJNGqHJKPP1hJJUhMFhUl9Oc5n7W', 4, 1, 1, NOW(), NOW()),
(4, 'Operator Jakarta', 'operator.jakarta@example.com', '$2a$10$7JqBvtbDyPs/Ap6s5Z8l6OhCqCJNGqHJKPP1hJJUhMFhUl9Oc5n7W', 5, 2, 1, NOW(), NOW());

-- Insert test programs
INSERT INTO programs (id, name, description, start_date, end_date, created_at, updated_at) VALUES 
(1, 'Program Statistik Dasar', 'Program pengumpulan data statistik dasar', '2024-01-01', '2024-12-31', NOW(), NOW()),
(2, 'Program Statistik Sektoral', 'Program statistik bidang sektoral', '2024-01-01', '2024-12-31', NOW(), NOW()),
(3, 'Program Statistik Khusus', 'Program statistik untuk kebutuhan khusus', '2024-01-01', '2024-12-31', NOW(), NOW());

-- Insert test outputs
INSERT INTO outputs (id, name, description, program_id, target_value, unit, created_at, updated_at) VALUES 
(1, 'Data Penduduk', 'Output data kependudukan', 1, 100, 'Publikasi', NOW(), NOW()),
(2, 'Data Ekonomi', 'Output data ekonomi', 1, 50, 'Laporan', NOW(), NOW()),
(3, 'Data Sosial', 'Output data sosial', 2, 75, 'Dashboard', NOW(), NOW()),
(4, 'Data Industri', 'Output data industri', 2, 25, 'Infografis', NOW(), NOW());

-- Insert test kegiatans
INSERT INTO kegiatans (id, name, description, output_id, anggaran, start_date, end_date, direktorat_pj_id, created_at, updated_at) VALUES 
(1, 'Sensus Penduduk', 'Kegiatan sensus penduduk nasional', 1, 5000000000, '2024-01-01', '2024-12-31', 1, NOW(), NOW()),
(2, 'Survei Angkatan Kerja', 'Kegiatan survei ketenagakerjaan', 1, 2000000000, '2024-02-01', '2024-11-30', 1, NOW(), NOW()),
(3, 'Survei Konsumsi Rumah Tangga', 'Kegiatan survei konsumsi', 2, 1500000000, '2024-03-01', '2024-10-31', 2, NOW(), NOW()),
(4, 'Survei Industri Manufaktur', 'Kegiatan survei industri', 4, 1000000000, '2024-04-01', '2024-09-30', 4, NOW(), NOW());