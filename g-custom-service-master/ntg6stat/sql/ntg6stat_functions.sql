CREATE OR replace FUNCTION fnc_t_rp_month_stat(a INTEGER)
    RETURNS INTEGER AS
$$/**/
begin
    IF ((select count(yyyymm) from t_rp_month_stat where yyyymm = to_char(current_date, 'YYYYMM')) < 1
        )
    then
        a = 100;
        insert into t_rp_month_stat
        select substring(yyyymmdd, 1, 6) yyyymm,
               gcsvc,
               avg(gcresavg)             gcresavg,
               max(gcresmax)             gcresmax,
               min(gcresmin)             gcresmin,
               sum(gctcnt)               gctcnt,
               sum(gctscnt)              gctscnt,
               sum(gctfcnt)              gctfcnt,
               avg(gmresavg)             gmresavg,
               max(gmresmax)             gmresmax,
               min(gmresmin)             gmresmin,
               sum(gmtcnt)               gmtcnt,
               sum(gmtscnt)              gmtscnt,
               sum(gmtfcnt)              gmtfcnt,
               avg(gcavgtry)             gcavgtry,
               sum(gccnttry)             gccnttry,
               max(max_elapsed_time)     max_elapsed_time,
               avg(avg_elapsed_time)     avg_elapsed_time,
               min(min_elapsed_time)     min_elapsed_time
        from t_rp_day_stat
        where substring(yyyymmdd, 1, 6) = to_char(current_date, 'YYYYMM')
        group by substring(yyyymmdd, 1, 6), gcsvc;

        update t_rp_month_stat as trps
        set yyyymm           = tdsu.yyyymm,
            gcsvc            = tdsu.gcsvc,
            gcresavg         = tdsu.gcresavg,
            gcresmax         = tdsu.gcresmax,
            gcresmin         = tdsu.gcresmin,
            gctcnt           = tdsu.gctcnt,
            gctscnt          = tdsu.gctscnt,
            gctfcnt          = tdsu.gctfcnt,
            gmresavg         = tdsu.gmresavg,
            gmresmax         = tdsu.gmresmax,
            gmresmin         = tdsu.gmresmin,
            gmtcnt           = tdsu.gmtcnt,
            gmtscnt          = tdsu.gmtscnt,
            gmtfcnt          = tdsu.gmtfcnt,
            gcavgtry         = tdsu.gcavgtry,
            gccnttry         = tdsu.gccnttry,
            max_elapsed_time = tdsu.max_elapsed_time,
            avg_elapsed_time = tdsu.avg_elapsed_time,
            min_elapsed_time = tdsu.min_elapsed_time
        from (
                 select substring(tds.yyyymmdd, 1, 6) yyyymm,
                        tds.gcsvc,
                        avg(tds.gcresavg)             gcresavg,
                        max(tds.gcresmax)             gcresmax,
                        min(tds.gcresmin)             gcresmin,
                        sum(tds.gctcnt)               gctcnt,
                        sum(tds.gctscnt)              gctscnt,
                        sum(tds.gctfcnt)              gctfcnt,
                        avg(tds.gmresavg)             gmresavg,
                        max(tds.gmresmax)             gmresmax,
                        min(tds.gmresmin)             gmresmin,
                        sum(tds.gmtcnt)               gmtcnt,
                        sum(tds.gmtscnt)              gmtscnt,
                        sum(tds.gmtfcnt)              gmtfcnt,
                        avg(tds.gcavgtry)             gcavgtry,
                        sum(tds.gccnttry)             gccnttry,
                        max(tds.max_elapsed_time)     max_elapsed_time,
                        avg(avg_elapsed_time)         avg_elapsed_time,
                        min(min_elapsed_time)         min_elapsed_time
                 from t_rp_day_stat tds
                 where substring(tds.yyyymmdd, 1, 6) = to_char((current_date - 1), 'YYYYMM')
                 group by substring(tds.yyyymmdd, 1, 6), tds.gcsvc
             ) tdsu
        where trps.yyyymm = to_char((current_date - 1), 'YYYYMM');
    else
        a = 200;
        update t_rp_month_stat as trps
        set yyyymm           = tdsu.yyyymm,
            gcsvc            = tdsu.gcsvc,
            gcresavg         = tdsu.gcresavg,
            gcresmax         = tdsu.gcresmax,
            gcresmin         = tdsu.gcresmin,
            gctcnt           = tdsu.gctcnt,
            gctscnt          = tdsu.gctscnt,
            gctfcnt          = tdsu.gctfcnt,
            gmresavg         = tdsu.gmresavg,
            gmresmax         = tdsu.gmresmax,
            gmresmin         = tdsu.gmresmin,
            gmtcnt           = tdsu.gmtcnt,
            gmtscnt          = tdsu.gmtscnt,
            gmtfcnt          = tdsu.gmtfcnt,
            gcavgtry         = tdsu.gcavgtry,
            gccnttry         = tdsu.gccnttry,
            max_elapsed_time = tdsu.max_elapsed_time,
            avg_elapsed_time = tdsu.avg_elapsed_time,
            min_elapsed_time = tdsu.min_elapsed_time
        from (
                 select substring(tds.yyyymmdd, 1, 6) yyyymm,
                        tds.gcsvc,
                        avg(tds.gcresavg)             gcresavg,
                        max(tds.gcresmax)             gcresmax,
                        min(tds.gcresmin)             gcresmin,
                        sum(tds.gctcnt)               gctcnt,
                        sum(tds.gctscnt)              gctscnt,
                        sum(tds.gctfcnt)              gctfcnt,
                        avg(tds.gmresavg)             gmresavg,
                        max(tds.gmresmax)             gmresmax,
                        min(tds.gmresmin)             gmresmin,
                        sum(tds.gmtcnt)               gmtcnt,
                        sum(tds.gmtscnt)              gmtscnt,
                        sum(tds.gmtfcnt)              gmtfcnt,
                        avg(tds.gcavgtry)             gcavgtry,
                        sum(tds.gccnttry)             gccnttry,
                        max(tds.max_elapsed_time)     max_elapsed_time,
                        avg(tds.avg_elapsed_time)     avg_elapsed_time,
                        min(tds.min_elapsed_time)     min_elapsed_time
                 from t_rp_day_stat tds
                 where substring(tds.yyyymmdd, 1, 6) = to_char(current_date, 'YYYYMM')
                 group by substring(tds.yyyymmdd, 1, 6), tds.gcsvc
             ) tdsu
        where trps.yyyymm = to_char(current_date, 'YYYYMM');
    end if;
    RETURN a;
END;
$$
    LANGUAGE PLPGSQL;



CREATE OR replace FUNCTION fnc_t_rp_day_stat(a INTEGER)
    RETURNS INTEGER AS
$$
begin
    IF ((select count(yyyymmdd) from t_rp_day_stat where to_date(yyyymmdd, 'YYYYMMDD') = (current_date - 1)) < 1
        )
    then
        a = 100;
        insert into t_rp_day_stat
        select substring(yyyymmddhhmm, 1, 8) yyyymmdd,
               gcsvc,
               avg(gcresavg)                 gcresavg,
               max(gcresmax)                 gcresmax,
               min(gcresmin)                 gcresmin,
               sum(gctcnt)                   gctcnt,
               sum(gctscnt)                  gctscnt,
               sum(gctfcnt)                  gctfcnt,
               avg(gmresavg)                 gmresavg,
               max(gmresmax)                 gmresmax,
               min(gmresmin)                 gmresmin,
               sum(gmtcnt)                   gmtcnt,
               sum(gmtscnt)                  gmtscnt,
               sum(gmtfcnt)                  gmtfcnt,
               avg(gcavgtry)                 gcavgtry,
               sum(gccnttry)                 gccnttry,
               max(max_elapsed_time)         max_elapsed_time,
               avg(avg_elapsed_time)         avg_elapsed_time,
               min(min_elapsed_time)         min_elapsed_time
        from mv_rp_time_stat
        where to_date(substring(yyyymmddhhmm, 1, 8), 'YYYYMMDD') = (current_date - 1)
        group by substring(yyyymmddhhmm, 1, 8), gcsvc;
    end if;
    RETURN a;
END;
$$
    LANGUAGE PLPGSQL;


-- select fnc_t_rp_day_stat(1);
-- select fnc_t_rp_month_stat(1);


CREATE OR replace FUNCTION fnc_go_gcstm_schedule(a INTEGER)
    RETURNS INTEGER AS
$$
begin
    a = 100;
    REFRESH MATERIALIZED VIEW mv_rp_time_stat;
    select fnc_t_rp_day_stat(1) into a;
    select fnc_t_rp_month_stat(1) into a;
    RETURN a;
END;
$$
    LANGUAGE PLPGSQL;


-- select fnc_go_gcstm_schedule(1);

--------------------------------------------------------------------------
-- 통계 rebuild 함수 생성.
-- example
-- select func_rebuild_stat('2020-02-01', '2020-06-01');
drop function func_rebuild_stat(text, text);
create or replace function func_rebuild_stat(sdate text, edate TEXT)
    returns text[] AS
$$
DECLARE
    rec         RECORD;
    rec_month   RECORD;
    total       numeric := 0;
    count       numeric := 0;
    rslt        text[];
    query       TEXT;
    query_month TEXT;
BEGIN
    -- day stat rebuild
    query := 'SELECT to_char(DATE(GENERATE_SERIES(DATE ''' || sdate || ''', DATE ''' || edate ||
             ''', ''1 day'')), ''YYYYMMDD'') AS date;';

    for rec in
        execute query using sdate, edate
        loop
            insert into t_rp_day_stat
            select substring(gcreq, 1, 8)                                          yyyymmdd,
                   gcsvc,
                   avg(gcdelay::float8)                                            gcresavg,
                   max(gcdelay)                                                    gcresmax,
                   min(gcdelay)                                                    gcresmin,
                   count(gcid)                                                     gctcnt,
                   COUNT(gcid) filter (where gcercode is null or gcercode = '') as gctscnt,
                   COUNT(gcid) filter (where gcercode != '')                    as gctfcnt,
                   avg(gmdelay::float8)                                            gmresavg,
                   max(gmdelay)                                                    gmresmax,
                   min(gmdelay)                                                    gmresmin,
                   count(gcid)                                                     gmtcnt,
                   COUNT(gcid) filter (where gmstat = '200 OK')                 as gmtscnt,
                   COUNT(gcid) filter (where gmstat != '200 OK')                as gmtfcnt,
                   avg(gctrycnt::float8)                                           gcavgtry,
                   sum(gctrycnt::float8)                                           gccnttry,
                   max(max_elapsed_time)::bigint                                   max_elapsed_time,
                   avg(avg_elapsed_time)::bigint                                   avg_elapsed_time,
                   min(min_elapsed_time)::bigint                                   min_elapsed_time
            from gcustom.rplog
            where substring(gcreq, 1, 8) = rec.date
            group by substring(gcreq, 1, 8), gcsvc
            order by substring(gcreq, 1, 8)
            on conflict (yyyymmdd, gcsvc)
                DO UPDATE
                SET gcresavg         = excluded.gcresavg,
                    gcresmax         = excluded.gcresmax,
                    gcresmin         = excluded.gcresmin,
                    gctcnt           = excluded.gctcnt,
                    gctscnt          = excluded.gctscnt,
                    gctfcnt          = excluded.gctfcnt,
                    gmresavg         = excluded.gmresavg,
                    gmresmax         = excluded.gmresmax,
                    gmresmin         = excluded.gmresmin,
                    gmtcnt           = excluded.gmtcnt,
                    gmtscnt          = excluded.gmtscnt,
                    gmtfcnt          = excluded.gmtfcnt,
                    gcavgtry         = excluded.gcavgtry,
                    gccnttry         = excluded.gccnttry,
                    max_elapsed_time = excluded.max_elapsed_time,
                    avg_elapsed_time = excluded.avg_elapsed_time,
                    min_elapsed_time = excluded.min_elapsed_time;


            total := total + count;
            raise notice 'stat day %', rec.date;
        end loop;

    -- month stat rebuild
    query_month := 'SELECT to_char(DATE(GENERATE_SERIES(DATE ''' || sdate || ''', DATE ''' || edate ||
                   ''', ''1 month'')), ''YYYYMM'') AS date;';

    for rec_month in execute query_month using sdate, edate
        loop
            insert into t_rp_month_stat
            select substring(yyyymmdd, 1, 6)     yyyymm,
                   gcsvc,
                   avg(gcresavg)                 gcresavg,
                   max(gcresmax)                 gcresmax,
                   min(gcresmin)                 gcresmin,
                   sum(gctcnt)                   gctcnt,
                   sum(gctscnt)                  gctscnt,
                   sum(gctfcnt)                  gctfcnt,
                   avg(gmresavg)                 gmresavg,
                   max(gmresmax)                 gmresmax,
                   min(gmresmin)                 gmresmin,
                   sum(gmtcnt)                   gmtcnt,
                   sum(gmtscnt)                  gmtscnt,
                   sum(gmtfcnt)                  gmtfcnt,
                   avg(gcavgtry)                 gcavgtry,
                   sum(gccnttry)                 gccnttry,
                   max(max_elapsed_time)::bigint max_elapsed_time,
                   avg(avg_elapsed_time)::bigint avg_elapsed_time,
                   min(min_elapsed_time)::bigint min_elapsed_time
            from t_rp_day_stat
            where substring(yyyymmdd, 1, 6) = rec_month.date
            group by substring(yyyymmdd, 1, 6), gcsvc
            on conflict (yyyymm, gcsvc)
                DO UPDATE
                SET gcresavg         = excluded.gcresavg,
                    gcresmax         = excluded.gcresmax,
                    gcresmin         = excluded.gcresmin,
                    gctcnt           = excluded.gctcnt,
                    gctscnt          = excluded.gctscnt,
                    gctfcnt          = excluded.gctfcnt,
                    gmresavg         = excluded.gmresavg,
                    gmresmax         = excluded.gmresmax,
                    gmresmin         = excluded.gmresmin,
                    gmtcnt           = excluded.gmtcnt,
                    gmtscnt          = excluded.gmtscnt,
                    gmtfcnt          = excluded.gmtfcnt,
                    gcavgtry         = excluded.gcavgtry,
                    gccnttry         = excluded.gccnttry,
                    max_elapsed_time = excluded.max_elapsed_time,
                    avg_elapsed_time = excluded.avg_elapsed_time,
                    min_elapsed_time = excluded.min_elapsed_time;

            raise notice 'stat month %', rec_month.date;

        end loop;

    return rslt;
END;
$$ LANGUAGE plpgsql;
