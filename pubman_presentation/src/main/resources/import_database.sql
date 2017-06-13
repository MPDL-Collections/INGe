-- CREATE TABLE escidoc_import_log (
--    id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
--    status character varying NOT NULL,
--    errorlevel character varying NOT NULL,
--    startdate timestamp NOT NULL,
--    enddate timestamp,
--    action character varying,
--    userid character varying,
--    name character varying,
--    format character varying,
--    context character varying,
--    percentage integer
--);

-- CREATE TABLE escidoc_import_log_detail (
--    id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
--    status character varying NOT NULL,
--    errorlevel character varying NOT NULL,
--    startdate timestamp NOT NULL,
--    enddate timestamp,
--    parent integer NOT NULL,
--    message character varying,
--    item_id character varying,
--    action character varying
--);

-- CREATE TABLE escidoc_import_log_item (
--    id integer GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
--    status character varying NOT NULL,
--    errorlevel character varying NOT NULL,
--    startdate timestamp NOT NULL,
--    enddate timestamp,
--    parent integer NOT NULL,
--    message character varying,
--    item_id character varying,
--    action character varying
--);

-- Table: public.import_log

-- DROP TABLE public.import_log;

CREATE TABLE public.import_log
(
    id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    action character varying COLLATE pg_catalog."default",
    userid character varying COLLATE pg_catalog."default",
    name character varying COLLATE pg_catalog."default",
    format character varying COLLATE pg_catalog."default",
    context character varying COLLATE pg_catalog."default",
    percentage integer,
    CONSTRAINT import_log_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log
    OWNER to postgres;

-- Table: public.import_log_detail

-- DROP TABLE public.import_log_detail;

CREATE TABLE public.import_log_detail
(
    id integer NOT NULL DEFAULT nextval('import_log_detail_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    parent integer NOT NULL,
    message character varying COLLATE pg_catalog."default",
    item_id character varying COLLATE pg_catalog."default",
    action character varying COLLATE pg_catalog."default",
    CONSTRAINT import_log_detail_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log_detail
    OWNER to postgres;

-- Table: public.import_log_item

-- DROP TABLE public.import_log_item;

CREATE TABLE public.import_log_item
(
    id integer NOT NULL DEFAULT nextval('import_log_item_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    parent integer NOT NULL,
    message character varying COLLATE pg_catalog."default",
    item_id character varying COLLATE pg_catalog."default",
    action character varying COLLATE pg_catalog."default",
    CONSTRAINT import_log_item_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log_item
    OWNER to postgres;
