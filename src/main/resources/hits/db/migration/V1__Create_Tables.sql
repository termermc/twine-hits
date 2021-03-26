CREATE TABLE public.counters
(
    id bigserial NOT NULL,
    counter_id character varying(16) NOT NULL,
    counter_name character varying(32) NOT NULL,
    counter_password character varying(10) NOT NULL,
    counter_text_color character varying(11) NOT NULL,
    counter_bg_color character varying(11) NOT NULL,
    counter_created_on timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE TABLE public.hits
(
    id bigserial NOT NULL,
    hit_ip text NOT NULL,
    hit_counter bigint NOT NULL,
    hit_time timestamp with time zone NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);