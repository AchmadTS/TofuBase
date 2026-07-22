INSERT INTO produk
    (id_produk, nama, satuan, harga_jual, jenis, stok)
VALUES
    (1, 'Tahu Putih', 'potong', 2000, 'Tahu Putih', 180),
    (2, 'Tahu Kuning', 'potong', 2500, 'Tahu Kuning', 60);

INSERT INTO produk
    (id_produk, nama, satuan, harga_jual, jenis, stok)
SELECT
    n,

    CASE (n % 6)
        WHEN 0 THEN 'Tahu Putih '  || LPAD(n::text, 6, '0')
        WHEN 1 THEN 'Tahu Kuning ' || LPAD(n::text, 6, '0')
        WHEN 2 THEN 'Tahu Goreng ' || LPAD(n::text, 6, '0')
        WHEN 3 THEN 'Tempe '       || LPAD(n::text, 6, '0')
        WHEN 4 THEN 'Ongol-ongol ' || LPAD(n::text, 6, '0')
        ELSE      'Produk '        || LPAD(n::text, 6, '0')
    END,

    CASE (n % 4)
        WHEN 0 THEN 'potong'
        WHEN 1 THEN 'pack'
        WHEN 2 THEN 'pcs'
        ELSE 'bungkus'
    END,

    CASE (n % 6)
        WHEN 0 THEN 2000 + ((n * 17) % 1500)
        WHEN 1 THEN 2500 + ((n * 19) % 1800)
        WHEN 2 THEN 3000 + ((n * 23) % 2000)
        WHEN 3 THEN 1500 + ((n * 13) % 1200)
        WHEN 4 THEN 4000 + ((n * 29) % 2500)
        ELSE 1000 + ((n * 11) % 900)
    END,

    CASE (n % 6)
        WHEN 0 THEN 'Tahu Putih'
        WHEN 1 THEN 'Tahu Kuning'
        WHEN 2 THEN 'Tahu Goreng'
        WHEN 3 THEN 'Tempe'
        WHEN 4 THEN 'Ongol-ongol'
        ELSE 'Produk Lain'
    END,

    CASE (n % 6)
        WHEN 0 THEN 50 + ((n * 7) % 500)
        WHEN 1 THEN 30 + ((n * 5) % 300)
        WHEN 2 THEN 20 + ((n * 9) % 250)
        WHEN 3 THEN 15 + ((n * 11) % 200)
        WHEN 4 THEN 40 + ((n * 13) % 400)
        ELSE 10 + ((n * 3) % 150)
    END

FROM generate_series(3, 100000) AS n;