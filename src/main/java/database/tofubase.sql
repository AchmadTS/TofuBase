-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 08 Jun 2026 pada 07.06
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tofubase`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `admin`
--

CREATE TABLE `admin` (
  `id_admin` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `jabatan` varchar(50) NOT NULL,
  `level_akses` varchar(20) NOT NULL,
  `tanggal_dibuat` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `admin`
--

INSERT INTO `admin` (`id_admin`, `id_user`, `jabatan`, `level_akses`, `tanggal_dibuat`) VALUES
(1, 4, 'Administrator', 'admin', '2024-01-01 08:00:00');

-- --------------------------------------------------------

--
-- Struktur dari tabel `bahan_baku`
--

CREATE TABLE `bahan_baku` (
  `id_bahan` int(11) NOT NULL,
  `id_supplier` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `satuan` varchar(20) NOT NULL,
  `stok` double NOT NULL,
  `harga_beli` double NOT NULL,
  `min_stok` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `bahan_baku`
--

INSERT INTO `bahan_baku` (`id_bahan`, `id_supplier`, `nama`, `satuan`, `stok`, `harga_beli`, `min_stok`) VALUES
(1, 1, 'Kedelai', 'kg', 85, 12000, 100),
(2, 2, 'Garam', 'kg', 18, 5000, 5),
(3, 3, 'Kayu bakar', 'ikat', 32, 8000, 50),
(4, 2, 'Kunyit', 'kg', 5, 20000, 2),
(5, 3, 'Plastik', 'pcs', 800, 500, 500);

-- --------------------------------------------------------

--
-- Struktur dari tabel `inventaris`
--

CREATE TABLE `inventaris` (
  `id_inventaris` int(11) NOT NULL,
  `tanggal_cek` date NOT NULL,
  `keterangan` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `inventaris`
--

INSERT INTO `inventaris` (`id_inventaris`, `tanggal_cek`, `keterangan`) VALUES
(1, '2026-06-01', 'Inventaris awal bulan Juni 2026 - semua peralatan produksi dicek, kondisi normal'),
(2, '2026-06-08', 'Pengecekan mingguan - mesin giling normal, wajan produksi normal, plastik cukup');

-- --------------------------------------------------------

--
-- Struktur dari tabel `laporan_keuangan`
--

CREATE TABLE `laporan_keuangan` (
  `id_laporan` int(11) NOT NULL,
  `periode_awal` date NOT NULL,
  `periode_akhir` date NOT NULL,
  `total_pemasukan` double NOT NULL,
  `total_pengeluaran` double NOT NULL,
  `saldo` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `laporan_keuangan`
--

INSERT INTO `laporan_keuangan` (`id_laporan`, `periode_awal`, `periode_akhir`, `total_pemasukan`, `total_pengeluaran`, `saldo`) VALUES
(1, '2026-06-01', '2026-06-08', 4200000, 2260000, 1940000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `owner`
--

CREATE TABLE `owner` (
  `id_owner` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `jabatan` varchar(50) NOT NULL,
  `level_akses` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `owner`
--

INSERT INTO `owner` (`id_owner`, `id_user`, `jabatan`, `level_akses`) VALUES
(1, 1, 'Owner', 'owner');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id_pelanggan` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pelanggan`
--

INSERT INTO `pelanggan` (`id_pelanggan`, `nama`, `alamat`, `no_telp`, `email`) VALUES
(1, 'Warung Bu Ani', 'Jl. Cihampelas No. 10, Bandung', '081111111111', 'buani@gmail.com'),
(2, 'Toko Sembako Maju', 'Jl. Pasar Caringin No. 7, Bandung', '082222222222', 'sembakomaju@gmail.com'),
(3, 'Pasar Induk Bandung', 'Jl. Soekarno-Hatta No. 200, Bandung', '022-9876543', 'pasarinduk@bandung.go.id'),
(4, 'Ibu Sari', 'Jl. Sukajadi No. 45, Bandung', '083333333333', 'ibusari@gmail.com');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pemasukan`
--

CREATE TABLE `pemasukan` (
  `id_pemasukan` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `sumber` varchar(100) NOT NULL,
  `jumlah` double NOT NULL,
  `keterangan` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pemasukan`
--

INSERT INTO `pemasukan` (`id_pemasukan`, `id_penjualan`, `tanggal`, `sumber`, `jumlah`, `keterangan`) VALUES
(1, 1, '2026-06-01', 'Penjualan Tahu', 1000000, 'Penerimaan dari Pasar Induk Bandung'),
(2, 2, '2026-06-03', 'Penjualan Tahu', 600000, 'Penerimaan dari Warung Bu Ani'),
(3, 3, '2026-06-04', 'Penjualan Tahu', 450000, 'Penerimaan dari Toko Sembako Maju'),
(4, 4, '2026-06-05', 'Penjualan Tahu', 350000, 'Penerimaan dari Ibu Sari'),
(5, 5, '2026-06-06', 'Penjualan Tahu', 400000, 'Penerimaan dari Warung Bu Ani'),
(6, 6, '2026-06-07', 'Penjualan Tahu', 1400000, 'Penerimaan dari Pasar Induk Bandung');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengeluaran`
--

CREATE TABLE `pengeluaran` (
  `id_pengeluaran` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `kategori` varchar(100) NOT NULL,
  `deskripsi` text NOT NULL,
  `jumlah` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pengeluaran`
--

INSERT INTO `pengeluaran` (`id_pengeluaran`, `tanggal`, `kategori`, `deskripsi`, `jumlah`) VALUES
(1, '2026-06-01', 'Bahan Baku', 'Pembelian Kedelai 100 kg x Rp 12.000', 1200000),
(2, '2026-06-02', 'Bahan Baku', 'Pembelian Kayu Bakar 20 ikat x Rp 8.000', 160000),
(3, '2026-06-03', 'Bahan Baku', 'Pembelian Garam 10 kg x Rp 5.000', 50000),
(4, '2026-06-05', 'Operasional', 'Biaya listrik dan air bulan Juni 2026', 350000),
(5, '2026-06-07', 'Tenaga Kerja', 'Upah operator produksi minggu ke-1 Juni 2026', 500000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `penjualan`
--

CREATE TABLE `penjualan` (
  `id_penjualan` int(11) NOT NULL,
  `id_pelanggan` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `total` double NOT NULL,
  `keterangan` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `penjualan`
--

INSERT INTO `penjualan` (`id_penjualan`, `id_pelanggan`, `tanggal`, `total`, `keterangan`) VALUES
(1, 3, '2026-06-01', 1000000, 'Penjualan ke Pasar Induk - 500 potong tahu putih'),
(2, 1, '2026-06-03', 600000, 'Penjualan ke Warung Bu Ani - 300 potong tahu putih'),
(3, 2, '2026-06-04', 450000, 'Penjualan ke Toko Sembako Maju - tahu putih & kuning'),
(4, 4, '2026-06-05', 350000, 'Penjualan ke Ibu Sari - 175 potong tahu putih'),
(5, 1, '2026-06-06', 400000, 'Penjualan ke Warung Bu Ani - 200 potong tahu putih'),
(6, 3, '2026-06-07', 1400000, 'Penjualan ke Pasar Induk - tahu putih & kuning');

-- --------------------------------------------------------

--
-- Struktur dari tabel `produk`
--

CREATE TABLE `produk` (
  `id_produk` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `satuan` varchar(20) NOT NULL,
  `harga_jual` double NOT NULL,
  `stok` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `produk`
--

INSERT INTO `produk` (`id_produk`, `nama`, `satuan`, `harga_jual`, `stok`) VALUES
(1, 'Tahu Putih', 'potong', 2000, 180),
(2, 'Tahu Kuning', 'potong', 2500, 60);

-- --------------------------------------------------------

--
-- Struktur dari tabel `produksi`
--

CREATE TABLE `produksi` (
  `id_produksi` int(11) NOT NULL,
  `id_produk` int(11) NOT NULL,
  `batch` varchar(20) NOT NULL,
  `tanggal` date NOT NULL,
  `hasil_tahu` int(11) NOT NULL DEFAULT 0,
  `id_user` int(11) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Selesai',
  `keterangan` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `produksi`
--

INSERT INTO `produksi` (`id_produksi`, `id_produk`, `batch`, `tanggal`, `hasil_tahu`, `id_user`, `status`, `keterangan`) VALUES
(1, 1, '#B-041', '2026-03-11', 181, 2, 'Proses', 'Batch ke-41, tahu putih'),
(2, 1, '#B-042', '2026-03-12', 182, 2, 'Selesai', 'Batch ke-42, tahu putih'),
(3, 1, '#B-043', '2026-03-13', 183, 3, 'Selesai', 'Batch ke-43, tahu putih'),
(4, 1, '#B-044', '2026-03-14', 175, 3, 'Selesai', 'Batch ke-44, tahu putih'),
(5, 2, '#B-045', '2026-03-15', 60, 2, 'Selesai', 'Batch ke-45, tahu kuning'),
(6, 1, '#B-046', '2026-04-02', 190, 2, 'Selesai', 'Batch ke-46, tahu putih'),
(7, 1, '#B-047', '2026-04-09', 195, 3, 'Selesai', 'Batch ke-47, tahu putih'),
(8, 1, '#B-048', '2026-04-16', 200, 2, 'Selesai', 'Batch ke-48, tahu putih'),
(9, 2, '#B-049', '2026-04-23', 55, 3, 'Selesai', 'Batch ke-49, tahu kuning'),
(10, 1, '#B-050', '2026-05-05', 205, 2, 'Selesai', 'Batch ke-50, tahu putih'),
(11, 1, '#B-051', '2026-05-12', 210, 3, 'Selesai', 'Batch ke-51, tahu putih'),
(12, 1, '#B-052', '2026-05-19', 215, 2, 'Selesai', 'Batch ke-52, tahu putih'),
(13, 2, '#B-053', '2026-05-26', 65, 3, 'Selesai', 'Batch ke-53, tahu kuning'),
(14, 1, '#B-054', '2026-06-02', 160, 2, 'Selesai', 'Batch ke-54, tahu putih'),
(15, 1, '#B-055', '2026-06-03', 200, 3, 'Selesai', 'Batch ke-55, tahu putih'),
(16, 1, '#B-056', '2026-06-04', 140, 2, 'Selesai', 'Batch ke-56, tahu putih'),
(17, 1, '#B-057', '2026-06-05', 210, 3, 'Selesai', 'Batch ke-57, tahu putih'),
(18, 1, '#B-058', '2026-06-06', 180, 2, 'Selesai', 'Batch ke-58, tahu putih'),
(19, 1, '#B-059', '2026-06-07', 220, 3, 'Selesai', 'Batch ke-59, tahu putih'),
(20, 1, '#B-060', '2026-06-08', 240, 2, 'Selesai', 'Batch ke-60, tahu putih (hari ini)');

-- --------------------------------------------------------

--
-- Struktur dari tabel `record_penjualan`
--

CREATE TABLE `record_penjualan` (
  `id_record_penjualan` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `id_produk` int(11) NOT NULL,
  `jumlah` double NOT NULL,
  `harga` double NOT NULL,
  `subtotal` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `record_penjualan`
--

INSERT INTO `record_penjualan` (`id_record_penjualan`, `id_penjualan`, `id_produk`, `jumlah`, `harga`, `subtotal`) VALUES
(1, 1, 1, 500, 2000, 1000000),
(2, 2, 1, 300, 2000, 600000),
(3, 3, 1, 150, 2000, 300000),
(4, 3, 2, 60, 2500, 150000),
(5, 4, 1, 175, 2000, 350000),
(6, 5, 1, 200, 2000, 400000),
(7, 6, 1, 560, 2000, 1120000),
(8, 6, 2, 112, 2500, 280000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `record_produksi`
--

CREATE TABLE `record_produksi` (
  `id_record_produksi` int(11) NOT NULL,
  `id_produksi` int(11) NOT NULL,
  `id_bahan` int(11) NOT NULL,
  `jumlah` double NOT NULL,
  `satuan` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `record_produksi`
--

INSERT INTO `record_produksi` (`id_record_produksi`, `id_produksi`, `id_bahan`, `jumlah`, `satuan`) VALUES
(1, 1, 1, 21, 'kg'),
(2, 1, 2, 1.5, 'kg'),
(3, 2, 1, 22, 'kg'),
(4, 2, 2, 1.5, 'kg'),
(5, 3, 1, 23, 'kg'),
(6, 3, 2, 1.5, 'kg'),
(7, 4, 1, 21, 'kg'),
(8, 4, 2, 1.5, 'kg'),
(9, 5, 1, 8, 'kg'),
(10, 5, 2, 0.5, 'kg'),
(11, 5, 4, 0.3, 'kg'),
(12, 6, 1, 23, 'kg'),
(13, 6, 2, 1.5, 'kg'),
(14, 7, 1, 23.5, 'kg'),
(15, 7, 2, 1.5, 'kg'),
(16, 8, 1, 24, 'kg'),
(17, 8, 2, 2, 'kg'),
(18, 9, 1, 7, 'kg'),
(19, 9, 2, 0.5, 'kg'),
(20, 9, 4, 0.3, 'kg'),
(21, 10, 1, 25, 'kg'),
(22, 10, 2, 2, 'kg'),
(23, 11, 1, 25.5, 'kg'),
(24, 11, 2, 2, 'kg'),
(25, 12, 1, 26, 'kg'),
(26, 12, 2, 2, 'kg'),
(27, 13, 1, 8, 'kg'),
(28, 13, 2, 0.5, 'kg'),
(29, 13, 4, 0.4, 'kg'),
(30, 14, 1, 19.5, 'kg'),
(31, 14, 2, 1.5, 'kg'),
(32, 15, 1, 24, 'kg'),
(33, 15, 2, 2, 'kg'),
(34, 16, 1, 17, 'kg'),
(35, 16, 2, 1.5, 'kg'),
(36, 17, 1, 25.5, 'kg'),
(37, 17, 2, 2, 'kg'),
(38, 18, 1, 22, 'kg'),
(39, 18, 2, 1.5, 'kg'),
(40, 19, 1, 26.5, 'kg'),
(41, 19, 2, 2, 'kg'),
(42, 20, 1, 29, 'kg'),
(43, 20, 2, 2.5, 'kg');

-- --------------------------------------------------------

--
-- Struktur dari tabel `staff`
--

CREATE TABLE `staff` (
  `id_staff` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `jabatan` varchar(50) NOT NULL,
  `tanggal_masuk` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `staff`
--

INSERT INTO `staff` (`id_staff`, `id_user`, `jabatan`, `tanggal_masuk`) VALUES
(1, 2, 'Operator Produksi', '2024-01-15'),
(2, 3, 'Operator Produksi', '2024-03-01');

-- --------------------------------------------------------

--
-- Struktur dari tabel `supplier`
--

CREATE TABLE `supplier` (
  `id_supplier` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `supplier`
--

INSERT INTO `supplier` (`id_supplier`, `nama`, `alamat`, `no_telp`, `email`) VALUES
(1, 'CV Kedelai Makmur', 'Jl. Pasar Kosambi No. 12, Bandung', '022-1111222', 'kedelaimakmur@gmail.com'),
(2, 'Toko Garam & Rempah Jaya', 'Jl. Pasar Baru No. 5, Bandung', '022-2222333', 'garamjaya@gmail.com'),
(3, 'UD Kayu Bakar Lestari', 'Jl. Industri No. 33, Cimahi', '022-3333444', 'kayulestari@gmail.com');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`, `nama`, `email`, `no_telp`, `status`) VALUES
(1, 'achmadtirtosudiro', '3b374293f911eb01887ca0624afcdb40', 'Achmad Tirto Sudiro', 'achmadtirtosudirosudiro@gmail.com', '085878288920', 'aktif'),
(2, 'nadhifaalfadhilah', '9e32fdbf1a2bebd9cecb3760419e3638', 'Nadhifa Alfadhilah', 'nadhifaalfadhilah@gmail.com', '081281934400', 'aktif'),
(3, 'isyalamlamal-sabil', '04d43afb1c34c3e6a4640653262d5777', 'Isya Lam Lam Al-Sabil', 'isyalamlamalsabil@gmail.com', '082117081812', 'aktif'),
(4, 'satriyagalankmulyadi', '9aa669e62f20dce3b53ad6e5a165acac', 'Satriya Galank Mulyadi', 'satriyagalankmulyadi@gmail.com', '082148224132', 'aktif'),
(5, 'nasrudinpandutama', 'b4b870af4d585a88aa3eeac45ec8a0c1', 'Nasrudin Pandutama', 'nasrudinpandutama@gmail.com', '082135254176', 'aktif');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_admin`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  ADD PRIMARY KEY (`id_bahan`),
  ADD KEY `id_supplier` (`id_supplier`);

--
-- Indeks untuk tabel `inventaris`
--
ALTER TABLE `inventaris`
  ADD PRIMARY KEY (`id_inventaris`);

--
-- Indeks untuk tabel `laporan_keuangan`
--
ALTER TABLE `laporan_keuangan`
  ADD PRIMARY KEY (`id_laporan`);

--
-- Indeks untuk tabel `owner`
--
ALTER TABLE `owner`
  ADD PRIMARY KEY (`id_owner`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`);

--
-- Indeks untuk tabel `pemasukan`
--
ALTER TABLE `pemasukan`
  ADD PRIMARY KEY (`id_pemasukan`),
  ADD KEY `id_penjualan` (`id_penjualan`);

--
-- Indeks untuk tabel `pengeluaran`
--
ALTER TABLE `pengeluaran`
  ADD PRIMARY KEY (`id_pengeluaran`);

--
-- Indeks untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  ADD PRIMARY KEY (`id_penjualan`),
  ADD KEY `id_pelanggan` (`id_pelanggan`);

--
-- Indeks untuk tabel `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id_produk`);

--
-- Indeks untuk tabel `produksi`
--
ALTER TABLE `produksi`
  ADD PRIMARY KEY (`id_produksi`),
  ADD KEY `id_produk` (`id_produk`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `record_penjualan`
--
ALTER TABLE `record_penjualan`
  ADD PRIMARY KEY (`id_record_penjualan`),
  ADD KEY `id_penjualan` (`id_penjualan`),
  ADD KEY `id_produk` (`id_produk`);

--
-- Indeks untuk tabel `record_produksi`
--
ALTER TABLE `record_produksi`
  ADD PRIMARY KEY (`id_record_produksi`),
  ADD KEY `id_produksi` (`id_produksi`),
  ADD KEY `id_bahan` (`id_bahan`);

--
-- Indeks untuk tabel `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id_staff`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeks untuk tabel `supplier`
--
ALTER TABLE `supplier`
  ADD PRIMARY KEY (`id_supplier`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `admin`
--
ALTER TABLE `admin`
  MODIFY `id_admin` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  MODIFY `id_bahan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `inventaris`
--
ALTER TABLE `inventaris`
  MODIFY `id_inventaris` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `laporan_keuangan`
--
ALTER TABLE `laporan_keuangan`
  MODIFY `id_laporan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `owner`
--
ALTER TABLE `owner`
  MODIFY `id_owner` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  MODIFY `id_pelanggan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `pemasukan`
--
ALTER TABLE `pemasukan`
  MODIFY `id_pemasukan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `pengeluaran`
--
ALTER TABLE `pengeluaran`
  MODIFY `id_pengeluaran` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  MODIFY `id_penjualan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `produk`
--
ALTER TABLE `produk`
  MODIFY `id_produk` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `produksi`
--
ALTER TABLE `produksi`
  MODIFY `id_produksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT untuk tabel `record_penjualan`
--
ALTER TABLE `record_penjualan`
  MODIFY `id_record_penjualan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT untuk tabel `record_produksi`
--
ALTER TABLE `record_produksi`
  MODIFY `id_record_produksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT untuk tabel `staff`
--
ALTER TABLE `staff`
  MODIFY `id_staff` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `supplier`
--
ALTER TABLE `supplier`
  MODIFY `id_supplier` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);

--
-- Ketidakleluasaan untuk tabel `bahan_baku`
--
ALTER TABLE `bahan_baku`
  ADD CONSTRAINT `bahan_baku_ibfk_1` FOREIGN KEY (`id_supplier`) REFERENCES `supplier` (`id_supplier`);

--
-- Ketidakleluasaan untuk tabel `owner`
--
ALTER TABLE `owner`
  ADD CONSTRAINT `owner_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);

--
-- Ketidakleluasaan untuk tabel `pemasukan`
--
ALTER TABLE `pemasukan`
  ADD CONSTRAINT `pemasukan_ibfk_1` FOREIGN KEY (`id_penjualan`) REFERENCES `penjualan` (`id_penjualan`);

--
-- Ketidakleluasaan untuk tabel `penjualan`
--
ALTER TABLE `penjualan`
  ADD CONSTRAINT `penjualan_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`);

--
-- Ketidakleluasaan untuk tabel `produksi`
--
ALTER TABLE `produksi`
  ADD CONSTRAINT `produksi_ibfk_1` FOREIGN KEY (`id_produk`) REFERENCES `produk` (`id_produk`),
  ADD CONSTRAINT `produksi_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);

--
-- Ketidakleluasaan untuk tabel `record_penjualan`
--
ALTER TABLE `record_penjualan`
  ADD CONSTRAINT `record_penjualan_ibfk_1` FOREIGN KEY (`id_produk`) REFERENCES `produk` (`id_produk`),
  ADD CONSTRAINT `record_penjualan_ibfk_2` FOREIGN KEY (`id_penjualan`) REFERENCES `penjualan` (`id_penjualan`);

--
-- Ketidakleluasaan untuk tabel `record_produksi`
--
ALTER TABLE `record_produksi`
  ADD CONSTRAINT `record_produksi_ibfk_1` FOREIGN KEY (`id_bahan`) REFERENCES `bahan_baku` (`id_bahan`),
  ADD CONSTRAINT `record_produksi_ibfk_2` FOREIGN KEY (`id_produksi`) REFERENCES `produksi` (`id_produksi`);

--
-- Ketidakleluasaan untuk tabel `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `staff_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
