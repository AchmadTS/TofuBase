INSERT INTO
  penjualan (
    id_penjualan,
    id_pelanggan,
    tanggal,
    total,
    keterangan
  )
VALUES
  (
    1,
    3,
    '2026-06-01',
    1000000,
    'Penjualan ke Pasar Induk - 500 potong tahu putih'
  ),
  (
    2,
    1,
    '2026-06-03',
    600000,
    'Penjualan ke Warung Bu Ani - 300 potong tahu putih'
  ),
  (
    3,
    2,
    '2026-06-04',
    450000,
    'Penjualan ke Toko Sembako Maju - tahu putih & kuning'
  ),
  (
    4,
    4,
    '2026-06-05',
    350000,
    'Penjualan ke Ibu Sari - 175 potong tahu putih'
  ),
  (
    5,
    1,
    '2026-06-06',
    400000,
    'Penjualan ke Warung Bu Ani - 200 potong tahu putih'
  ),
  (
    6,
    3,
    '2026-06-07',
    1400000,
    'Penjualan ke Pasar Induk - tahu putih & kuning'
  );

INSERT INTO penjualan
(
    id_penjualan,
    id_pelanggan,
    tanggal,
    total,
    keterangan
)
SELECT
    n,

    ((n - 1) % 4) + 1 AS id_pelanggan,

    DATE '2026-01-01' + (((n - 7) % 365) * INTERVAL '1 day') AS tanggal,

    CASE (n % 6)
        WHEN 0 THEN 1000000 + (((n::bigint * 23000) % 700000))
        WHEN 1 THEN  600000 + (((n::bigint * 17000) % 500000))
        WHEN 2 THEN  450000 + (((n::bigint * 14000) % 400000))
        WHEN 3 THEN  350000 + (((n::bigint * 12000) % 300000))
        WHEN 4 THEN  400000 + (((n::bigint * 15000) % 450000))
        ELSE         1400000 + (((n::bigint * 25000) % 900000))
    END AS total,

    CASE (n % 6)
        WHEN 0 THEN 'Penjualan ke Pasar Induk - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 1 THEN 'Penjualan ke Warung Bu Ani - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 2 THEN 'Penjualan ke Toko Sembako Maju - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 3 THEN 'Penjualan ke Ibu Sari - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 4 THEN 'Penjualan ke pelanggan umum - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        ELSE 'Penjualan ke distributor - transaksi ke-' ||
             LPAD(n::text, 6, '0')
    END AS keterangan

FROM generate_series(7, 100000) AS n;