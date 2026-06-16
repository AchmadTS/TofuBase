# TofuBase

![Java](https://img.shields.io/badge/Java-25-orange)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Swing](https://img.shields.io/badge/Java-Swing-green)
![Status](https://img.shields.io/badge/Status-Active%20Development-success)

**TofuBase** adalah aplikasi desktop berbasis **Java Swing** dan **MySQL** untuk membantu pengelolaan operasional pabrik tahu. Project ini menyediakan sistem login, dashboard ringkasan, manajemen bahan baku, riwayat transaksi bahan baku, serta struktur database yang sudah disiapkan untuk modul produksi, penjualan, keuangan, inventaris, supplier, pelanggan, dan manajemen user.

## Fitur Utama

- **Login berbasis role** untuk `Admin`, `Owner`, dan `Staff`
- **Dashboard** dengan:
  - kartu ringkasan data
  - grafik produksi berdasarkan rentang waktu
  - status stok bahan baku
  - tabel aktivitas/riwayat data
  - auto-refresh data
- **Manajemen Bahan Baku**:
  - daftar stok bahan baku
  - pencarian
  - pagination
  - tambah data
  - edit data bahan baku
  - lihat riwayat bahan baku
- **UI modern** dengan komponen kustom:
  - sidebar
  - rounded panel
  - custom scrollbar
  - tabel aktivitas
- **Database seeder** untuk mengisi data awal dan mengosongkan tabel sebelum seed ulang
- **Struktur database lengkap** untuk kebutuhan operasional pabrik tahu

## Teknologi yang Digunakan

- **Java 25**
- **Maven**
- **Java Swing**
- **MySQL / MariaDB**
- **MySQL Connector/J 8.3.0**

## Struktur Project

```text
TofuBase/
├─ src/main/java/
│  ├─ App/                  # Entry point aplikasi
│  ├─ components/           # Komponen UI kustom
│  ├─ controllers/          # Controller
│  ├─ dao/                  # Akses data ke database
│  ├─ database/
│  │  ├─ tofubase.sql       # Struktur database
│  │  └─ seeder/            # File SQL data awal
│  ├─ models/               # Model data
│  ├─ tools/                # Tool utilitas, termasuk database seeder
│  ├─ utils/                # Koneksi, theme, formatting
│  └─ views/                # Tampilan Swing
└─ pom.xml
```

## Struktur Database

Database `tofubase` sudah menyiapkan tabel utama berikut:

- `users`
- `admin`
- `owner`
- `staff`
- `supplier`
- `pelanggan`
- `produk`
- `bahan_baku`
- `produksi`
- `record_produksi`
- `penjualan`
- `record_penjualan`
- `pemasukan`
- `pengeluaran`
- `laporan_keuangan`
- `inventaris`

## Prasyarat

Sebelum menjalankan project, pastikan sudah terpasang:

- **JDK 25**
- **Apache Maven**
- **MySQL / MariaDB**
- **XAMPP** atau server database lain yang kompatibel

## Cara Menjalankan

### 1) Import database

1. Buat database baru dengan nama:

```sql
tofubase
```

2. Import file:

```text
src/main/java/database/tofubase.sql
```

3. Jika ingin mengisi data awal, jalankan file seeder yang tersedia di:

```text
src/main/java/database/seeder/
```

### 2) Sesuaikan koneksi database

Cek file berikut:

```text
src/main/java/utils/DatabaseConfig.java
```

Konfigurasi default yang dipakai project ini:

- Host: `localhost`
- Port: `3306`
- Database: `tofubase`
- User: `root`
- Password: ` `

Jika database kamu memakai kredensial berbeda, ubah bagian ini terlebih dahulu.

### 3) Jalankan aplikasi

Entry point utama ada di:

```text
src/main/java/App/TofuBaseApp.java
```

Jalankan class tersebut dari IDE seperti NetBeans atau IntelliJ IDEA.

## Seeder Database

Project ini memiliki tool untuk mengosongkan tabel lalu menjalankan seed data awal.

Class yang dipakai:

```text
src/main/java/tools/DatabaseSeeder.java
```

Seeder ini akan:

1. melakukan truncate pada tabel-tabel utama
2. menjalankan file SQL seed secara berurutan
3. mengisi data awal ke database

> Catatan: beberapa seeder modul lanjutan sudah tersedia, dan sebagian masih bisa diaktifkan sesuai kebutuhan pengembangan.

## Role User

Berdasarkan struktur data yang ada, sistem mendukung beberapa role:

- **Admin**
- **Owner**
- **Staff**

Contoh data akun awal tersedia pada file seeder `userSeeder.sql`, `adminSeeder.sql`, `ownerSeeder.sql`, dan `staffSeeder.sql`.

## Akun Demo

Jika menggunakan data hasil seeder, beberapa akun awal dapat ditemukan pada file berikut:

```text
database/seeder/userSeeder.sql
database/seeder/adminSeeder.sql
database/seeder/ownerSeeder.sql
database/seeder/staffSeeder.sql
```

Silakan sesuaikan username dan password sesuai data yang terdapat pada file seeder tersebut.

## Arsitektur Aplikasi

Project menerapkan pola pemisahan tanggung jawab (Separation of Concerns) dengan struktur:

- **Model** → representasi data
- **DAO** → akses database
- **Controller** → logika bisnis
- **View** → antarmuka pengguna (Swing)
- **Utils** → utilitas aplikasi
- **Components** → komponen UI kustom

Alur umum:

```text
View
 ↓
Controller
 ↓
DAO
 ↓
MySQL Database
```

## Roadmap Pengembangan

### Versi Saat Ini (v1)

- [x] Login multi-role
- [x] Dashboard ringkasan
- [x] Grafik produksi
- [x] Manajemen bahan baku
- [x] Riwayat bahan baku
- [x] Manajemen supplier
- [x] Riwayat pembelian bahan baku
- [x] Database seeder
- [x] Custom UI Components

### Pengembangan Selanjutnya (v2)

- [ ] Modul Produksi
  - [ ] CRUD data produksi
  - [ ] Riwayat produksi
  - [ ] Monitoring kapasitas produksi
- [ ] Modul Inventaris
  - [ ] Stok barang jadi
  - [ ] Mutasi inventaris
  - [ ] Notifikasi stok minimum
- [ ] Modul Pelanggan
  - [ ] Data pelanggan
  - [ ] Riwayat transaksi pelanggan

### Pengembangan Jangka Panjang (v3)

- [ ] Laporan Keuangan
- [ ] Export PDF
- [ ] Export Excel
- [ ] Manajemen User Lengkap
- [ ] Audit Log Aktivitas
- [ ] Backup & Restore Database
- [ ] Dark Mode

## Catatan Pengembangan

- Halaman utama yang sudah aktif di `MainFrame` saat ini mencakup **Dashboard** dan **Bahan Baku**.
- Modul lain seperti **Produksi**, **Stok & Distribusi**, **Laporan Keuangan**, dan **Kelola User** sudah tercermin pada struktur aplikasi/database dan dapat dilanjutkan pengembangannya.
- Tombol **Export PDF** pada menu bahan baku masih menampilkan informasi bahwa fiturnya sedang dikembangkan.

## Screenshot / Preview

### Halaman Login

<img width="1536" height="812" alt="Screenshot 2026-06-14 164528" src="https://github.com/user-attachments/assets/09b4b207-ed34-4349-bfe0-09ae6d23d650" />

### Dashboard

<img width="1536" height="815" alt="Screenshot 2026-06-14 164758" src="https://github.com/user-attachments/assets/531ee746-0a4d-42dc-8488-495a7f74f554" />

### Halaman Bahan Baku

<img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/214d96f4-a958-42c7-9bea-600561d58ece" />

- Modal Tambah / Riwayat / Edit

  <img width="1523" height="813" alt="image" src="https://github.com/user-attachments/assets/e02db73a-61d2-47e0-8057-b91df3692b6b" />

  <img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/5fd7820f-e28d-4a23-b6c6-776066fec0a6" />

  <img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/f84c060c-0d39-4b54-8890-3f979e0d8eed" />

### Halaman Supplier
<img width="1536" height="813" alt="image" src="https://github.com/user-attachments/assets/ccbc08eb-7273-43a9-99b2-bea63de56981" />

- Modal Tambah / Riwayat / Edit

  <img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/6339a06c-2c37-4617-af49-0e7f35418fd6" />

  <img width="1536" height="813" alt="image" src="https://github.com/user-attachments/assets/a474f6e5-3082-4f2e-a657-659466405111" />

  <img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/e3251068-5668-43ce-9cfc-8cc2f1384bb8" />


## Lisensi

Project ini dikembangkan untuk tujuan pembelajaran, pengembangan perangkat lunak, dan implementasi sistem informasi operasional pabrik tahu.

© 2026 TofuBase Team. All Rights Reserved.
