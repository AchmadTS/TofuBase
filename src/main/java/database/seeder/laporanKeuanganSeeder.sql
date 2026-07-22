INSERT INTO
  laporan_keuangan (
    id_laporan,
    periode_awal,
    periode_akhir,
    total_pemasukan,
    total_pengeluaran,
    saldo
  )
VALUES
  (
    1,
    '2026-06-01',
    '2026-06-08',
    4200000,
    2260000,
    1940000
  );

INSERT INTO laporan_keuangan
(
    id_laporan,
    periode_awal,
    periode_akhir,
    total_pemasukan,
    total_pengeluaran,
    saldo
)
SELECT
    n,

    DATE '2026-01-01'
        + (((n - 2) % 365) * INTERVAL '1 day') AS periode_awal,

    DATE '2026-01-01'
        + (((n - 2) % 365) * INTERVAL '1 day')
        + INTERVAL '7 day' AS periode_akhir,

    3000000 + ((n::bigint * 25000) % 7000000) AS total_pemasukan,

    1000000 + ((n::bigint * 18000) % 5000000) AS total_pengeluaran,

    (3000000 + ((n::bigint * 25000) % 7000000))
    -
    (1000000 + ((n::bigint * 18000) % 5000000)) AS saldo

FROM generate_series(2, 100000) AS n;