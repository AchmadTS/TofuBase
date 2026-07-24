--
-- PostgreSQL database dump
--

\restrict SaNQMuJTMyic2SEFcTfkXtEyKXSYYSBRHCSNeNLeRdxikxxEgQ4eGXOpu8aZSLe

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2026-07-24 10:35:24

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 2 (class 3079 OID 19881)
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- TOC entry 5220 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- TOC entry 252 (class 1255 OID 16691)
-- Name: update_updated_at_column(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$;


ALTER FUNCTION public.update_updated_at_column() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 223 (class 1259 OID 16411)
-- Name: admin; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admin (
    id_admin integer NOT NULL,
    id_user integer NOT NULL,
    jabatan character varying(50) NOT NULL,
    level_akses character varying(20) NOT NULL,
    tanggal_dibuat timestamp without time zone NOT NULL
);


ALTER TABLE public.admin OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16410)
-- Name: admin_id_admin_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.admin_id_admin_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.admin_id_admin_seq OWNER TO postgres;

--
-- TOC entry 5221 (class 0 OID 0)
-- Dependencies: 222
-- Name: admin_id_admin_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.admin_id_admin_seq OWNED BY public.admin.id_admin;


--
-- TOC entry 227 (class 1259 OID 16437)
-- Name: bahan_baku; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bahan_baku (
    id_bahan integer NOT NULL,
    id_supplier integer NOT NULL,
    nama character varying(100) NOT NULL,
    satuan character varying(20) NOT NULL,
    stok double precision NOT NULL,
    harga_beli double precision NOT NULL,
    min_stok double precision NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.bahan_baku OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16436)
-- Name: bahan_baku_id_bahan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.bahan_baku_id_bahan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.bahan_baku_id_bahan_seq OWNER TO postgres;

--
-- TOC entry 5222 (class 0 OID 0)
-- Dependencies: 226
-- Name: bahan_baku_id_bahan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.bahan_baku_id_bahan_seq OWNED BY public.bahan_baku.id_bahan;


--
-- TOC entry 229 (class 1259 OID 16455)
-- Name: inventaris; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventaris (
    id_inventaris integer NOT NULL,
    tanggal_cek date NOT NULL,
    keterangan text NOT NULL
);


ALTER TABLE public.inventaris OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16454)
-- Name: inventaris_id_inventaris_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.inventaris_id_inventaris_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.inventaris_id_inventaris_seq OWNER TO postgres;

--
-- TOC entry 5223 (class 0 OID 0)
-- Dependencies: 228
-- Name: inventaris_id_inventaris_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.inventaris_id_inventaris_seq OWNED BY public.inventaris.id_inventaris;


--
-- TOC entry 231 (class 1259 OID 16467)
-- Name: laporan_keuangan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.laporan_keuangan (
    id_laporan integer NOT NULL,
    periode_awal date NOT NULL,
    periode_akhir date NOT NULL,
    total_pemasukan double precision NOT NULL,
    total_pengeluaran double precision NOT NULL,
    saldo double precision NOT NULL
);


ALTER TABLE public.laporan_keuangan OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16466)
-- Name: laporan_keuangan_id_laporan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.laporan_keuangan_id_laporan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.laporan_keuangan_id_laporan_seq OWNER TO postgres;

--
-- TOC entry 5224 (class 0 OID 0)
-- Dependencies: 230
-- Name: laporan_keuangan_id_laporan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.laporan_keuangan_id_laporan_seq OWNED BY public.laporan_keuangan.id_laporan;


--
-- TOC entry 233 (class 1259 OID 16480)
-- Name: owner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.owner (
    id_owner integer NOT NULL,
    id_user integer NOT NULL,
    jabatan character varying(50) NOT NULL,
    level_akses character varying(20) NOT NULL
);


ALTER TABLE public.owner OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16479)
-- Name: owner_id_owner_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.owner_id_owner_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.owner_id_owner_seq OWNER TO postgres;

--
-- TOC entry 5225 (class 0 OID 0)
-- Dependencies: 232
-- Name: owner_id_owner_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.owner_id_owner_seq OWNED BY public.owner.id_owner;


--
-- TOC entry 235 (class 1259 OID 16491)
-- Name: pelanggan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pelanggan (
    id_pelanggan integer NOT NULL,
    nama character varying(100) NOT NULL,
    alamat text NOT NULL,
    no_telp character varying(20) NOT NULL,
    email character varying(100) NOT NULL
);


ALTER TABLE public.pelanggan OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16490)
-- Name: pelanggan_id_pelanggan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pelanggan_id_pelanggan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pelanggan_id_pelanggan_seq OWNER TO postgres;

--
-- TOC entry 5226 (class 0 OID 0)
-- Dependencies: 234
-- Name: pelanggan_id_pelanggan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pelanggan_id_pelanggan_seq OWNED BY public.pelanggan.id_pelanggan;


--
-- TOC entry 239 (class 1259 OID 16519)
-- Name: pemasukan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pemasukan (
    id_pemasukan integer NOT NULL,
    id_penjualan integer NOT NULL,
    tanggal date NOT NULL,
    sumber character varying(100) NOT NULL,
    jumlah double precision NOT NULL,
    keterangan text NOT NULL
);


ALTER TABLE public.pemasukan OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16518)
-- Name: pemasukan_id_pemasukan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pemasukan_id_pemasukan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pemasukan_id_pemasukan_seq OWNER TO postgres;

--
-- TOC entry 5227 (class 0 OID 0)
-- Dependencies: 238
-- Name: pemasukan_id_pemasukan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pemasukan_id_pemasukan_seq OWNED BY public.pemasukan.id_pemasukan;


--
-- TOC entry 241 (class 1259 OID 16534)
-- Name: pengeluaran; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pengeluaran (
    id_pengeluaran integer NOT NULL,
    tanggal date NOT NULL,
    kategori character varying(100) NOT NULL,
    deskripsi text NOT NULL,
    jumlah double precision NOT NULL
);


ALTER TABLE public.pengeluaran OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 16533)
-- Name: pengeluaran_id_pengeluaran_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.pengeluaran_id_pengeluaran_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.pengeluaran_id_pengeluaran_seq OWNER TO postgres;

--
-- TOC entry 5228 (class 0 OID 0)
-- Dependencies: 240
-- Name: pengeluaran_id_pengeluaran_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.pengeluaran_id_pengeluaran_seq OWNED BY public.pengeluaran.id_pengeluaran;


--
-- TOC entry 237 (class 1259 OID 16505)
-- Name: penjualan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.penjualan (
    id_penjualan integer NOT NULL,
    id_pelanggan integer NOT NULL,
    tanggal date NOT NULL,
    total double precision NOT NULL,
    keterangan text NOT NULL
);


ALTER TABLE public.penjualan OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16504)
-- Name: penjualan_id_penjualan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.penjualan_id_penjualan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.penjualan_id_penjualan_seq OWNER TO postgres;

--
-- TOC entry 5229 (class 0 OID 0)
-- Dependencies: 236
-- Name: penjualan_id_penjualan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.penjualan_id_penjualan_seq OWNED BY public.penjualan.id_penjualan;


--
-- TOC entry 243 (class 1259 OID 16548)
-- Name: produk; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produk (
    id_produk integer NOT NULL,
    nama character varying(100) NOT NULL,
    satuan character varying(20) NOT NULL,
    harga_jual double precision NOT NULL,
    jenis character varying(50) NOT NULL,
    stok double precision NOT NULL
);


ALTER TABLE public.produk OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 16547)
-- Name: produk_id_produk_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.produk_id_produk_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.produk_id_produk_seq OWNER TO postgres;

--
-- TOC entry 5230 (class 0 OID 0)
-- Dependencies: 242
-- Name: produk_id_produk_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.produk_id_produk_seq OWNED BY public.produk.id_produk;


--
-- TOC entry 245 (class 1259 OID 16561)
-- Name: produksi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produksi (
    id_produksi integer NOT NULL,
    id_produk integer NOT NULL,
    batch character varying(20) NOT NULL,
    tanggal date NOT NULL,
    hasil_tahu integer DEFAULT 0 NOT NULL,
    id_user integer NOT NULL,
    status character varying(20) DEFAULT 'Selesai'::character varying NOT NULL,
    keterangan text NOT NULL
);


ALTER TABLE public.produksi OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 16560)
-- Name: produksi_id_produksi_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.produksi_id_produksi_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.produksi_id_produksi_seq OWNER TO postgres;

--
-- TOC entry 5231 (class 0 OID 0)
-- Dependencies: 244
-- Name: produksi_id_produksi_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.produksi_id_produksi_seq OWNED BY public.produksi.id_produksi;


--
-- TOC entry 247 (class 1259 OID 16580)
-- Name: record_penjualan; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.record_penjualan (
    id_record_penjualan integer NOT NULL,
    id_penjualan integer NOT NULL,
    id_produk integer NOT NULL,
    jumlah double precision NOT NULL,
    harga double precision NOT NULL,
    subtotal double precision NOT NULL
);


ALTER TABLE public.record_penjualan OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 16579)
-- Name: record_penjualan_id_record_penjualan_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.record_penjualan_id_record_penjualan_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.record_penjualan_id_record_penjualan_seq OWNER TO postgres;

--
-- TOC entry 5232 (class 0 OID 0)
-- Dependencies: 246
-- Name: record_penjualan_id_record_penjualan_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.record_penjualan_id_record_penjualan_seq OWNED BY public.record_penjualan.id_record_penjualan;


--
-- TOC entry 249 (class 1259 OID 16593)
-- Name: record_produksi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.record_produksi (
    id_record_produksi integer NOT NULL,
    id_produksi integer NOT NULL,
    id_bahan integer NOT NULL,
    jumlah double precision NOT NULL,
    satuan character varying(20) NOT NULL
);


ALTER TABLE public.record_produksi OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 16592)
-- Name: record_produksi_id_record_produksi_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.record_produksi_id_record_produksi_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.record_produksi_id_record_produksi_seq OWNER TO postgres;

--
-- TOC entry 5233 (class 0 OID 0)
-- Dependencies: 248
-- Name: record_produksi_id_record_produksi_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.record_produksi_id_record_produksi_seq OWNED BY public.record_produksi.id_record_produksi;


--
-- TOC entry 251 (class 1259 OID 16605)
-- Name: staff; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.staff (
    id_staff integer NOT NULL,
    id_user integer NOT NULL,
    jabatan character varying(50) NOT NULL,
    tanggal_masuk date NOT NULL,
    tanggal_keluar date
);


ALTER TABLE public.staff OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 16604)
-- Name: staff_id_staff_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.staff_id_staff_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.staff_id_staff_seq OWNER TO postgres;

--
-- TOC entry 5234 (class 0 OID 0)
-- Dependencies: 250
-- Name: staff_id_staff_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.staff_id_staff_seq OWNED BY public.staff.id_staff;


--
-- TOC entry 225 (class 1259 OID 16423)
-- Name: supplier; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.supplier (
    id_supplier integer NOT NULL,
    nama character varying(100) NOT NULL,
    alamat text NOT NULL,
    no_telp character varying(20) NOT NULL,
    email character varying(100) NOT NULL
);


ALTER TABLE public.supplier OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16422)
-- Name: supplier_id_supplier_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.supplier_id_supplier_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.supplier_id_supplier_seq OWNER TO postgres;

--
-- TOC entry 5235 (class 0 OID 0)
-- Dependencies: 224
-- Name: supplier_id_supplier_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.supplier_id_supplier_seq OWNED BY public.supplier.id_supplier;


--
-- TOC entry 221 (class 1259 OID 16391)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id_user integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    nama character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    no_telp character varying(20) NOT NULL,
    status character varying(20) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16390)
-- Name: users_id_user_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_user_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_user_seq OWNER TO postgres;

--
-- TOC entry 5236 (class 0 OID 0)
-- Dependencies: 220
-- Name: users_id_user_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_user_seq OWNED BY public.users.id_user;


--
-- TOC entry 4981 (class 2604 OID 16414)
-- Name: admin id_admin; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin ALTER COLUMN id_admin SET DEFAULT nextval('public.admin_id_admin_seq'::regclass);


--
-- TOC entry 4983 (class 2604 OID 16440)
-- Name: bahan_baku id_bahan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bahan_baku ALTER COLUMN id_bahan SET DEFAULT nextval('public.bahan_baku_id_bahan_seq'::regclass);


--
-- TOC entry 4986 (class 2604 OID 16458)
-- Name: inventaris id_inventaris; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventaris ALTER COLUMN id_inventaris SET DEFAULT nextval('public.inventaris_id_inventaris_seq'::regclass);


--
-- TOC entry 4987 (class 2604 OID 16470)
-- Name: laporan_keuangan id_laporan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.laporan_keuangan ALTER COLUMN id_laporan SET DEFAULT nextval('public.laporan_keuangan_id_laporan_seq'::regclass);


--
-- TOC entry 4988 (class 2604 OID 16483)
-- Name: owner id_owner; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.owner ALTER COLUMN id_owner SET DEFAULT nextval('public.owner_id_owner_seq'::regclass);


--
-- TOC entry 4989 (class 2604 OID 16494)
-- Name: pelanggan id_pelanggan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pelanggan ALTER COLUMN id_pelanggan SET DEFAULT nextval('public.pelanggan_id_pelanggan_seq'::regclass);


--
-- TOC entry 4991 (class 2604 OID 16522)
-- Name: pemasukan id_pemasukan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pemasukan ALTER COLUMN id_pemasukan SET DEFAULT nextval('public.pemasukan_id_pemasukan_seq'::regclass);


--
-- TOC entry 4992 (class 2604 OID 16537)
-- Name: pengeluaran id_pengeluaran; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pengeluaran ALTER COLUMN id_pengeluaran SET DEFAULT nextval('public.pengeluaran_id_pengeluaran_seq'::regclass);


--
-- TOC entry 4990 (class 2604 OID 16508)
-- Name: penjualan id_penjualan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.penjualan ALTER COLUMN id_penjualan SET DEFAULT nextval('public.penjualan_id_penjualan_seq'::regclass);


--
-- TOC entry 4993 (class 2604 OID 16551)
-- Name: produk id_produk; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produk ALTER COLUMN id_produk SET DEFAULT nextval('public.produk_id_produk_seq'::regclass);


--
-- TOC entry 4994 (class 2604 OID 16564)
-- Name: produksi id_produksi; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produksi ALTER COLUMN id_produksi SET DEFAULT nextval('public.produksi_id_produksi_seq'::regclass);


--
-- TOC entry 4997 (class 2604 OID 16583)
-- Name: record_penjualan id_record_penjualan; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_penjualan ALTER COLUMN id_record_penjualan SET DEFAULT nextval('public.record_penjualan_id_record_penjualan_seq'::regclass);


--
-- TOC entry 4998 (class 2604 OID 16596)
-- Name: record_produksi id_record_produksi; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_produksi ALTER COLUMN id_record_produksi SET DEFAULT nextval('public.record_produksi_id_record_produksi_seq'::regclass);


--
-- TOC entry 4999 (class 2604 OID 16608)
-- Name: staff id_staff; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staff ALTER COLUMN id_staff SET DEFAULT nextval('public.staff_id_staff_seq'::regclass);


--
-- TOC entry 4982 (class 2604 OID 16426)
-- Name: supplier id_supplier; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier ALTER COLUMN id_supplier SET DEFAULT nextval('public.supplier_id_supplier_seq'::regclass);


--
-- TOC entry 4980 (class 2604 OID 16394)
-- Name: users id_user; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id_user SET DEFAULT nextval('public.users_id_user_seq'::regclass);


--
-- TOC entry 5007 (class 2606 OID 16421)
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (id_admin);


--
-- TOC entry 5015 (class 2606 OID 16453)
-- Name: bahan_baku bahan_baku_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bahan_baku
    ADD CONSTRAINT bahan_baku_pkey PRIMARY KEY (id_bahan);


--
-- TOC entry 5018 (class 2606 OID 16465)
-- Name: inventaris inventaris_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventaris
    ADD CONSTRAINT inventaris_pkey PRIMARY KEY (id_inventaris);


--
-- TOC entry 5020 (class 2606 OID 16478)
-- Name: laporan_keuangan laporan_keuangan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.laporan_keuangan
    ADD CONSTRAINT laporan_keuangan_pkey PRIMARY KEY (id_laporan);


--
-- TOC entry 5023 (class 2606 OID 16489)
-- Name: owner owner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.owner
    ADD CONSTRAINT owner_pkey PRIMARY KEY (id_owner);


--
-- TOC entry 5025 (class 2606 OID 16503)
-- Name: pelanggan pelanggan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pelanggan
    ADD CONSTRAINT pelanggan_pkey PRIMARY KEY (id_pelanggan);


--
-- TOC entry 5032 (class 2606 OID 16532)
-- Name: pemasukan pemasukan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pemasukan
    ADD CONSTRAINT pemasukan_pkey PRIMARY KEY (id_pemasukan);


--
-- TOC entry 5034 (class 2606 OID 16546)
-- Name: pengeluaran pengeluaran_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pengeluaran
    ADD CONSTRAINT pengeluaran_pkey PRIMARY KEY (id_pengeluaran);


--
-- TOC entry 5029 (class 2606 OID 16517)
-- Name: penjualan penjualan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.penjualan
    ADD CONSTRAINT penjualan_pkey PRIMARY KEY (id_penjualan);


--
-- TOC entry 5036 (class 2606 OID 16559)
-- Name: produk produk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produk
    ADD CONSTRAINT produk_pkey PRIMARY KEY (id_produk);


--
-- TOC entry 5043 (class 2606 OID 16578)
-- Name: produksi produksi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produksi
    ADD CONSTRAINT produksi_pkey PRIMARY KEY (id_produksi);


--
-- TOC entry 5047 (class 2606 OID 16591)
-- Name: record_penjualan record_penjualan_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_penjualan
    ADD CONSTRAINT record_penjualan_pkey PRIMARY KEY (id_record_penjualan);


--
-- TOC entry 5051 (class 2606 OID 16603)
-- Name: record_produksi record_produksi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_produksi
    ADD CONSTRAINT record_produksi_pkey PRIMARY KEY (id_record_produksi);


--
-- TOC entry 5054 (class 2606 OID 16614)
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (id_staff);


--
-- TOC entry 5013 (class 2606 OID 16435)
-- Name: supplier supplier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.supplier
    ADD CONSTRAINT supplier_pkey PRIMARY KEY (id_supplier);


--
-- TOC entry 5001 (class 2606 OID 16409)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 5003 (class 2606 OID 16405)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- TOC entry 5005 (class 2606 OID 16407)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 5008 (class 1259 OID 16675)
-- Name: idx_admin_id_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_admin_id_user ON public.admin USING btree (id_user);


--
-- TOC entry 5016 (class 1259 OID 16676)
-- Name: idx_bahan_baku_id_supplier; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_bahan_baku_id_supplier ON public.bahan_baku USING btree (id_supplier);


--
-- TOC entry 5021 (class 1259 OID 16677)
-- Name: idx_owner_id_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_owner_id_user ON public.owner USING btree (id_user);


--
-- TOC entry 5030 (class 1259 OID 16678)
-- Name: idx_pemasukan_id_penjualan; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_pemasukan_id_penjualan ON public.pemasukan USING btree (id_penjualan);


--
-- TOC entry 5026 (class 1259 OID 16679)
-- Name: idx_penjualan_id_pelanggan; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_penjualan_id_pelanggan ON public.penjualan USING btree (id_pelanggan);


--
-- TOC entry 5027 (class 1259 OID 16680)
-- Name: idx_penjualan_tanggal; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_penjualan_tanggal ON public.penjualan USING btree (tanggal);


--
-- TOC entry 5037 (class 1259 OID 16681)
-- Name: idx_produksi_id_produk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_produksi_id_produk ON public.produksi USING btree (id_produk);


--
-- TOC entry 5038 (class 1259 OID 16682)
-- Name: idx_produksi_id_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_produksi_id_user ON public.produksi USING btree (id_user);


--
-- TOC entry 5039 (class 1259 OID 16685)
-- Name: idx_produksi_prod_batch; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_produksi_prod_batch ON public.produksi USING btree (batch);


--
-- TOC entry 5040 (class 1259 OID 16684)
-- Name: idx_produksi_prod_tanggal_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_produksi_prod_tanggal_id ON public.produksi USING btree (tanggal, id_produksi);


--
-- TOC entry 5041 (class 1259 OID 16683)
-- Name: idx_produksi_tanggal; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_produksi_tanggal ON public.produksi USING btree (tanggal);


--
-- TOC entry 5044 (class 1259 OID 16686)
-- Name: idx_record_penjualan_id_penjualan; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_record_penjualan_id_penjualan ON public.record_penjualan USING btree (id_penjualan);


--
-- TOC entry 5045 (class 1259 OID 16687)
-- Name: idx_record_penjualan_id_produk; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_record_penjualan_id_produk ON public.record_penjualan USING btree (id_produk);


--
-- TOC entry 5048 (class 1259 OID 16689)
-- Name: idx_record_produksi_id_bahan; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_record_produksi_id_bahan ON public.record_produksi USING btree (id_bahan);


--
-- TOC entry 5049 (class 1259 OID 16688)
-- Name: idx_record_produksi_id_produksi; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_record_produksi_id_produksi ON public.record_produksi USING btree (id_produksi);


--
-- TOC entry 5052 (class 1259 OID 16690)
-- Name: idx_staff_id_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_staff_id_user ON public.staff USING btree (id_user);


--
-- TOC entry 5009 (class 1259 OID 19880)
-- Name: idx_supplier_nama; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_supplier_nama ON public.supplier USING btree (nama);


--
-- TOC entry 5010 (class 1259 OID 19963)
-- Name: idx_supplier_nama_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_supplier_nama_id ON public.supplier USING btree (nama, id_supplier);


--
-- TOC entry 5011 (class 1259 OID 19962)
-- Name: idx_supplier_nama_trgm; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_supplier_nama_trgm ON public.supplier USING gin (nama public.gin_trgm_ops);


--
-- TOC entry 5067 (class 2620 OID 16692)
-- Name: bahan_baku update_bahan_baku_updated_at; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER update_bahan_baku_updated_at BEFORE UPDATE ON public.bahan_baku FOR EACH ROW EXECUTE FUNCTION public.update_updated_at_column();


--
-- TOC entry 5055 (class 2606 OID 16615)
-- Name: admin admin_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admin
    ADD CONSTRAINT admin_ibfk_1 FOREIGN KEY (id_user) REFERENCES public.users(id_user);


--
-- TOC entry 5056 (class 2606 OID 16620)
-- Name: bahan_baku bahan_baku_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bahan_baku
    ADD CONSTRAINT bahan_baku_ibfk_1 FOREIGN KEY (id_supplier) REFERENCES public.supplier(id_supplier);


--
-- TOC entry 5057 (class 2606 OID 16625)
-- Name: owner owner_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.owner
    ADD CONSTRAINT owner_ibfk_1 FOREIGN KEY (id_user) REFERENCES public.users(id_user);


--
-- TOC entry 5059 (class 2606 OID 16630)
-- Name: pemasukan pemasukan_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pemasukan
    ADD CONSTRAINT pemasukan_ibfk_1 FOREIGN KEY (id_penjualan) REFERENCES public.penjualan(id_penjualan);


--
-- TOC entry 5058 (class 2606 OID 16635)
-- Name: penjualan penjualan_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.penjualan
    ADD CONSTRAINT penjualan_ibfk_1 FOREIGN KEY (id_pelanggan) REFERENCES public.pelanggan(id_pelanggan);


--
-- TOC entry 5060 (class 2606 OID 16640)
-- Name: produksi produksi_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produksi
    ADD CONSTRAINT produksi_ibfk_1 FOREIGN KEY (id_produk) REFERENCES public.produk(id_produk);


--
-- TOC entry 5061 (class 2606 OID 16645)
-- Name: produksi produksi_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produksi
    ADD CONSTRAINT produksi_ibfk_2 FOREIGN KEY (id_user) REFERENCES public.users(id_user);


--
-- TOC entry 5062 (class 2606 OID 16650)
-- Name: record_penjualan record_penjualan_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_penjualan
    ADD CONSTRAINT record_penjualan_ibfk_1 FOREIGN KEY (id_produk) REFERENCES public.produk(id_produk);


--
-- TOC entry 5063 (class 2606 OID 16655)
-- Name: record_penjualan record_penjualan_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_penjualan
    ADD CONSTRAINT record_penjualan_ibfk_2 FOREIGN KEY (id_penjualan) REFERENCES public.penjualan(id_penjualan);


--
-- TOC entry 5064 (class 2606 OID 16660)
-- Name: record_produksi record_produksi_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_produksi
    ADD CONSTRAINT record_produksi_ibfk_1 FOREIGN KEY (id_bahan) REFERENCES public.bahan_baku(id_bahan);


--
-- TOC entry 5065 (class 2606 OID 16665)
-- Name: record_produksi record_produksi_ibfk_2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.record_produksi
    ADD CONSTRAINT record_produksi_ibfk_2 FOREIGN KEY (id_produksi) REFERENCES public.produksi(id_produksi);


--
-- TOC entry 5066 (class 2606 OID 16670)
-- Name: staff staff_ibfk_1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_ibfk_1 FOREIGN KEY (id_user) REFERENCES public.users(id_user);


-- Completed on 2026-07-24 10:35:25

--
-- PostgreSQL database dump complete
--

\unrestrict SaNQMuJTMyic2SEFcTfkXtEyKXSYYSBRHCSNeNLeRdxikxxEgQ4eGXOpu8aZSLe

