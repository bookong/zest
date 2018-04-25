DROP TABLE IF EXISTS `tv_item_score` ;
CREATE TABLE `tv_item_score` (
  `id` long(19) NOT NULL,
  `item_code` varchar(256) NOT NULL,
  `count` int(10) DEFAULT NULL,
  `score` float DEFAULT '0',
  `create_time` timestamp NOT NULL ,
  `update_time` timestamp NOT NULL ,
  `total_score` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tv_item_score_idx_1` (`item_code`)
) ;

DROP TABLE IF EXISTS `tv_service` ;
CREATE TABLE `tv_service` (
  `id` long(19) NOT NULL ,
  `code` varchar(256) NOT NULL ,
  `name` varchar(256) DEFAULT NULL,
  `type` varchar(256) DEFAULT NULL ,
  `price` float DEFAULT NULL ,
  `validity_status` long(1) DEFAULT NULL ,
  `publish_status` long(1) DEFAULT NULL ,
  `create_time` timestamp NOT NULL ,
  `update_time` timestamp NOT NULL ,
  `operator_name` varchar(256) DEFAULT NULL,
  `description` varchar(3072) DEFAULT NULL,
  `bitrate` varchar(128) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_service_idx_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_product` ;
CREATE TABLE `tv_product` (
  `id` long(19) NOT NULL ,
  `code` varchar(256) DEFAULT NULL ,
  `name` varchar(256) DEFAULT NULL,
  `type` varchar(256) DEFAULT NULL ,
  `picture_big` varchar(256) DEFAULT NULL,
  `picture_small` varchar(256) DEFAULT NULL,
  `enter_url` varchar(256) DEFAULT NULL,
  `validity_status` long(1) DEFAULT NULL ,
  `publish_status` long(1) DEFAULT NULL ,
  `validity_starttime` timestamp NOT NULL ,
  `validity_endtime` timestamp NOT NULL ,
  `order_type` long(1) DEFAULT NULL ,
  `order_cycle` varchar(256) DEFAULT NULL ,
  `unsubscribe` long(1) DEFAULT NULL ,
  `keepsubscribe` long(1) DEFAULT NULL ,
  `chargeplan_code` varchar(256) DEFAULT NULL,
  `create_time` timestamp NOT NULL ,
  `last_modify` timestamp NOT NULL ,
  `publish_time` timestamp NOT NULL ,
  `operator_name` varchar(256) DEFAULT NULL,
  `description` varchar(3072) DEFAULT NULL,
  `partner_code` varchar(128) DEFAULT NULL ,
  `picture_big_2` varchar(256) DEFAULT NULL ,
  `biz_type` int(2) DEFAULT '1' ,
  `oper_type` long(1) DEFAULT NULL,
  `use_type` int(2) DEFAULT '0',
  `free_time` long(19) DEFAULT '300',
  `product_type` int(1) DEFAULT NULL,
  `display_type` int(11) DEFAULT '1' ,
  `valid_time` datetime DEFAULT NULL ,
  `expire_time` datetime DEFAULT NULL ,
  `order_num` int(11) DEFAULT '0' ,
  PRIMARY KEY (`id`),
  KEY `tv_product_idx_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_rel_service_content` ;
CREATE TABLE `tv_rel_service_content` (
  `id` long(19) NOT NULL ,
  `s_code` varchar(256) DEFAULT NULL,
  `c_code` varchar(256) DEFAULT NULL,
  `validity_status` long(1) DEFAULT NULL ,
  `content_type` varchar(256) DEFAULT NULL ,
  `publish_status` long(1) DEFAULT NULL ,
  `update_time` timestamp NOT NULL ,
  `category_path` varchar(1024) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_service_content_idx_1` (`c_code`),
  KEY `tv_rel_service_content_idx_2` (`s_code`)
) ;

DROP TABLE IF EXISTS `tv_rel_product_service` ;
CREATE TABLE `tv_rel_product_service` (
  `id` long(19) NOT NULL ,
  `p_code` varchar(256) DEFAULT NULL,
  `s_code` varchar(256) DEFAULT NULL,
  `validity_status` long(1) DEFAULT NULL ,
  `publish_status` long(1) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_product_service_idx_1` (`p_code`),
  KEY `tv_rel_product_service_idx_2` (`s_code`)
) ;

DROP TABLE IF EXISTS `tv_guide_video` ;
CREATE TABLE `tv_guide_video` (
  `id` long(20) NOT NULL ,
  `title` varchar(128) DEFAULT NULL ,
  `code` varchar(128) DEFAULT NULL ,
  `description` varchar(1024) DEFAULT NULL ,
  `guide_video_mode` int(2) DEFAULT '0' ,
  `update_time` datetime DEFAULT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_guide_video_item_fdn` ;
CREATE TABLE `tv_guide_video_item_fdn` (
  `id` long(20) NOT NULL ,
  `title` varchar(128) DEFAULT NULL ,
  `guide_code` varchar(128) DEFAULT NULL ,
  `description` varchar(1024) DEFAULT NULL ,
  `guide_item_code` varchar(128) DEFAULT NULL ,
  `guide_category_code` varchar(128) DEFAULT NULL ,
  `guide_item_type` int(2) DEFAULT '1' ,
  `big_image_url` varchar(256) DEFAULT NULL ,
  `link_type` int(2) DEFAULT NULL ,
  `mode` int(2) DEFAULT NULL ,
  `sort` int(2) DEFAULT NULL ,
  `guide_video_mode` int(2) DEFAULT '0' ,
  `link_value_code` varchar(128) DEFAULT NULL ,
  `link_category_code` varchar(128) DEFAULT NULL ,
  `user_group_code` varchar(4000) DEFAULT NULL ,
  `package_name` varchar(128) DEFAULT NULL ,
  `res_uri` varchar(128) DEFAULT NULL ,
  `item_begin_time` datetime DEFAULT NULL ,
  `item_end_time` datetime DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_cloud_recommend` ;
CREATE TABLE `tv_cloud_recommend` (
  `id` long(19) NOT NULL,
  `code` varchar(128) NOT NULL ,
  `name` varchar(200) NOT NULL,
  `description` varchar(2000) DEFAULT NULL ,
  `horizontal_big` varchar(256) DEFAULT NULL ,
  `horizontal_small` varchar(256) DEFAULT NULL ,
  `upright_big` varchar(256) DEFAULT NULL ,
  `upright_small` varchar(256) DEFAULT NULL ,
  `default_category_code` varchar(128) DEFAULT NULL ,
  `default_category_path` varchar(256) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `is_used_by_ott` int(11) DEFAULT '0',
  `mode` int(2) DEFAULT '2' ,
  `res_uri` varchar(256) DEFAULT NULL ,
  `target_type` int(2) DEFAULT '1' ,
  `package_name` varchar(256) DEFAULT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_category_filter` ;
CREATE TABLE `tv_category_filter` (
  `id` long(11) NOT NULL ,
  `category_code` varchar(1024) NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0' ,
  `app_code` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tv_category_filter_1` (`app_code`)
) ;

DROP TABLE IF EXISTS `tv_adaptation_rule` ;
CREATE TABLE `tv_adaptation_rule` (
  `id` long(19)  ,
  `rule_name` varchar(256)  ,
  `rule_code` varchar(32)  ,
  `user_id` varchar(32) ,
  `provider_code` varchar(32) ,
  `user_type` varchar(2) ,
  `terminal_model` varchar(32) ,
  `terminal_type` varchar(2) ,
  `bcti_version` varchar(32) ,
  `player_version` varchar(32) ,
  `user_group_code` varchar(32) ,
  `product_group_code` varchar(32) ,
  `terminal_group_code` varchar(32) ,
  `media_encoding_protocol` long(19) ,
  `is_support_ad` int(1) ,
  `is_support_bsd` int(1) ,
  `picture` int(1) ,
  `update_time` datetime ,
  `cdn_priority` varchar(512) ,
  `remark` varchar(2000) ,
  `template_type` int(2) ,
  `audio_format` int(2) ,
  `content_group_code` varchar(128) ,
  PRIMARY KEY (`id`),
  KEY `tv_adaptation_rule_1` (`user_id`),
  KEY `tv_adaptation_rule_2` (`provider_code`),
  KEY `tv_adaptation_rule_3` (`rule_code`)
) ;

DROP TABLE IF EXISTS `tv_bitrate_model` ;
CREATE TABLE `tv_bitrate_model` (
  `id` long(11) ,
  `code` varchar(128) ,
  `name` varchar(128) ,
  `description` varchar(2000) ,
  `bitrate_code` varchar(128) ,
  `audio_format` int(2) ,
  `priority` int(1) ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_bitr_encode_prot_audio_enu` ;
CREATE TABLE `tv_bitr_encode_prot_audio_enu` (
  `id` long(19) ,
  `encode` varchar(32) ,
  `protocol` varchar(32) ,
  `digit` long(19) ,
  `bitrate` varchar(32) ,
  `audio` varchar(32) 
) ;

DROP TABLE IF EXISTS `tv_category` ;
CREATE TABLE `tv_category` (
  `id` long(20) ,
  `code` varchar(128) ,
  `parent_code` varchar(128) ,
  `name` varchar(128) ,
  `description` text,
  `icon1` varchar(128) ,
  `icon2` varchar(128) ,
  `big_image1` varchar(128) ,
  `big_image2` varchar(128) ,
  `child_number` int(11) ,
  `display_mode` int(11) ,
  `template_code` varchar(64) ,
  `sequence` int(4) NOT NULL ,
  `status` int(11) NOT NULL ,
  `site_code` varchar(128) ,
  `type` int(11) NOT NULL ,
  `reference_code` varchar(128) ,
  `kind` int(11) ,
  `url` varchar(256) ,
  `pop_rel_category` varchar(24) ,
  `pop_type` varchar(24) ,
  `ott_small_image` varchar(256) ,
  `icon3` varchar(256) ,
  `big_image3` varchar(256) ,
  `category_name_image1` varchar(256) ,
  `category_name_image2` varchar(256) ,
  `show_way` int(2) ,
  `mark_uri` varchar(256) ,
  `mark_position` int(2) ,
  `nav_recommend` varchar(256) ,
  `nav_topic_category` varchar (128) ,
  `search_tags` varchar(100) ,
  `tag_type` int(2) ,
  `SEARCH_FLAG` int(11) ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_category_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_category_item_ext_fdn` ;
CREATE TABLE `tv_category_item_ext_fdn` (
  `id` long(20) ,
  `visible_status` int(11) ,
  `icon1` varchar(1024) ,
  `icon2` varchar(1024) ,
  `content_type` int(11) ,
  `content_code` varchar(128) ,
  `category_code` varchar(128) ,
  `show_name` varchar(128) ,
  `ipd_show_name` varchar(128) ,
  `begin_time` datetime ,
  `end_time` datetime ,
  `description` text,
  `item_type` int(11) ,
  `item_index` int(11) ,
  `insert_time` datetime ,
  `onlined_protocol` long(19) ,
  `onlined_bitrate` varchar(2048) ,
  `onlined_protocol_audio` varchar(512) ,
  PRIMARY KEY (`id`),
  KEY `tv_category_item_ext_1` (`content_code`),
  KEY `tv_category_item_ext_2` (`category_code`,`item_index`)
) ;

DROP TABLE IF EXISTS `tv_channel` ;
CREATE TABLE `tv_channel` (
  `id` long(20) ,
  `code` varchar(128) ,
  `name` varchar(128) ,
  `description` text,
  `type` int(11) ,
  `timeshift_duration` int(11) ,
  `small_image1` varchar(128) ,
  `small_image2` varchar(128) ,
  `big_image1` varchar(128) ,
  `big_image2` varchar(128) ,
  `reserve1` varchar(128) ,
  `reserve2` varchar(128) ,
  `reserve3` varchar(128) ,
  `reserve4` varchar(128) ,
  `reserve5` varchar(128) ,
  `package_code` text ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_encode_protocol_enum` ;
CREATE TABLE `tv_encode_protocol_enum` (
  `id` long(19) ,
  `encode` varchar(32) ,
  `protocol` varchar(32) ,
  `digit` int(19) ,
  `priority` int(1) ,
  KEY `tv_encode_protocol_enum_1` (`digit`),
  KEY `tv_encode_protocol_enum_2` (`encode`),
  KEY `tv_encode_protocol_enum_3` (`protocol`)
) ;

DROP TABLE IF EXISTS `tv_program_ext` ;
CREATE TABLE `tv_program_ext` (
  `id` long(20) ,
  `code` varchar(128) ,
  `name` varchar(128) ,
  `ipd_name` varchar(128) ,
  `description` text,
  `type` int(11) ,
  `status` int(11) ,
  `episode_number` int(11) ,
  `actor` varchar(128) ,
  `director` varchar(128) ,
  `region` varchar(64) ,
  `language` varchar(32) ,
  `genre` varchar(256) ,
  `keywords` varchar(1024) ,
  `rating` varchar(8) ,
  `small_image1` varchar(128) ,
  `small_image2` varchar(128) ,
  `big_image1` varchar(128) ,
  `big_image2` varchar(128) ,
  `reserve1` varchar(128) ,
  `reserve2` varchar(128) ,
  `reserve3` varchar(128) ,
  `reserve4` varchar(128) ,
  `reserve5` varchar(128) ,
  `package_code` text ,
  `search_name` varchar(256) ,
  `year` varchar(96) ,
  `content_type` varchar(96) ,
  `content_type_code` varchar(64) ,
  `tags` varchar(1024) ,
  `length` int(20) ,
  `subtitleType` int(11) ,
  `subtitleURL` varchar(382) ,
  `onlined_protocol` long(19) ,
  `update_time` datetime ,
  `onlined_bitrate` varchar(2048) ,
  `price` float ,
  `p_code` varchar(256) ,
  `small_image3` varchar(256) ,
  `big_image3` varchar(256) ,
  `onlined_protocol_audio` varchar(512) ,
  `old_id` varchar(384) ,
  `r_media_code` varchar(384) ,
  `mi_small_image` varchar(256) ,
  `mi_big_image` varchar(256) ,
  `update_episode_number` int(10) ,
  `update_episode_title` varchar (32) ,
  `xbox_image1` varchar(128) ,
  `xbox_image2` varchar(128) ,
  `xbox_image3` varchar(128) ,
  `xbox_image4` varchar(128) ,
  `xbox_image5` varchar(128) ,
  `xbox_image6` varchar(128) ,
  `xbox_image7` varchar(128) ,
  `xbox_image_1138_640` varchar(128) ,
  `xbox_image_424_640` varchar(128) ,
  `xbox_image_640_640` varchar(128) ,
  `xbox_image_424_100` varchar(128) ,
  `category` varchar(40) ,
  `copyright_type` int(11) ,
  `sub_title` varchar(64) ,
  `insert_time` timestamp ,
  `source_code` varchar(32) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_program_ext_1` (`code`),
  KEY `tv_program_ext_2` (`rating`)
) ;

DROP TABLE IF EXISTS `tv_program_ext_fdn` ;
CREATE TABLE `tv_program_ext_fdn` (
  `id` long(20) ,
  `code` varchar(128) ,
  `name` varchar(128) ,
  `ipd_name` varchar(128) ,
  `description` text,
  `type` int(11) ,
  `status` int(11) ,
  `episode_number` int(11) ,
  `actor` varchar(128) ,
  `director` varchar(128) ,
  `region` varchar(64) ,
  `language` varchar(32) ,
  `genre` varchar(256) ,
  `keywords` varchar(1024) ,
  `rating` varchar(8) ,
  `small_image1` varchar(128) ,
  `small_image2` varchar(128) ,
  `big_image1` varchar(128) ,
  `big_image2` varchar(128) ,
  `reserve1` varchar(128) ,
  `reserve2` varchar(128) ,
  `reserve3` varchar(128) ,
  `reserve4` varchar(128) ,
  `reserve5` varchar(128) ,
  `package_code` text ,
  `search_name` varchar(256) ,
  `year` varchar(96) ,
  `content_type` varchar(96) ,
  `content_type_code` varchar(64) ,
  `tags` varchar(1024) ,
  `length` int(20) ,
  `subtitleType` int(11) ,
  `subtitleURL` varchar(382) ,
  `onlined_protocol` long(19) ,
  `update_time` datetime ,
  `onlined_bitrate` varchar(2048) ,
  `price` float ,
  `p_code` varchar(256) ,
  `small_image3` varchar(256) ,
  `big_image3` varchar(256) ,
  `onlined_protocol_audio` varchar(512) ,
  `old_id` varchar(384) ,
  `r_media_code` varchar(384) ,
  `mi_small_image` varchar(256) ,
  `mi_big_image` varchar(256) ,
  `update_episode_number` int(10) ,
  `update_episode_title` varchar (32) ,
  `xbox_image1` varchar(128) ,
  `xbox_image2` varchar(128) ,
  `xbox_image3` varchar(128) ,
  `xbox_image4` varchar(128) ,
  `xbox_image5` varchar(128) ,
  `xbox_image6` varchar(128) ,
  `xbox_image7` varchar(128) ,
  `xbox_image_1138_640` varchar(128) ,
  `xbox_image_424_640` varchar(128) ,
  `xbox_image_640_640` varchar(128) ,
  `xbox_image_424_100` varchar(128) ,
  `category` varchar(40) ,
  `copyright_type` int(11) ,
  `sub_title` varchar(64) ,
  PRIMARY KEY (`id`),
  KEY `tv_program_ext_fdn_1` (`code`),
  KEY `tv_program_ext_fdn_2` (`rating`)
) ;

DROP TABLE IF EXISTS `tv_media_file_ext` ;
CREATE TABLE `tv_media_file_ext` (
  `id` long(19) NOT NULL,
  `code` varchar(128) DEFAULT NULL,
  `content_code` varchar(128) DEFAULT NULL,
  `content_type` varchar(128) DEFAULT NULL,
  `type` int(10) DEFAULT NULL,
  `episode_num` int(10) DEFAULT NULL,
  `onlined_protocol` long(19) DEFAULT NULL ,
  `bitrate` varchar(256) DEFAULT NULL ,
  `bitrate_type` int(10) DEFAULT NULL,
  `onlined_protocol_audio` varchar(512) DEFAULT NULL ,
  `video_title` varchar(32) DEFAULT NULL ,
  `video_desc` varchar(64) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_media_file_ext_idx_1` (`code`),
  KEY `tv_media_file_ext_idx_2` (`content_code`),
  KEY `tv_media_file_ext_idx_3` (`episode_num`)
) ;

DROP TABLE IF EXISTS `tv_media_file_ext_fdn` ;
CREATE TABLE `tv_media_file_ext_fdn` (
  `id` long(19) NOT NULL,
  `code` varchar(128) DEFAULT NULL,
  `content_code` varchar(128) DEFAULT NULL,
  `content_type` varchar(128) DEFAULT NULL,
  `type` int(10) DEFAULT NULL,
  `episode_num` int(10) DEFAULT NULL,
  `onlined_protocol` long(19) DEFAULT NULL ,
  `bitrate` varchar(256) DEFAULT NULL ,
  `bitrate_type` int(10) DEFAULT NULL,
  `onlined_protocol_audio` varchar(512) DEFAULT NULL ,
  `video_title` varchar(32) DEFAULT NULL ,
  `video_desc` varchar(64) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_media_file_ext_fdn_idx_1` (`code`),
  KEY `tv_media_file_ext_fdn_idx_2` (`content_code`),
  KEY `tv_media_file_ext_fdn_idx_3` (`episode_num`)
) ;

DROP TABLE IF EXISTS `tv_media_detail_fdn` ;
CREATE TABLE `tv_media_detail_fdn` (
  `id` long(19) NOT NULL,
  `code` varchar(128) DEFAULT NULL,
  `media_file_code` varchar(128) DEFAULT NULL,
  `content_code` varchar(128) DEFAULT NULL,
  `content_type` varchar(128) DEFAULT NULL ,
  `type` int(10) DEFAULT NULL,
  `episode_num` int(10) DEFAULT NULL,
  `video_encode_type` int(11) DEFAULT NULL,
  `audio_encode_type` int(11) DEFAULT NULL,
  `source_url` varchar(1536) DEFAULT NULL ,
  `play_url` varchar(1536) DEFAULT NULL ,
  `path` varchar(1536) DEFAULT NULL,
  `cp_code` text ,
  `source` varchar(96) DEFAULT NULL ,
  `can_download` int(10) DEFAULT NULL ,
  `download_url` varchar(1536) DEFAULT NULL ,
  `can_onlineplay` int(10) DEFAULT NULL ,
  `duration` int(10) DEFAULT NULL ,
  `cdn` int(19) DEFAULT NULL,
  `storage_id` int(19) DEFAULT NULL,
  `file_size` long(19) DEFAULT NULL,
  `use_space` int(19) DEFAULT NULL,
  `audio_type` int(10) DEFAULT NULL,
  `screen_format` int(10) DEFAULT NULL,
  `captioning` int(1) DEFAULT NULL,
  `drm_type` int(1) DEFAULT NULL,
  `is_3d_video` int(1) DEFAULT NULL,
  `format_3d` int(1) DEFAULT NULL,
  `split_duration` varchar(4000) DEFAULT NULL,
  `online_status` int(1) DEFAULT NULL ,
  `format` varchar(256) DEFAULT NULL ,
  `protocol` varchar(256) DEFAULT NULL ,
  `bitrate` varchar(256) DEFAULT NULL ,
  `insert_time` timestamp NULL ,
  `update_time` timestamp NULL ,
  `resolution` varchar(384) DEFAULT NULL ,
  `audio_format` int(10) DEFAULT NULL,
  `fid` varchar(256) DEFAULT NULL,
  `tar_url` varchar(1024) DEFAULT NULL,
  `original` varchar(64) DEFAULT NULL,
  `old_id` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_rel_business_group_content` ;
CREATE TABLE `tv_rel_business_group_content` (
  `id` long(19)  ,
  `business_group_code` varchar(256) ,
  `content_code` varchar(256) ,
  `create_time` datetime ,
  `mark_url` varchar(150) ,
  `mark_position` int(11) ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_business_group_content_1` (`business_group_code`),
  KEY `tv_rel_business_group_content_2` (`content_code`)
) ;

DROP TABLE IF EXISTS `tv_rel_category` ;
CREATE TABLE `tv_rel_category` (
  `id` long(20)  ,
  `source_cate_code` varchar(128)  ,
  `target_cate_code` varchar(128)  ,
  `update_time` timestamp NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_user_group_category` ;
CREATE TABLE `tv_user_group_category` (
  `id` long(12)  ,
  `group_code` varchar(64) ,
  `category_code` varchar(128) ,
  `sequence` int(2) ,
  `parent_category_code` varchar(128) ,
  `category_name` varchar(128) ,
  `category_kind` int(2) ,
  `category_path` varchar(128) ,
  `category_display_name` varchar(128) ,
  `category_child_count` int(11) ,
  `bitrate_model_code` varchar(256) ,
  `update_time` datetime ,
  `product_codes` varchar(2048) ,
  `icon` varchar(256) DEFAULT NULL ,
  `show_way` int(2) DEFAULT '0' ,
  `hls_start_bitrate` varchar(10) ,
  PRIMARY KEY (`id`),
  KEY `tv_user_group_category_1` (`group_code`,`category_code`,`parent_category_code`),
  KEY `tv_user_group_category_2` (`category_code`)
) ;


DROP TABLE IF EXISTS `vas_program_table` ;
CREATE TABLE `vas_program_table` (
  `Id` long(11) NOT NULL ,
  `name` varchar(128) ,
  `code` varchar(128) ,
  `type` int(11) ,
  `genre` varchar(128) ,
  `keyword` varchar(256) ,
  `tags` varchar(256) ,
  `description` varchar(1024) ,
  `status` int(11) ,
  `synckey` varchar(256) ,
  `ORI_NAME` varchar(128) ,
  `NEW_DAY` smallint(6) ,
  `LEFT_DAY` smallint(6) ,
  `ACTOR_DISPLAY` varchar(256) ,
  `WRITER_DISPLAY` varchar(256) ,
  `search_name` varchar(128) ,
  `SORT_NAME` varchar(128) ,
  `ISSUE_YEAR` smallint(6) ,
  `ORIGIN_DATE` timestamp NOT NULL ,
  `COMPANY` varchar(128) ,
  `REGION` varchar(128) ,
  `RATING` varchar(64) ,
  `STAR_LEVEL` tinyint(4) ,
  `COMMENTS` varchar(512) ,
  `LANGUAGE` varchar(32) ,
  `VIEW_POINT` varchar(256) ,
  `AWARDS` varchar(64) ,
  `SPLITE_INFO` varchar(1024) ,
  `SMALLPIC` varchar(512) ,
  `POSTER` varchar(512) ,
  `STILL` varchar(512) ,
  `RESERVE1` varchar(256) ,
  `RESERVE2` varchar(256) ,
  `RESERVE3` varchar(256) ,
  `RESERVE4` varchar(256) ,
  `RESERVE5` varchar(256) ,
  `last_update_time` timestamp NOT NULL ,
  `active_status` int(11) ,
  `length` int(11) ,
  `episode_number` int(11) ,
  `domain` varchar(512) ,
  `start_time` timestamp NOT NULL ,
  `end_time` timestamp NOT NULL ,
  `country` varchar(128) ,
  `project` varchar(128) ,
  `publish_time` timestamp NOT NULL ,
  `content_type` varchar(128) ,
  `valid_time` timestamp NOT NULL ,
  `expire_time` timestamp NOT NULL ,
  `opt_code` varchar(128) ,
  PRIMARY KEY (`Id`),
  KEY `vas_program_table_1` (`code`),
  KEY `vas_program_table_2` (`code`),
  KEY `vas_program_table_3` (`code`)
) ;

DROP TABLE IF EXISTS `tv_topic` ;
CREATE TABLE `tv_topic` (
  `id` long(19) NOT NULL ,
  `code` varchar(32) NOT NULL ,
  `name` varchar(128) DEFAULT NULL ,
  `poster` varchar(256) DEFAULT NULL ,
  `bg_pic` varchar(256) DEFAULT NULL ,
  `template_type` int(2) DEFAULT NULL ,
  `description` varchar(256) DEFAULT NULL ,
  `status` int(2) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `bg_position` int(2) DEFAULT '0' ,
  PRIMARY KEY (`id`,`code`),
  UNIQUE KEY `id` (`id`),
  KEY `tv_topic_idx_1` (`code`,`status`)
) ;

DROP TABLE IF EXISTS `tv_topic_item` ;
CREATE TABLE `tv_topic_item` (
  `id` long(19) NOT NULL ,
  `topic_code` varchar(128) NOT NULL ,
  `topic_id` int(19) NOT NULL ,
  `item_type` int(1) NOT NULL DEFAULT '-1' ,
  `item_code` varchar(128) NOT NULL ,
  `category_code_path` varchar(128) DEFAULT NULL ,
  `item_uri` varchar(128) NOT NULL ,
  `item_res_uri` varchar(128) DEFAULT NULL ,
  `link_type` int(1) NOT NULL ,
  `package_name` varchar(128) DEFAULT NULL ,
  `show_name` varchar(128) NOT NULL ,
  `status` int(1) NOT NULL ,
  `sequence` int(11) NOT NULL DEFAULT '999' ,
  `begin_time` datetime DEFAULT NULL ,
  `end_time` datetime DEFAULT NULL ,
  `item_poster` varchar(256) DEFAULT NULL ,
  `item_suolue` varchar(256) DEFAULT NULL ,
  `director` varchar(256) DEFAULT NULL ,
  `actor` varchar(256) DEFAULT NULL ,
  `open_look_back` int(1) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `description` varchar(256) DEFAULT NULL ,
  `live_poster` varchar(256) DEFAULT NULL,
  `live_suolue` varchar(256) DEFAULT NULL,
  `show_type` int(1) NOT NULL DEFAULT '0' ,
  PRIMARY KEY (`id`,`topic_code`),
  KEY `tv_topic_item_idx_1` (`topic_code`,`status`)
) ;

DROP TABLE IF EXISTS `tv_live_program_item` ;
CREATE TABLE `tv_live_program_item` (
  `id` long(20) NOT NULL ,
  `channel_code` varchar(128) DEFAULT NULL ,
  `code` varchar(128) DEFAULT NULL ,
  `name` varchar(128) DEFAULT NULL ,
  `begin_time` datetime DEFAULT NULL ,
  `end_time` datetime DEFAULT NULL ,
  `open_look_back` int(1) DEFAULT NULL ,
  `create_time` timestamp NOT NULL ,
  `update_time` timestamp NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_rel_topic_category` ;
CREATE TABLE `tv_rel_topic_category` (
  `id` long(19) NOT NULL ,
  `category_code` varchar(256) DEFAULT NULL ,
  `category_path` varchar(256) DEFAULT NULL ,
  `topic_code` varchar(256) DEFAULT NULL ,
  `validity_status` long(1) DEFAULT NULL ,
  `publish_status` long(1) DEFAULT NULL ,
  `sequence` int(11) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_topic_category_idx_1` (`category_code`),
  KEY `tv_rel_topic_category_idx_2` (`topic_code`,`validity_status`)
) ;

DROP TABLE IF EXISTS `tv_topic_category` ;
CREATE TABLE `tv_topic_category` (
  `id` long(11) NOT NULL ,
  `code` varchar(32) NOT NULL ,
  `parent_code` varchar(32) DEFAULT NULL ,
  `category_path` varchar(128) DEFAULT NULL ,
  `name` varchar(128) DEFAULT NULL ,
  `description` varchar(512) DEFAULT NULL ,
  `icon1` varchar(128) DEFAULT NULL ,
  `sequence` int(4) DEFAULT NULL ,
  `mark_position` int(2) DEFAULT NULL ,
  `mark_uri` varchar(256) DEFAULT NULL ,
  `status` int(2) DEFAULT NULL ,
  `child_number` int(9) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_topic_category_1` (`code`,`status`)
) ;

DROP TABLE IF EXISTS `tv_dict_value` ;
CREATE TABLE `tv_dict_value` (
  `id` bigint(19) NOT NULL ,
  `code` varchar(128) DEFAULT NULL,
  `dict_key` varchar(128) DEFAULT NULL,
  `dict_value` varchar(128) DEFAULT NULL,
  `create_time` timestamp DEFAULT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_search_tag` ;
CREATE TABLE `tv_search_tag` (
  `id` int(11) NOT NULL ,
  `tag_type` int(11) DEFAULT NULL ,
  `search_type` int(11) DEFAULT NULL ,
  `tag_name` varchar(128) DEFAULT NULL,
  `tag_keyword` varchar(128) DEFAULT NULL,
  `order_num` int(11) DEFAULT NULL ,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_subscriber_local` ;
CREATE TABLE `tv_subscriber_local` (
  `id` bigint(12) NOT NULL ,
  `customer_id` bigint(12) DEFAULT NULL,
  `nick_name` varchar(64) DEFAULT NULL,
  `tvid` varchar(128) DEFAULT NULL,
  `user_account` varchar(128) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `status` int(2) DEFAULT '1' ,
  `region_code` varchar(16) DEFAULT NULL,
  `user_type` varchar(64) DEFAULT NULL,
  `user_group` varchar(64) DEFAULT NULL,
  `terminal_model` varchar(128) DEFAULT NULL,
  `ip_address` varchar(250) DEFAULT NULL,
  `open_time` datetime DEFAULT NULL,
  `update_time` timestamp DEFAULT NULL ,
  `terminal_provider` varchar(64) DEFAULT NULL,
  `batch_no` varchar(128) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `terminal_group_code` varchar(128) DEFAULT NULL,
  `user_kind` int(2) unsigned DEFAULT '0',
  `series_code` varchar(50) DEFAULT NULL ,
  `busi_type` int(2) DEFAULT '1' ,
  `tvprofile` varchar(1024) DEFAULT NULL ,
  `net_operator_code` varchar(256) DEFAULT NULL ,
  `package_code` varchar(256) DEFAULT NULL ,
  `distributor` varchar(256) DEFAULT NULL ,
  `subscriber_group_code` varchar(256) DEFAULT NULL ,
  `partner_user_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_subscriber_local_u_1` (`user_account`),
  KEY `tv_subscriber_local_idx_1` (`tvid`,`terminal_model`,`terminal_provider`),
  KEY `tv_subscriber_local_idx_2` (`customer_id`),
  KEY `tv_subscriber_local_idx_3` (`partner_user_id`)
) ;

DROP TABLE IF EXISTS `tv_message` ;
CREATE TABLE `tv_message` (
  `id` bigint(19) NOT NULL ,
  `code` varchar(256) NOT NULL,
  `type` int(2) DEFAULT NULL ,
  `display_type` int(2) DEFAULT NULL ,
  `pop_type` int(2) DEFAULT NULL ,
  `style` int(2) DEFAULT NULL ,
  `icon` varchar(256) DEFAULT NULL,
  `SUMMARY` varchar(4000) DEFAULT NULL,
  `validity_starttime` datetime DEFAULT NULL,
  `validity_endtime` datetime DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `onclick_flag` int(2) DEFAULT NULL ,
  `uri` varchar(128) DEFAULT NULL,
  `title` varchar(4000) DEFAULT NULL,
  `partner_code` varchar(4000) DEFAULT NULL,
  `term_version` varchar(256) DEFAULT NULL,
  `sender` varchar(256) DEFAULT NULL,
  `subscriber_group_code` varchar(4000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

-- EPG 5.0
DROP TABLE IF EXISTS `tv_rel_ug_nav` ;
CREATE TABLE `tv_rel_ug_nav` (
  `id` long(11) NOT NULL ,
  `user_group_code` varchar(128) NOT NULL ,
  `nav_id` int(11) NOT NULL ,
  `logo_pic` varchar(256) DEFAULT NULL ,
  `logo_show` tinyint(4) NOT NULL ,
  `logo_align` tinyint(4) NOT NULL ,
  `bg_pic` varchar(256) DEFAULT NULL ,
  `bg_pic_policy` tinyint(4) NOT NULL ,
  `ui_update_time` datetime NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_rel_ug_nav_u_1` (`user_group_code`)
) ;

DROP TABLE IF EXISTS `tv_rel_ug_nav_page` ;
CREATE TABLE `tv_rel_ug_nav_page` (
  `id` long(11) NOT NULL ,
  `user_group_code` varchar(128) NOT NULL ,
  `nav_page_code` varchar(64) DEFAULT NULL ,
  `nav_id` int(11) NOT NULL ,
  `nav_page_id` int(11) NOT NULL ,
  `title` varchar(64) NOT NULL ,
  `bg_pic` varchar(256) NOT NULL ,
  `row_count` tinyint(4) NOT NULL ,
  `col_count` tinyint(4) NOT NULL ,
  `row_spaces` varchar(32) NOT NULL ,
  `col_spaces` varchar(32) NOT NULL ,
  `tab_type` tinyint(4) NOT NULL ,
  `is_first_tab` tinyint(4) NOT NULL ,
  `is_home_tab` tinyint(4) NOT NULL ,
  `sort` int(11) NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_rel_ug_nav_page_u_1` (`user_group_code`,`nav_page_code`)
) ;

DROP TABLE IF EXISTS `tv_recmd` ;
CREATE TABLE `tv_recmd` (
  `id` long(11) NOT NULL ,
  `code` varchar(64) NOT NULL ,
  `nav_id` int(11) NOT NULL ,
  `nav_page_id` int(11) NOT NULL ,
  `left` tinyint(4) NOT NULL ,
  `top` tinyint(4) NOT NULL ,
  `width` tinyint(4) NOT NULL ,
  `height` tinyint(4) NOT NULL ,
  `ui_type` tinyint(4) NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_recmd_u_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_recmd_data` ;
CREATE TABLE `tv_recmd_data` (
  `id` long(11) NOT NULL ,
  `recmd_id` int(11) NOT NULL ,
  `name` varchar(128) DEFAULT NULL ,
  `title` varchar(128) DEFAULT NULL ,
  `show_type` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_rel_ug_recmd_data` ;
CREATE TABLE `tv_rel_ug_recmd_data` (
  `id` long(11) NOT NULL ,
  `user_group_code` varchar(64) NOT NULL ,
  `recmd_data_id` int(11) NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tv_rel_ug_recmd_data_u_1` (`user_group_code`,`recmd_data_id`)
) ;

DROP TABLE IF EXISTS `tv_recmd_data_item` ;
CREATE TABLE `tv_recmd_data_item` (
  `id` long(11) NOT NULL ,
  `recmd_data_id` int(11) NOT NULL ,
  `title` varchar(128) DEFAULT NULL ,
  `description` varchar(512) DEFAULT NULL ,
  `target_type` int(11) NOT NULL ,
  `target` varchar(512) NOT NULL ,
  `target_cate` varchar(128) DEFAULT NULL ,
  `category_code_path` varchar(512) DEFAULT NULL ,
  `poster` varchar(256) DEFAULT NULL ,
  `poster_bak` varchar(256) DEFAULT NULL ,
  `mark_left_top` varchar(256) DEFAULT NULL ,
  `mark_left_bottom` varchar(256) DEFAULT NULL ,
  `mark_right_top` varchar(256) DEFAULT NULL ,
  `mark_right_bottom` varchar(256) DEFAULT NULL ,
  `sort` int(11) NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_recmd_video_data_item` ;
CREATE TABLE `tv_recmd_video_data_item` (
  `id` long(11) NOT NULL ,
  `recmd_data_id` int(11) NOT NULL ,
  `title` varchar(128) DEFAULT NULL ,
  `description` varchar(512) DEFAULT NULL ,
  `target_type` int(11) NOT NULL ,
  `target` varchar(512) NOT NULL ,
  `category_code_path` varchar(512) DEFAULT NULL ,
  `video_code` varchar(128) DEFAULT NULL ,
  `video_category` varchar(128) DEFAULT NULL ,
  `play_type` tinyint(4) NOT NULL ,
  `big_pg` varchar(256) DEFAULT NULL ,
  `sort` int(11) NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_rel_category_content` ;
CREATE TABLE `tv_rel_category_content` (
  `id` long(20) NOT NULL,
  `content_type` int(11) DEFAULT NULL ,
  `content_code` varchar(128) DEFAULT NULL ,
  `extend_info` varchar(256) DEFAULT NULL ,
  `category_code` varchar(128) DEFAULT NULL ,
  `show_name` varchar(128) DEFAULT NULL ,
  `ipd_show_name` varchar(128) DEFAULT NULL ,
  `item_type` int(11) DEFAULT NULL ,
  `item_index` int(11) DEFAULT NULL ,
  `onlined_protocol` long(19) DEFAULT NULL ,
  `onlined_bitrate` varchar(2048) DEFAULT NULL ,
  `onlined_protocol_audio` varchar(512) DEFAULT NULL ,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tv_rel_category_content_idx_1` (`category_code`, `content_type`)
) ;

DROP TABLE IF EXISTS `tv_ug_nav_extra_logo` ;
CREATE TABLE `tv_ug_nav_extra_logo` (
  `id` int(11) NOT NULL,
  `user_group_code` varchar(128) NOT NULL ,
  `nav_id` int(11) NOT NULL ,
  `image_url` varchar(512) NOT NULL ,
  `v_align` tinyint(4) NOT NULL ,
  `h_align` tinyint(4) NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_ug_nav_extra_logo_idx_1` (`user_group_code`,`nav_id`)
) ;

DROP TABLE IF EXISTS `tv_ug_quick_category` ;
CREATE TABLE `tv_ug_quick_category` (
  `id` bigint NOT NULL,
  `user_group_code` varchar(128) NOT NULL ,
  `category_code` varchar(128) NOT NULL ,
  `image_url` varchar(512) NOT NULL ,
  `sequence` int NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_ug_quick_category_idx_1` (`user_group_code`)
) ;

DROP TABLE IF EXISTS `tv_rel_content_spotlight` ;
CREATE TABLE `tv_rel_content_spotlight` (
  `id` bigint NOT NULL,
  `content_code` varchar(128) NOT NULL ,
  `spotlight_code` varchar(128) NOT NULL ,
  `category_path` varchar(512) NOT NULL ,
  `title` varchar(128) NOT NULL ,
  `poster` varchar(256) NOT NULL ,
  `sequence` int NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_content_spotlight_idx_1` (`content_code`)
) ;

DROP TABLE IF EXISTS `epg_content_type` ;
CREATE TABLE `epg_content_type` (
  `id` bigint NOT NULL,
  `name` varchar(256) NOT NULL ,
  `code` varchar(256) NOT NULL ,
  `video_staff_type` int NOT NULL ,
  `episode_title_display_type` int NOT NULL ,
  `last_episode_display_type` int NOT NULL ,
  `episode_sort_type` int NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `epg_content_type_idx_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_program_biz` ;
CREATE TABLE `tv_program_biz` (
  `id` bigint NOT NULL,
  `episode_title_display_type` tinyint NOT NULL ,
  `last_episode_display_type` tinyint NOT NULL ,
  `episode_sort_type` tinyint NOT NULL ,
  `staff_type` tinyint NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_ug_quick_category` ;
CREATE TABLE `tv_ug_quick_category` (
  `id` bigint NOT NULL,
  `user_group_code` varchar(128) NOT NULL ,
  `category_code` varchar(128) NOT NULL ,
  `category_name` varchar(512) NOT NULL ,
  `sequence` int NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_ug_quick_category_idx_1` (`user_group_code`)
) ;

DROP TABLE IF EXISTS `tv_shortcut` ;
CREATE TABLE `tv_shortcut` (
  `id` bigint NOT NULL,
  `title` varchar(128) NOT NULL ,
  `url` varchar(256) NOT NULL ,
  `pic_path` varchar(512) NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_rel_shortcut_ug` ;
CREATE TABLE `tv_rel_shortcut_ug` (
  `id` bigint NOT NULL,
  `user_group_code` varchar(128) NOT NULL ,
  `shortcut_id` bigint NOT NULL ,
  `shortcut_type` tinyint NOT NULL ,
  `sort` int NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_shortcut_ug_idx_1` (`user_group_code`, `shortcut_id`)
) ;

DROP TABLE IF EXISTS `tv_product_chargeplan` ;
CREATE TABLE `tv_product_chargeplan` (
  `id` bigint NOT NULL,
  `code` varchar(256) DEFAULT NULL,
  `price` float DEFAULT NULL,
  `validity_status` bigint DEFAULT NULL ,
  `validity_starttime` timestamp DEFAULT NULL ,
  `validity_endtime` timestamp DEFAULT NULL ,
  `publish_status` bigint DEFAULT NULL ,
  `create_time` timestamp NOT NULL ,
  `update_time` timestamp NOT NULL ,
  `operator_name` varchar(256) DEFAULT NULL,
  `free_episode` bigint DEFAULT NULL,
  `description` varchar(3072) DEFAULT NULL,
  `mark_id` int DEFAULT NULL,
  `mark_position` int DEFAULT NULL,
  `free_episode_new` varchar(128) DEFAULT NULL ,
  `episode_mark_id` bigint DEFAULT NULL ,
  `episode_mark_display_type` tinyint DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_product_chargeplan_idx_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_recommend_item_mark` ;
CREATE TABLE `tv_recommend_item_mark` (
  `id` bigint NOT NULL,
  `code` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `pic` text,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_cloud_recom_source_config` ;
CREATE TABLE `tv_cloud_recom_source_config` (
  `id` bigint(19) NOT NULL,
  `name` varchar(128) NOT NULL,
  `code` varchar(128) NOT NULL ,
  `cloud_rem_code` varchar(128) NOT NULL ,
  `source_type` int(2) NOT NULL ,
  `content_type` varchar(128) DEFAULT NULL ,
  `category_path` varchar(200) NOT NULL ,
  `record_number` int(11) NOT NULL ,
  `sort` varchar(20) NOT NULL ,
  `source_sort` int(11) NOT NULL ,
  `update_time` datetime DEFAULT NULL ,
  `user_group_code` text,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `epg_subject_ext` ;
CREATE TABLE `epg_subject_ext` (
  `ID` bigint(19) NOT NULL,
  `NAME` varchar(128) DEFAULT NULL,
  `SHOW_NAME` varchar(128) DEFAULT NULL,
  `CODE` varchar(128) DEFAULT NULL,
  `STATUS` bigint(2) DEFAULT NULL,
  `ICON` varchar(256) DEFAULT NULL,
  `VALID_TIME` date DEFAULT NULL,
  `EXPIRE_TIME` date DEFAULT NULL,
  `CREATE_TIME` date DEFAULT NULL,
  `UPDATE_TIME` date DEFAULT NULL,
  `OPERATOR_ID` bigint(19) DEFAULT NULL,
  `DESCRIPTION` varchar(256) DEFAULT NULL,
  `STA_STATUS` bigint(2) DEFAULT NULL,
  `SOURCE` varchar(64) DEFAULT NULL,
  `DISPATCH_TIME` datetime DEFAULT NULL,
  `RATIO` bigint(2) DEFAULT '0',
  `ONLINED_PROTOCOL` bigint(19) DEFAULT NULL ,
  `onlined_bitrate` varchar(2048) DEFAULT NULL,
  `icon2` varchar(256) DEFAULT NULL ,
  `big_image1` varchar(256) DEFAULT NULL ,
  `big_image2` varchar(256) DEFAULT NULL ,
  PRIMARY KEY (`ID`),
  KEY `epg_subject_ext_idx_1` (`CODE`)
) ;

DROP TABLE IF EXISTS `tv_recommend_item_ext_fdn` ;
CREATE TABLE `tv_recommend_item_ext_fdn` (
  `id` bigint(20) NOT NULL,
  `recommend_id` bigint(20) DEFAULT NULL,
  `recommend_code` varchar(1024) DEFAULT NULL,
  `item_display` varchar(1024) DEFAULT NULL,
  `item_display_type` varchar(1024) DEFAULT NULL ,
  `item_target` varchar(1024) DEFAULT NULL,
  `item_target_type` varchar(1024) DEFAULT NULL ,
  `power` int(11) DEFAULT NULL,
  `item_begin_time` datetime DEFAULT NULL,
  `item_end_time` datetime DEFAULT NULL,
  `source_cate` varchar(1024) DEFAULT NULL,
  `name` varchar(3000) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `item_display_big` varchar(1024) DEFAULT NULL,
  `description` varchar(3000) DEFAULT NULL,
  `category_code_path` varchar(128) DEFAULT NULL,
  `onlined_protocol` bigint(19) DEFAULT NULL ,
  `item_display2` varchar(1024) DEFAULT NULL,
  `item_display_big2` varchar(1024) DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `onlined_bitrate` varchar(2048) DEFAULT NULL,
  `onlined_protocol_audio` varchar(512) DEFAULT NULL ,
  `app_mode` int(2) DEFAULT '2' ,
  `res_uri` varchar(256) DEFAULT NULL ,
  `mark` varchar(256) DEFAULT NULL ,
  `mark_position` int(2) DEFAULT '0' ,
  `item_xbox_img` varchar(1024) DEFAULT NULL,
  `which_episode_number` varchar(32) DEFAULT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_recommend_item_ext_idx_1` (`recommend_code`)
) ;

DROP TABLE IF EXISTS `tv_channel_package` ;
CREATE TABLE `tv_channel_package` (
  `id`            bigint       NOT NULL,
  `code`          varchar(64)  NOT NULL ,
  `name`          varchar(128) NOT NULL ,
  `template_code` varchar(64)  NOT NULL ,
  `sort`          int          NOT NULL ,
  `update_time`   datetime     NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_channel_package_u_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_rel_cp_category` ;
CREATE TABLE `tv_rel_cp_category` (
  `id`             bigint       NOT NULL,
  `cp_id`          bigint       NOT NULL ,
  `category_code`  varchar(128) NOT NULL ,
  `title`          varchar(128) NOT NULL ,
  `template_code`  varchar(64)  NOT NULL ,
  `kind`           int          NOT NULL ,
  `reference_code` varchar(128) ,
  `sort`           int          NOT NULL ,
  `update_time`    datetime     NOT NULL ,
  PRIMARY KEY (`id`),
  KEY `tv_rel_cp_category_idx_1` (`cp_id`)
) ;

DROP TABLE IF EXISTS `tv_bg_pic` ;
CREATE TABLE `tv_bg_pic` (
  `id`          bigint       NOT NULL,
  `ug_code`     varchar(128) NOT NULL ,
  `bg_pic`      varchar(256) NOT NULL ,
  `update_time` datetime     NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_bg_pic_u_1` (`ug_code`)
) ;

DROP TABLE IF EXISTS `tv_logo` ;
CREATE TABLE `tv_logo` (
  `id`          bigint       NOT NULL,
  `ug_code`     varchar(128) NOT NULL ,
  `pic`         varchar(256) NOT NULL ,
  `v_align`     tinyint      NOT NULL ,
  `h_align`     tinyint      NOT NULL ,
  `update_time` datetime     NOT NULL ,
  PRIMARY KEY (`id`)
) ;

DROP TABLE IF EXISTS `tv_floor_block` ;
CREATE TABLE `tv_floor_block` (
  `id`          bigint      NOT NULL,
  `code`        varchar(64) NOT NULL ,
  `title`       varchar(64) NOT NULL ,
  `type`        int         NOT NULL ,
  `top`         tinyint     NOT NULL ,
  `ai_sort`     tinyint     NOT NULL ,
  `pic`         varchar (256)        ,
  `update_time` datetime    NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_floor_block_u_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_floor` ;
CREATE TABLE `tv_floor` (
  `id`             bigint      NOT NULL,
  `code`           varchar(64) NOT NULL ,
  `title`          varchar(64) NOT NULL ,
  `row_count`      int         NOT NULL ,
  `col_count`      int         NOT NULL ,
  `row_scaling`    varchar(64) NOT NULL ,
  `floor_block_id` bigint      NOT NULL ,
  `type`           int         NOT NULL ,
  `top`            tinyint     NOT NULL ,
  `display_title`  tinyint     NOT NULL ,
  `update_time`    datetime    NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_floor_u_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_rel_ug_floor` ;
CREATE TABLE `tv_rel_ug_floor` (
  `id`          bigint       NOT NULL,
  `ug_code`     varchar(128) NOT NULL ,
  `floor_id`    bigint       NOT NULL ,
  `sort`        bigint       NOT NULL ,
  `update_time` datetime     NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_rel_ug_floor_u_1` (`ug_code`, `floor_id`)
) ;

DROP TABLE IF EXISTS `tv_floor_recmd` ;
CREATE TABLE `tv_floor_recmd` (
  `id`               bigint      NOT NULL,
  `code`             varchar(64) NOT NULL ,
  `floor_id`         bigint      NOT NULL ,
  `left`             int         NOT NULL ,
  `top`              int         NOT NULL ,
  `width`            int         NOT NULL ,
  `height`           int         NOT NULL ,
  `repeat`           int         NOT NULL ,
  `show_type`        int         NOT NULL ,
  `ai_recmd`         tinyint     NOT NULL ,
  `ai_target_type`   int                  ,
  `ai_content_type`  varchar(64)          ,
  `update_time`      datetime    NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE  KEY `tv_floor_recmd_u_1` (`code`)
) ;

DROP TABLE IF EXISTS `tv_floor_recmd_item` ;
CREATE TABLE `tv_floor_recmd_item` (
  `id`                    bigint       NOT NULL,
  `floor_recmd_id`        bigint       NOT NULL COMMENT '所属的楼层推荐位的 ID',
  `title`                 varchar(128)          COMMENT '推荐位元素标题',
  `sub_title`             varchar(128)          COMMENT '副标题',
  `target_type`           int          NOT NULL COMMENT '推荐内容',
  `target`                varchar(512) NOT NULL COMMENT '目标内容',
  `category_code_path`    varchar(512)          COMMENT '栏目路径',
  `channel_template_code` varchar(128)          COMMENT '栏目的 template_code',
  `channel_code_path`     varchar(512)          COMMENT '频道包到频道（栏目）的路径',
  `poster`                varchar(256) NOT NULL COMMENT '海报',
  `poster_bak`            varchar(256)          COMMENT '备用海报',
  `video_code`            varchar(128)          COMMENT '导视短片代码或直播代码',
  `video_category`        varchar(128)          COMMENT '导视短片所属栏目',
  `play_type`             tinyint      NOT NULL COMMENT '播放类型',
  `mark_left_top`         bigint                COMMENT '左上角标',
  `mark_left_bottom`      bigint                COMMENT '左下角标',
  `mark_right_top`        bigint                COMMENT '右上角标',
  `mark_right_bottom`     bigint                COMMENT '右下角标',
  `sort`                  bigint       NOT NULL COMMENT '依据此字段升序排序',
  `update_time`           datetime     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ;
