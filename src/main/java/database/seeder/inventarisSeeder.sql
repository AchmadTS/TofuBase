INSERT INTO
  inventaris (id_inventaris, tanggal_cek, keterangan)
VALUES
  (
    1,
    '2026-06-01',
    'Inventaris awal bulan Juni 2026 - semua peralatan produksi dicek, kondisi normal'
  ),
  (
    2,
    '2026-06-08',
    'Pengecekan mingguan - mesin giling normal, wajan produksi normal, plastik cukup'
  );

INSERT INTO inventaris
(
    id_inventaris,
    tanggal_cek,
    keterangan
)
SELECT
    n,

    DATE '2026-06-03'
        + (((n - 3) % 365) * INTERVAL '1 day') AS tanggal_cek,

    CASE (n % 5)
        WHEN 0 THEN
            'Pengecekan rutin - kondisi inventaris normal, data ke-' ||
            LPAD(n::text, 6, '0')

        WHEN 1 THEN
            'Pemeriksaan berkala - beberapa item perlu perhatian, data ke-' ||
            LPAD(n::text, 6, '0')

        WHEN 2 THEN
            'Audit inventaris - stok alat produksi sesuai, data ke-' ||
            LPAD(n::text, 6, '0')

        WHEN 3 THEN
            'Monitoring inventaris - tidak ada kerusakan berarti, data ke-' ||
            LPAD(n::text, 6, '0')

        ELSE
            'Pengecekan harian - seluruh perlengkapan dalam kondisi baik, data ke-' ||
            LPAD(n::text, 6, '0')
    END AS keterangan

FROM generate_series(3, 100000) AS n;