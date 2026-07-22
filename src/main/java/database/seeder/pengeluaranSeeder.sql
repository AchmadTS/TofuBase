INSERT INTO
  pengeluaran (
    id_pengeluaran,
    tanggal,
    kategori,
    deskripsi,
    jumlah
  )
VALUES
  (
    1,
    '2026-06-01',
    'Bahan Baku',
    'Pembelian Kedelai 100 kg x Rp 12.000',
    1200000
  ),
  (
    2,
    '2026-06-02',
    'Bahan Baku',
    'Pembelian Kayu Bakar 20 ikat x Rp 8.000',
    160000
  ),
  (
    3,
    '2026-06-03',
    'Bahan Baku',
    'Pembelian Garam 10 kg x Rp 5.000',
    50000
  ),
  (
    4,
    '2026-06-05',
    'Operasional',
    'Biaya listrik dan air bulan Juni 2026',
    350000
  ),
  (
    5,
    '2026-06-07',
    'Tenaga Kerja',
    'Upah operator produksi minggu ke-1 Juni 2026',
    500000
  );

INSERT INTO pengeluaran
(
    id_pengeluaran,
    tanggal,
    kategori,
    deskripsi,
    jumlah
)
SELECT
    n,

    DATE '2026-01-01' + (((n - 6) % 365) * INTERVAL '1 day') AS tanggal,

    CASE (n % 4)
        WHEN 0 THEN 'Bahan Baku'
        WHEN 1 THEN 'Operasional'
        WHEN 2 THEN 'Tenaga Kerja'
        ELSE 'Perawatan'
    END AS kategori,

    CASE (n % 8)
        WHEN 0 THEN 'Pembelian Kedelai ' ||
                    (((n::bigint * 7) % 250) + 1) ||
                    ' kg'

        WHEN 1 THEN 'Pembelian Garam ' ||
                    (((n::bigint * 3) % 50) + 1) ||
                    ' kg'

        WHEN 2 THEN 'Pembelian Kayu Bakar ' ||
                    (((n::bigint * 5) % 40) + 1) ||
                    ' ikat'

        WHEN 3 THEN 'Biaya listrik dan air periode ke-' ||
                    LPAD(n::text, 6, '0')

        WHEN 4 THEN 'Upah operator produksi periode ke-' ||
                    LPAD(n::text, 6, '0')

        WHEN 5 THEN 'Servis mesin produksi periode ke-' ||
                    LPAD(n::text, 6, '0')

        WHEN 6 THEN 'Pembelian plastik kemasan periode ke-' ||
                    LPAD(n::text, 6, '0')

        ELSE 'Biaya operasional lain periode ke-' ||
             LPAD(n::text, 6, '0')
    END AS deskripsi,

    CASE (n % 8)
        WHEN 0 THEN 1000000 + ((n::bigint * 23000) % 800000)
        WHEN 1 THEN  120000 + ((n::bigint * 1700) % 150000)
        WHEN 2 THEN   50000 + ((n::bigint * 1200) % 90000)
        WHEN 3 THEN  250000 + ((n::bigint * 2100) % 200000)
        WHEN 4 THEN  400000 + ((n::bigint * 2500) % 300000)
        WHEN 5 THEN  150000 + ((n::bigint * 1900) % 120000)
        WHEN 6 THEN   80000 + ((n::bigint * 1600) % 110000)
        ELSE           30000 + ((n::bigint * 900) % 70000)
    END AS jumlah

FROM generate_series(6, 100000) AS n;