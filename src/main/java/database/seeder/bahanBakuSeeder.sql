INSERT INTO bahan_baku
    (id_bahan, id_supplier, nama, satuan, stok, harga_beli, min_stok)
VALUES
    (1, 1, 'Kedelai', 'kg', 85, 12000, 100),
    (2, 2, 'Garam', 'kg', 18, 5000, 5),
    (3, 3, 'Kayu bakar', 'ikat', 32, 8000, 50),
    (4, 2, 'Kunyit', 'kg', 5, 20000, 2),
    (5, 3, 'Plastik', 'pcs', 800, 500, 500);

WITH bahan_gen AS (
    SELECT
        rn,
        ((rn - 1) % 10000) + 1 AS id_supplier,
        (((rn - 1) / 10000) % 2) + 1 AS variant,
        rn + 5 AS id_bahan
    FROM generate_series(1, 99995) AS rn
)
INSERT INTO bahan_baku
    (id_bahan, id_supplier, nama, satuan, stok, harga_beli, min_stok)
SELECT
    id_bahan,
    id_supplier,
    CASE (id_supplier % 5)
        WHEN 0 THEN CASE variant WHEN 1 THEN 'Kedelai' ELSE 'Kedelai Premium' END
        WHEN 1 THEN CASE variant WHEN 1 THEN 'Garam' ELSE 'Garam Halus' END
        WHEN 2 THEN CASE variant WHEN 1 THEN 'Kayu bakar' ELSE 'Kayu bakar Kering' END
        WHEN 3 THEN CASE variant WHEN 1 THEN 'Kunyit' ELSE 'Kunyit Bubuk' END
        ELSE       CASE variant WHEN 1 THEN 'Plastik' ELSE 'Plastik Kemasan' END
    END AS nama,
    CASE (id_supplier % 5)
        WHEN 0 THEN 'kg'
        WHEN 1 THEN 'kg'
        WHEN 2 THEN 'ikat'
        WHEN 3 THEN 'kg'
        ELSE 'pcs'
    END AS satuan,
    CASE variant
        WHEN 1 THEN
            CASE (id_supplier % 5)
                WHEN 0 THEN 50 + ((id_supplier::bigint * 7) % 151)
                WHEN 1 THEN 10 + ((id_supplier::bigint * 3) % 91)
                WHEN 2 THEN 20 + ((id_supplier::bigint * 5) % 131)
                WHEN 3 THEN 5 + ((id_supplier::bigint * 11) % 46)
                ELSE 100 + ((id_supplier::bigint * 13) % 901)
            END
        ELSE
            CASE (id_supplier % 5)
                WHEN 0 THEN 40 + ((id_supplier::bigint * 9) % 141)
                WHEN 1 THEN 8 + ((id_supplier::bigint * 4) % 83)
                WHEN 2 THEN 15 + ((id_supplier::bigint * 6) % 121)
                WHEN 3 THEN 4 + ((id_supplier::bigint * 10) % 41)
                ELSE 80 + ((id_supplier::bigint * 15) % 821)
            END
    END AS stok,
    CASE variant
        WHEN 1 THEN
            CASE (id_supplier % 5)
                WHEN 0 THEN 12000 + ((id_supplier::bigint * 23) % 8000)
                WHEN 1 THEN 5000 + ((id_supplier::bigint * 17) % 4000)
                WHEN 2 THEN 8000 + ((id_supplier::bigint * 19) % 7000)
                WHEN 3 THEN 20000 + ((id_supplier::bigint * 29) % 15000)
                ELSE 500 + ((id_supplier::bigint * 31) % 2500)
            END
        ELSE
            CASE (id_supplier % 5)
                WHEN 0 THEN 11000 + ((id_supplier::bigint * 21) % 7000)
                WHEN 1 THEN 4800 + ((id_supplier::bigint * 15) % 3500)
                WHEN 2 THEN 7600 + ((id_supplier::bigint * 17) % 6500)
                WHEN 3 THEN 18000 + ((id_supplier::bigint * 27) % 14000)
                ELSE 450 + ((id_supplier::bigint * 29) % 2200)
            END
    END AS harga_beli,
    CASE (id_supplier % 5)
        WHEN 0 THEN 100
        WHEN 1 THEN 5
        WHEN 2 THEN 50
        WHEN 3 THEN 2
        ELSE 500
    END AS min_stok
FROM bahan_gen;