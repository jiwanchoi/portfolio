drop materialized view mv_rp_time_stat;

CREATE MATERIALIZED VIEW mv_rp_time_stat AS
select substring(gcreq, 1, 12)                                             yyyymmddhhmm,
       gcsvc,
       avg(gcdelay::float8)                                                gcresavg,
       max(gcdelay)                                                        gcresmax,
       min(gcdelay)                                                        gcresmin,
       count(gcid)                                                         gctcnt,
       COUNT(case when gcercode is null or gcercode = '' then gcid end) as gctscnt,
       COUNT(case when gcercode != '' then gcid end)                    as gctfcnt,
       avg(gmdelay::float8)                                                gmresavg,
       max(gmdelay)                                                        gmresmax,
       min(gmdelay)                                                        gmresmin,
       count(gcid)                                                         gmtcnt,
       COUNT(case when gmstat = '200 OK' then gcid end)                 as gmtscnt,
       COUNT(case when gmstat != '200 OK' then gcid end)                as gmtfcnt,
       avg(gctrycnt::float8)                                               gcavgtry,
       sum(gctrycnt::float8)                                               gccnttry,
       max(max_elapsed_time)::bigint                                       max_elapsed_time,
       round(avg(avg_elapsed_time), 0)::bigint                             avg_elapsed_time,
       min(min_elapsed_time)::bigint                                       min_elapsed_time
from gcustom.rplog
where to_date(substring(gcreq, 1, 8), 'YYYYMMDD') > (current_date - 5)
group by substring(gcreq, 1, 12), gcsvc
order by substring(gcreq, 1, 12);