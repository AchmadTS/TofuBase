SET TIME ZONE '+00:00';

CREATE TABLE users (
  id_user SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  nama VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  no_telp VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL
);

CREATE TABLE admin (
  id_admin SERIAL PRIMARY KEY,
  id_user INTEGER NOT NULL,
  jabatan VARCHAR(50) NOT NULL,
  level_akses VARCHAR(20) NOT NULL,
  tanggal_dibuat TIMESTAMP NOT NULL
);

CREATE TABLE supplier (
  id_supplier SERIAL PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  alamat TEXT NOT NULL,
  no_telp VARCHAR(20) NOT NULL,
  email VARCHAR(100) NOT NULL
);

CREATE TABLE bahan_baku (
  id_bahan SERIAL PRIMARY KEY,
  id_supplier INTEGER NOT NULL,
  nama VARCHAR(100) NOT NULL,
  satuan VARCHAR(20) NOT NULL,
  stok DOUBLE PRECISION NOT NULL,
  harga_beli DOUBLE PRECISION NOT NULL,
  min_stok DOUBLE PRECISION NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inventaris (
  id_inventaris SERIAL PRIMARY KEY,
  tanggal_cek DATE NOT NULL,
  keterangan TEXT NOT NULL
);

CREATE TABLE laporan_keuangan (
  id_laporan SERIAL PRIMARY KEY,
  periode_awal DATE NOT NULL,
  periode_akhir DATE NOT NULL,
  total_pemasukan DOUBLE PRECISION NOT NULL,
  total_pengeluaran DOUBLE PRECISION NOT NULL,
  saldo DOUBLE PRECISION NOT NULL
);

CREATE TABLE owner (
  id_owner SERIAL PRIMARY KEY,
  id_user INTEGER NOT NULL,
  jabatan VARCHAR(50) NOT NULL,
  level_akses VARCHAR(20) NOT NULL
);

CREATE TABLE pelanggan (
  id_pelanggan SERIAL PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  alamat TEXT NOT NULL,
  no_telp VARCHAR(20) NOT NULL,
  email VARCHAR(100) NOT NULL
);

CREATE TABLE penjualan (
  id_penjualan SERIAL PRIMARY KEY,
  id_pelanggan INTEGER NOT NULL,
  tanggal DATE NOT NULL,
  total DOUBLE PRECISION NOT NULL,
  keterangan TEXT NOT NULL
);

CREATE TABLE pemasukan (
  id_pemasukan SERIAL PRIMARY KEY,
  id_penjualan INTEGER NOT NULL,
  tanggal DATE NOT NULL,
  sumber VARCHAR(100) NOT NULL,
  jumlah DOUBLE PRECISION NOT NULL,
  keterangan TEXT NOT NULL
);

CREATE TABLE pengeluaran (
  id_pengeluaran SERIAL PRIMARY KEY,
  tanggal DATE NOT NULL,
  kategori VARCHAR(100) NOT NULL,
  deskripsi TEXT NOT NULL,
  jumlah DOUBLE PRECISION NOT NULL
);

CREATE TABLE produk (
  id_produk SERIAL PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  satuan VARCHAR(20) NOT NULL,
  harga_jual DOUBLE PRECISION NOT NULL,
  jenis VARCHAR(50) NOT NULL,
  stok DOUBLE PRECISION NOT NULL
);

CREATE TABLE produksi (
  id_produksi SERIAL PRIMARY KEY,
  id_produk INTEGER NOT NULL,
  batch VARCHAR(20) NOT NULL,
  tanggal DATE NOT NULL,
  hasil_tahu INTEGER NOT NULL DEFAULT 0,
  id_user INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'Selesai',
  keterangan TEXT NOT NULL
);

CREATE TABLE record_penjualan (
  id_record_penjualan SERIAL PRIMARY KEY,
  id_penjualan INTEGER NOT NULL,
  id_produk INTEGER NOT NULL,
  jumlah DOUBLE PRECISION NOT NULL,
  harga DOUBLE PRECISION NOT NULL,
  subtotal DOUBLE PRECISION NOT NULL
);

CREATE TABLE record_produksi (
  id_record_produksi SERIAL PRIMARY KEY,
  id_produksi INTEGER NOT NULL,
  id_bahan INTEGER NOT NULL,
  jumlah DOUBLE PRECISION NOT NULL,
  satuan VARCHAR(20) NOT NULL
);

CREATE TABLE staff (
  id_staff SERIAL PRIMARY KEY,
  id_user INTEGER NOT NULL,
  jabatan VARCHAR(50) NOT NULL,
  tanggal_masuk DATE NOT NULL,
  tanggal_keluar DATE DEFAULT NULL
);

ALTER TABLE admin ADD CONSTRAINT admin_ibfk_1 FOREIGN KEY (id_user) REFERENCES users (id_user);
ALTER TABLE bahan_baku ADD CONSTRAINT bahan_baku_ibfk_1 FOREIGN KEY (id_supplier) REFERENCES supplier (id_supplier);
ALTER TABLE owner ADD CONSTRAINT owner_ibfk_1 FOREIGN KEY (id_user) REFERENCES users (id_user);
ALTER TABLE pemasukan ADD CONSTRAINT pemasukan_ibfk_1 FOREIGN KEY (id_penjualan) REFERENCES penjualan (id_penjualan);
ALTER TABLE penjualan ADD CONSTRAINT penjualan_ibfk_1 FOREIGN KEY (id_pelanggan) REFERENCES pelanggan (id_pelanggan);
ALTER TABLE produksi ADD CONSTRAINT produksi_ibfk_1 FOREIGN KEY (id_produk) REFERENCES produk (id_produk);
ALTER TABLE produksi ADD CONSTRAINT produksi_ibfk_2 FOREIGN KEY (id_user) REFERENCES users (id_user);
ALTER TABLE record_penjualan ADD CONSTRAINT record_penjualan_ibfk_1 FOREIGN KEY (id_produk) REFERENCES produk (id_produk);
ALTER TABLE record_penjualan ADD CONSTRAINT record_penjualan_ibfk_2 FOREIGN KEY (id_penjualan) REFERENCES penjualan (id_penjualan);
ALTER TABLE record_produksi ADD CONSTRAINT record_produksi_ibfk_1 FOREIGN KEY (id_bahan) REFERENCES bahan_baku (id_bahan);
ALTER TABLE record_produksi ADD CONSTRAINT record_produksi_ibfk_2 FOREIGN KEY (id_produksi) REFERENCES produksi (id_produksi);
ALTER TABLE staff ADD CONSTRAINT staff_ibfk_1 FOREIGN KEY (id_user) REFERENCES users (id_user);

CREATE INDEX idx_admin_id_user ON admin(id_user);
CREATE INDEX idx_bahan_baku_id_supplier ON bahan_baku(id_supplier);
CREATE INDEX idx_owner_id_user ON owner(id_user);
CREATE INDEX idx_pemasukan_id_penjualan ON pemasukan(id_penjualan);
CREATE INDEX idx_penjualan_id_pelanggan ON penjualan(id_pelanggan);
CREATE INDEX idx_penjualan_tanggal ON penjualan(tanggal);
CREATE INDEX idx_produksi_id_produk ON produksi(id_produk);
CREATE INDEX idx_produksi_id_user ON produksi(id_user);
CREATE INDEX idx_produksi_tanggal ON produksi(tanggal);
CREATE INDEX idx_produksi_prod_tanggal_id ON produksi(tanggal, id_produksi);
CREATE INDEX idx_produksi_prod_batch ON produksi(batch);
CREATE INDEX idx_record_penjualan_id_penjualan ON record_penjualan(id_penjualan);
CREATE INDEX idx_record_penjualan_id_produk ON record_penjualan(id_produk);
CREATE INDEX idx_record_produksi_id_produksi ON record_produksi(id_produksi);
CREATE INDEX idx_record_produksi_id_bahan ON record_produksi(id_bahan);
CREATE INDEX idx_staff_id_user ON staff(id_user);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_bahan_baku_updated_at
BEFORE UPDATE ON bahan_baku
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();