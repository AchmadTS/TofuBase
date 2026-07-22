INSERT INTO pelanggan
    (id_pelanggan, nama, alamat, no_telp, email)
VALUES
    (1, 'Warung Bu Ani', 'Jl. Cihampelas No. 10, Bandung', '081111111111', 'buani@gmail.com'),
    (2, 'Toko Sembako Maju', 'Jl. Pasar Caringin No. 7, Bandung', '082222222222', 'sembakomaju@gmail.com'),
    (3, 'Pasar Induk Bandung', 'Jl. Soekarno-Hatta No. 200, Bandung', '022-9876543', 'pasarinduk@bandung.go.id'),
    (4, 'Ibu Sari', 'Jl. Sukajadi No. 45, Bandung', '083333333333', 'ibusari@gmail.com');

INSERT INTO pelanggan
    (id_pelanggan, nama, alamat, no_telp, email)
SELECT
    n,

    CONCAT(
        CASE (n % 6)
            WHEN 0 THEN 'Warung '
            WHEN 1 THEN 'Toko '
            WHEN 2 THEN 'PT '
            WHEN 3 THEN 'CV '
            WHEN 4 THEN 'UD '
            ELSE 'Distributor '
        END,
        CASE (n % 8)
            WHEN 0 THEN 'Maju Jaya '
            WHEN 1 THEN 'Sari Rasa '
            WHEN 2 THEN 'Berkah Abadi '
            WHEN 3 THEN 'Sejahtera '
            WHEN 4 THEN 'Makmur '
            WHEN 5 THEN 'Sentosa '
            WHEN 6 THEN 'Prima '
            ELSE 'Jaya Bersama '
        END,
        LPAD(n::text, 6, '0')
    ) AS nama,

    CONCAT(
        'Jl. ',
        CASE (n % 10)
            WHEN 0 THEN 'Asia Afrika'
            WHEN 1 THEN 'Dago'
            WHEN 2 THEN 'Riau'
            WHEN 3 THEN 'Antapani'
            WHEN 4 THEN 'Soekarno-Hatta'
            WHEN 5 THEN 'Cimahi'
            WHEN 6 THEN 'Pasteur'
            WHEN 7 THEN 'Kopo'
            WHEN 8 THEN 'Buah Batu'
            ELSE 'Sukajadi'
        END,
        ' No. ',
        ((n * 7) % 250) + 1,
        ', Bandung'
    ) AS alamat,

    CONCAT(
        '08',
        LPAD((((n * 37) % 900000000))::text, 9, '0')
    ) AS no_telp,

    CONCAT(
        LOWER(
            REPLACE(
                CASE (n % 6)
                    WHEN 0 THEN 'warung'
                    WHEN 1 THEN 'toko'
                    WHEN 2 THEN 'pt'
                    WHEN 3 THEN 'cv'
                    WHEN 4 THEN 'ud'
                    ELSE 'distributor'
                END,
                ' ',
                ''
            )
        ),
        LPAD(n::text, 6, '0'),
        '@example.com'
    ) AS email

FROM generate_series(5, 100000) AS n;