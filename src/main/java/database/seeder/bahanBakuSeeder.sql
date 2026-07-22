INSERT INTO bahan_baku
    (id_bahan, id_supplier, nama, satuan, stok, harga_beli, min_stok)
VALUES
    (1, 1, 'Kedelai', 'kg', 85, 12000, 100),
    (2, 2, 'Garam', 'kg', 18, 5000, 5),
    (3, 3, 'Kayu bakar', 'ikat', 32, 8000, 50),
    (4, 2, 'Kunyit', 'kg', 5, 20000, 2),
    (5, 3, 'Plastik', 'pcs', 800, 500, 500);

INSERT INTO bahan_baku
    (id_bahan, id_supplier, nama, satuan, stok, harga_beli, min_stok)
SELECT
    n,

    CASE (n % 3)
        WHEN 0 THEN 1
        WHEN 1 THEN 2
        ELSE 3
    END AS id_supplier,

    CASE (n % 5)
        WHEN 0 THEN 'Kedelai ' || LPAD(n::text, 6, '0')
        WHEN 1 THEN 'Garam ' || LPAD(n::text, 6, '0')
        WHEN 2 THEN 'Kayu bakar ' || LPAD(n::text, 6, '0')
        WHEN 3 THEN 'Kunyit ' || LPAD(n::text, 6, '0')
        ELSE 'Plastik ' || LPAD(n::text, 6, '0')
    END AS nama,

    CASE (n % 4)
        WHEN 0 THEN 'kg'
        WHEN 1 THEN 'ikat'
        WHEN 2 THEN 'pcs'
        ELSE 'liter'
    END AS satuan,

    CASE (n % 5)
        WHEN 0 THEN 50 + ((n * 7) % 151)
        WHEN 1 THEN 10 + ((n * 3) % 91)
        WHEN 2 THEN 20 + ((n * 5) % 131)
        WHEN 3 THEN 5 + ((n * 11) % 46)
        ELSE 100 + ((n * 13) % 901)
    END AS stok,

    CASE (n % 5)
        WHEN 0 THEN 12000 + ((n * 23) % 8000)
        WHEN 1 THEN 5000 + ((n * 17) % 4000)
        WHEN 2 THEN 8000 + ((n * 19) % 7000)
        WHEN 3 THEN 20000 + ((n * 29) % 15000)
        ELSE 500 + ((n * 31) % 2500)
    END AS harga_beli,

    CASE (n % 5)
        WHEN 0 THEN 100
        WHEN 1 THEN 5
        WHEN 2 THEN 50
        WHEN 3 THEN 2
        ELSE 500
    END AS min_stok

FROM generate_series(6, 100000) AS n;