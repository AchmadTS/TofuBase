INSERT INTO
  record_produksi (
    id_record_produksi,
    id_produksi,
    id_bahan,
    jumlah,
    satuan
  )
VALUES
  (1, 1, 1, 21.0, 'kg'),
  (2, 1, 2, 1.5, 'kg'),
  (3, 2, 1, 22.0, 'kg'),
  (4, 2, 2, 1.5, 'kg'),
  (5, 3, 1, 23.0, 'kg'),
  (6, 3, 2, 1.5, 'kg'),
  (7, 4, 1, 21.0, 'kg'),
  (8, 4, 2, 1.5, 'kg'),
  (9, 5, 1, 8.0, 'kg'),
  (10, 5, 2, 0.5, 'kg'),
  (11, 5, 4, 0.3, 'kg'),
  (12, 6, 1, 23.0, 'kg'),
  (13, 6, 2, 1.5, 'kg'),
  (14, 7, 1, 23.5, 'kg'),
  (15, 7, 2, 1.5, 'kg'),
  (16, 8, 1, 24.0, 'kg'),
  (17, 8, 2, 2.0, 'kg'),
  (18, 9, 1, 7.0, 'kg'),
  (19, 9, 2, 0.5, 'kg'),
  (20, 9, 4, 0.3, 'kg'),
  (21, 10, 1, 25.0, 'kg'),
  (22, 10, 2, 2.0, 'kg'),
  (23, 11, 1, 25.5, 'kg'),
  (24, 11, 2, 2.0, 'kg'),
  (25, 12, 1, 26.0, 'kg'),
  (26, 12, 2, 2.0, 'kg'),
  (27, 13, 1, 8.0, 'kg'),
  (28, 13, 2, 0.5, 'kg'),
  (29, 13, 4, 0.4, 'kg'),
  (30, 14, 1, 19.5, 'kg'),
  (31, 14, 2, 1.5, 'kg'),
  (32, 15, 1, 24.0, 'kg'),
  (33, 15, 2, 2.0, 'kg'),
  (34, 16, 1, 17.0, 'kg'),
  (35, 16, 2, 1.5, 'kg'),
  (36, 17, 1, 25.5, 'kg'),
  (37, 17, 2, 2.0, 'kg'),
  (38, 18, 1, 22.0, 'kg'),
  (39, 18, 2, 1.5, 'kg'),
  (40, 19, 1, 26.5, 'kg'),
  (41, 19, 2, 2.0, 'kg'),
  (42, 20, 1, 29.0, 'kg'),
  (43, 20, 2, 2.5, 'kg');

INSERT INTO
  record_produksi (
    id_record_produksi,
    id_produksi,
    id_bahan,
    jumlah,
    satuan
  )
SELECT
  n AS id_record_produksi,
  MOD(n - 44, 100000) + 1 AS id_produksi,
  CASE MOD(n, 5)
    WHEN 0 THEN 1
    WHEN 1 THEN 2
    WHEN 2 THEN 3
    WHEN 3 THEN 4
    ELSE 5
  END AS id_bahan,
  ROUND(
    CASE MOD(n, 5)
      WHEN 0 THEN 20 + MOD(n * 7, 10)
      WHEN 1 THEN 1 + MOD(n * 3, 20) / 10
      WHEN 2 THEN 5 + MOD(n * 5, 15)
      WHEN 3 THEN 0.2 + MOD(n * 2, 5) / 10
      ELSE 100 + MOD(n * 11, 400)
    END,
    1
  ) AS jumlah,
  CASE MOD(n, 5)
    WHEN 0 THEN 'kg'
    WHEN 1 THEN 'kg'
    WHEN 2 THEN 'ikat'
    WHEN 3 THEN 'kg'
    ELSE 'pcs'
  END AS satuan
FROM
  (
    SELECT
      seq + 44 AS n
    FROM
      (
        SELECT
          o.n + t.n * 10 + h.n * 100 + th.n * 1000 + tt.n * 10000 AS seq
        FROM
          (
            SELECT
              0 n
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
              0 n
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
              0 n
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
              0 n
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
              0 n
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
      seq < 99957
  ) data;