alter table gcustom.rplog
	add max_elapsed_time bigint default 0;
comment on column rplog.max_elapsed_time is 'GeoMaster response max elapsed time';

alter table gcustom.rplog
	add avg_elapsed_time bigint default 0;
comment on column rplog.avg_elapsed_time is 'GeoMaster response avg elapsed time';

alter table gcustom.rplog
	add min_elapsed_time bigint default 0;
comment on column rplog.min_elapsed_time is 'GeoMaster response min elapsed time';


alter table gcustom.t_rp_day_stat
	add max_elapsed_time bigint default 0;
comment on column gcustom.t_rp_day_stat.max_elapsed_time is '최대 GM 응답시간';

alter table gcustom.t_rp_day_stat
	add avg_elapsed_time bigint default 0;
comment on column gcustom.t_rp_day_stat.avg_elapsed_time is '평균 GM 응답시간';

alter table gcustom.t_rp_day_stat
	add min_elapsed_time bigint default 0;
comment on column gcustom.t_rp_day_stat.min_elapsed_time is '최소 GM 응답시간';


alter table gcustom.t_rp_month_stat
	add max_elapsed_time bigint default 0;

comment on column gcustom.t_rp_month_stat.max_elapsed_time is '최대 GM 응답시간';

alter table gcustom.t_rp_month_stat
	add avg_elapsed_time bigint default 0;

comment on column gcustom.t_rp_month_stat.avg_elapsed_time is '평균 gm 응답시간';

alter table gcustom.t_rp_month_stat
	add min_elapsed_time bigint default 0;

comment on column gcustom.t_rp_month_stat.min_elapsed_time is '최소 GM 응답시간';

