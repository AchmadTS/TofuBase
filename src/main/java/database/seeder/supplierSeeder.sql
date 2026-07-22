INSERT INTO supplier
    (id_supplier, nama, alamat, no_telp, email)
VALUES
    (
        1,
        'CV Kedelai Makmur',
        'Jl. Pasar Kosambi No. 12, Bandung',
        '022-1111222',
        'kedelaimakmur@gmail.com'
    ),
    (
        2,
        'Toko Garam & Rempah Jaya',
        'Jl. Pasar Baru No. 5, Bandung',
        '022-2222333',
        'garamjaya@gmail.com'
    ),
    (
        3,
        'UD Kayu Bakar Lestari',
        'Jl. Industri No. 33, Cimahi',
        '022-3333444',
        'kayulestari@gmail.com'
    );

INSERT INTO supplier
    (id_supplier, nama, alamat, no_telp, email)
SELECT
    n,

    CONCAT(
        CASE (n % 5)
            WHEN 0 THEN 'CV '
            WHEN 1 THEN 'UD '
            WHEN 2 THEN 'PT '
            WHEN 3 THEN 'Toko '
            ELSE 'Supplier '
        END,
        CASE (n % 8)
            WHEN 0 THEN 'Makmur '
            WHEN 1 THEN 'Jaya '
            WHEN 2 THEN 'Lestari '
            WHEN 3 THEN 'Abadi '
            WHEN 4 THEN 'Sejahtera '
            WHEN 5 THEN 'Berkah '
            WHEN 6 THEN 'Sentosa '
            ELSE 'Nusantara '
        END,
        LPAD(n::text, 6, '0')
    ) AS nama,

    CONCAT(
        'Jl. ',
        CASE (n % 10)
            WHEN 0 THEN 'Soekarno Hatta'
            WHEN 1 THEN 'Asia Afrika'
            WHEN 2 THEN 'Pasteur'
            WHEN 3 THEN 'Buah Batu'
            WHEN 4 THEN 'Cibiru'
            WHEN 5 THEN 'Antapani'
            WHEN 6 THEN 'Kopo'
            WHEN 7 THEN 'Rancaekek'
            WHEN 8 THEN 'Cimahi'
            ELSE 'Padalarang'
        END,
        ' No. ',
        ((n * 13) % 300) + 1,
        ', ',
        CASE (n % 4)
            WHEN 0 THEN 'Bandung'
            WHEN 1 THEN 'Cimahi'
            WHEN 2 THEN 'Karawang'
            ELSE 'Sumedang'
        END
    ) AS alamat,

    CONCAT(
        '08',
        LPAD(((n * 97) % 1000000000)::text, 9, '0')
    ) AS no_telp,

    CONCAT(
        'supplier',
        LPAD(n::text, 6, '0'),
        '@gmail.com'
    ) AS email

FROM generate_series(4, 100000) AS n;