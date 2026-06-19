# TofuBase

![Java](https://img.shields.io/badge/Java-25-orange)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Swing](https://img.shields.io/badge/Java-Swing-green)
![Status](https://img.shields.io/badge/Status-Beta-success)

**TofuBase** adalah aplikasi desktop berbasis **Java Swing** dan **MySQL** yang dirancang untuk membantu pengelolaan operasional pabrik tahu secara terintegrasi. Aplikasi ini menyediakan sistem autentikasi berbasis role, dashboard monitoring, manajemen bahan baku, supplier, produk, pelanggan, penjualan, produksi, inventaris, keuangan, serta manajemen pengguna. Dengan dukungan visualisasi data, pencatatan aktivitas, dan struktur database yang terorganisir, TofuBase membantu meningkatkan efisiensi pengelolaan data dan proses bisnis operasional pabrik tahu.

## Fitur Utama

### Autentikasi Berbasis Role

- Login untuk role `Admin`, `Owner`, dan `Staff`
- Hak akses disesuaikan berdasarkan peran pengguna

### Dashboard Monitoring

- Kartu ringkasan data operasional
- Grafik produksi berdasarkan rentang waktu
- Status stok bahan baku
- Riwayat aktivitas terbaru
- Auto-refresh data

### Manajemen Bahan Baku

- Daftar stok bahan baku
- Pencarian data
- Pagination
- Tambah data
- Edit data
- Riwayat perubahan bahan baku

### Manajemen Supplier

- Daftar supplier
- Pencarian data supplier
- Tambah supplier
- Edit supplier
- Riwayat perubahan supplier

### Manajemen Produk

- Daftar produk
- Tambah produk
- Edit produk
- Riwayat produk

### Manajemen Pelanggan

- Daftar pelanggan
- Tambah pelanggan
- Edit pelanggan

### Manajemen Penjualan

- Pencatatan transaksi penjualan
- Monitoring data penjualan
- Riwayat penjualan

### Manajemen Produksi

- Data produksi
- Monitoring produksi
- Riwayat produksi

### Manajemen Inventaris

- Monitoring inventaris
- Status stok inventaris

### Manajemen Keuangan

- Data pemasukan
- Data pengeluaran
- Laporan keuangan

### Manajemen User

- Kelola data pengguna
- Pengaturan role pengguna

### Activity Logging

- Pencatatan aktivitas pengguna secara otomatis
- Riwayat aktivitas pada dashboard
- Tabel aktivitas dengan aksi edit dan hapus

### Antarmuka Modern

- Sidebar navigasi
- Rounded panel
- Custom scrollbar
- Tabel interaktif
- Konsistensi tema dan komponen UI

### Database Seeder

- Mengosongkan tabel utama sebelum proses seed ulang
- Mengisi data awal aplikasi secara otomatis
- Mempermudah proses setup dan pengujian

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
- [x] Dashboard monitoring
- [x] Grafik produksi
- [x] Status stok bahan baku
- [x] Activity logging
- [x] Manajemen bahan baku
- [x] Manajemen supplier
- [x] Manajemen produk
- [x] Manajemen pelanggan
- [x] Manajemen penjualan
- [x] Manajemen produksi
- [x] Manajemen inventaris
- [x] Manajemen pemasukan
- [x] Manajemen pengeluaran
- [x] Laporan keuangan
- [x] Manajemen user
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

## Catatan Pengembangan

Halaman yang telah aktif pada aplikasi saat ini meliputi:

- Dashboard
- Bahan Baku
- Produk
- Pelanggan
- Penjualan
- Pemasukan
- Pengeluaran
- Inventaris
- Produksi
- Laporan Keuangan
- Kelola User
- Supplier

Beberapa fitur lanjutan seperti Export PDF, Export Excel, dan Backup Database.

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

### Halaman Produk
<img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/eed2240c-09e5-433b-9424-5e27841ad307" />

- Modal Tambah

  <img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/328509d8-2ea8-4ea3-9350-c9c4f26fd4f3" />

### Halaman Pelanggan
<img width="1536" height="815" alt="Screenshot 2026-06-19 125524" src="https://github.com/user-attachments/assets/14ede6e3-b09f-4254-a025-5d69322c22e8" />

- Modal Tambah

  <img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/2c826aa7-8e0f-43b6-87b2-fb0ead83e040" />

### Halaman Penjualan
<img width="1536" height="812" alt="image" src="https://github.com/user-attachments/assets/2634dc75-24b9-4e16-ab12-f19f2986b128" />

### Halaman Produksi
<img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/42d65eb6-74a4-40bc-9f9a-c2dc557f6dde" />

- Modal Tambah

  <img width="1536" height="813" alt="image" src="https://github.com/user-attachments/assets/f59b5ba0-c7c2-4a71-ae60-7240e2cfc889" />

### Halaman Inventaris
<img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/5393365f-4fb3-4f03-b7d0-ee88732a5708" />

- Modal Tambah

  <img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/c8527f7c-5248-4c31-83cb-09d015ae9d45" />

### Halaman Laporan Keuangan
<img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/ae32c7c1-cc58-4728-b199-11cc71fc06ff" />

- Modal Export PDF

  <img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/34ed3abc-5074-4d73-8d9c-0f3c77f5fc61" />

### Halaman Pemasukan
<img width="1536" height="815" alt="image" src="https://github.com/user-attachments/assets/c3d4cd04-5e0b-4c37-84a3-a5d0f7e55f20" />

- Modal Tambah

  <img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/45e51b34-4378-47c8-b653-fa932228e193" />

### Halaman Pengeluaran
<img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/e0df9959-81b8-4ae9-8832-6ab008b294b2" />

- Modal Tambah

  <img width="1536" height="816" alt="image" src="https://github.com/user-attachments/assets/014b4275-9695-4176-9f69-8609900a0f99" />

### Halaman Kelola User
<img width="1536" height="813" alt="image" src="https://github.com/user-attachments/assets/3339fc3c-4496-4b57-b1f5-19448c0654b7" />

## Lisensi

Project ini dikembangkan untuk tujuan pembelajaran, pengembangan perangkat lunak, dan implementasi sistem informasi operasional pabrik tahu.

© 2026 TofuBase Team. All Rights Reserved.
