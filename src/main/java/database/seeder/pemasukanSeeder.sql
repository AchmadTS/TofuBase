INSERT INTO
  pemasukan (
    id_pemasukan,
    id_penjualan,
    tanggal,
    sumber,
    jumlah,
    keterangan
  )
VALUES
  (
    1,
    1,
    '2026-06-01',
    'Penjualan Tahu',
    1000000,
    'Penerimaan dari Pasar Induk Bandung'
  ),
  (
    2,
    2,
    '2026-06-03',
    'Penjualan Tahu',
    600000,
    'Penerimaan dari Warung Bu Ani'
  ),
  (
    3,
    3,
    '2026-06-04',
    'Penjualan Tahu',
    450000,
    'Penerimaan dari Toko Sembako Maju'
  ),
  (
    4,
    4,
    '2026-06-05',
    'Penjualan Tahu',
    350000,
    'Penerimaan dari Ibu Sari'
  ),
  (
    5,
    5,
    '2026-06-06',
    'Penjualan Tahu',
    400000,
    'Penerimaan dari Warung Bu Ani'
  ),
  (
    6,
    6,
    '2026-06-07',
    'Penjualan Tahu',
    1400000,
    'Penerimaan dari Pasar Induk Bandung'
  );

INSERT INTO pemasukan
(
    id_pemasukan,
    id_penjualan,
    tanggal,
    sumber,
    jumlah,
    keterangan
)
SELECT
    n,

    n AS id_penjualan,

    DATE '2026-01-01' + (((n - 7) % 365) * INTERVAL '1 day') AS tanggal,

    CASE (n % 4)
        WHEN 0 THEN 'Penjualan Tahu'
        WHEN 1 THEN 'Penjualan Tempe'
        WHEN 2 THEN 'Penjualan Gorengan'
        ELSE 'Penjualan Produk Lain'
    END AS sumber,

    CASE (n % 6)
        WHEN 0 THEN 1000000 + ((n::bigint * 23000) % 700000)
        WHEN 1 THEN  600000 + ((n::bigint * 17000) % 500000)
        WHEN 2 THEN  450000 + ((n::bigint * 14000) % 400000)
        WHEN 3 THEN  350000 + ((n::bigint * 12000) % 300000)
        WHEN 4 THEN  400000 + ((n::bigint * 15000) % 450000)
        ELSE         1400000 + ((n::bigint * 25000) % 900000)
    END AS jumlah,

    CASE (n % 6)
        WHEN 0 THEN 'Penerimaan dari Pasar Induk Bandung - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 1 THEN 'Penerimaan dari Warung Bu Ani - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 2 THEN 'Penerimaan dari Toko Sembako Maju - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 3 THEN 'Penerimaan dari Ibu Sari - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        WHEN 4 THEN 'Penerimaan dari pelanggan umum - transaksi ke-' ||
                    LPAD(n::text, 6, '0')
        ELSE 'Penerimaan dari distributor - transaksi ke-' ||
             LPAD(n::text, 6, '0')
    END AS keterangan

FROM generate_series(7, 100000) AS n;