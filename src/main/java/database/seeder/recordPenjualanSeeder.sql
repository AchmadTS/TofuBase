INSERT INTO
  record_penjualan (
    id_record_penjualan,
    id_penjualan,
    id_produk,
    jumlah,
    harga,
    subtotal
  )
VALUES
  (1, 1, 1, 500, 2000, 1000000),
  (2, 2, 1, 300, 2000, 600000),
  (3, 3, 1, 150, 2000, 300000),
  (4, 3, 2, 60, 2500, 150000),
  (5, 4, 1, 175, 2000, 350000),
  (6, 5, 1, 200, 2000, 400000),
  (7, 6, 1, 560, 2000, 1120000),
  (8, 6, 2, 112, 2500, 280000);

INSERT INTO
  record_penjualan (
    id_record_penjualan,
    id_penjualan,
    id_produk,
    jumlah,
    harga,
    subtotal
  )
SELECT
  n AS id_record_penjualan,
  n AS id_penjualan,
  CASE MOD(n, 6)
    WHEN 0 THEN 1
    WHEN 1 THEN 2
    WHEN 2 THEN 1
    WHEN 3 THEN 2
    WHEN 4 THEN 1
    ELSE 2
  END AS id_produk,
  CASE MOD(n, 6)
    WHEN 0 THEN 120 + MOD(n * 7, 420)
    WHEN 1 THEN 80 + MOD(n * 5, 260)
    WHEN 2 THEN 150 + MOD(n * 3, 300)
    WHEN 3 THEN 60 + MOD(n * 9, 180)
    WHEN 4 THEN 200 + MOD(n * 11, 500)
    ELSE 100 + MOD(n * 13, 350)
  END AS jumlah,
  CASE MOD(n, 2)
    WHEN 0 THEN 2000
    ELSE 2500
  END AS harga,
  (
    CASE MOD(n, 6)
      WHEN 0 THEN 120 + MOD(n * 7, 420)
      WHEN 1 THEN 80 + MOD(n * 5, 260)
      WHEN 2 THEN 150 + MOD(n * 3, 300)
      WHEN 3 THEN 60 + MOD(n * 9, 180)
      WHEN 4 THEN 200 + MOD(n * 11, 500)
      ELSE 100 + MOD(n * 13, 350)
    END
  ) * (
    CASE MOD(n, 2)
      WHEN 0 THEN 2000
      ELSE 2500
    END
  ) AS subtotal
FROM
  (
    SELECT
      seq + 9 AS n
    FROM
      (
        SELECT
          o.n + t.n * 10 + h.n * 100 + th.n * 1000 + tt.n * 10000 AS seq
        FROM
          (
            SELECT
              0 AS n
            UNION ALL
            SELECT
              1
            UNION ALL
            SELECT
              2
            UNION ALL
            SELECT
              3
            UNION ALL
            SELECT
              4
            UNION ALL
            SELECT
              5
            UNION ALL
            SELECT
              6
            UNION ALL
            SELECT
              7
            UNION ALL
            SELECT
              8
            UNION ALL
            SELECT
              9
          ) o
          CROSS JOIN (
            SELECT
              0 AS n
            UNION ALL
            SELECT
              1
            UNION ALL
            SELECT
              2
            UNION ALL
            SELECT
              3
            UNION ALL
            SELECT
              4
            UNION ALL
            SELECT
              5
            UNION ALL
            SELECT
              6
            UNION ALL
            SELECT
              7
            UNION ALL
            SELECT
              8
            UNION ALL
            SELECT
              9
          ) t
          CROSS JOIN (
            SELECT
              0 AS n
            UNION ALL
            SELECT
              1
            UNION ALL
            SELECT
              2
            UNION ALL
            SELECT
              3
            UNION ALL
            SELECT
              4
            UNION ALL
            SELECT
              5
            UNION ALL
            SELECT
              6
            UNION ALL
            SELECT
              7
            UNION ALL
            SELECT
              8
            UNION ALL
            SELECT
              9
          ) h
          CROSS JOIN (
            SELECT
              0 AS n
            UNION ALL
            SELECT
              1
            UNION ALL
            SELECT
              2
            UNION ALL
            SELECT
              3
            UNION ALL
            SELECT
              4
            UNION ALL
            SELECT
              5
            UNION ALL
            SELECT
              6
            UNION ALL
            SELECT
              7
            UNION ALL
            SELECT
              8
            UNION ALL
            SELECT
              9
          ) th
          CROSS JOIN (
            SELECT
              0 AS n
            UNION ALL
            SELECT
              1
            UNION ALL
            SELECT
              2
            UNION ALL
            SELECT
              3
            UNION ALL
            SELECT
              4
            UNION ALL
            SELECT
              5
            UNION ALL
            SELECT
              6
            UNION ALL
            SELECT
              7
            UNION ALL
            SELECT
              8
            UNION ALL
            SELECT
              9
          ) tt
      ) angka
    WHERE
      seq < 99992
  ) data;