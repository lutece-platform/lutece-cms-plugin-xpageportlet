--
-- Structure for table xpage_portlet
--
DROP TABLE IF EXISTS xpageportlet_portlet;
CREATE TABLE xpageportlet_portlet (
  id_portlet INT DEFAULT 0 NOT NULL,
  xpage_name VARCHAR(255) DEFAULT '' NOT NULL,
  nb_params INT DEFAULT 0 NOT NULL,
  PRIMARY KEY (id_portlet)
);

--
-- Structure for table xpageportlet_params
--
DROP TABLE IF EXISTS xpageportlet_params;
CREATE TABLE xpageportlet_params (
  id_portlet INT DEFAULT 0 NOT NULL,
  param_key VARCHAR(100) DEFAULT '' NOT NULL,
  param_value VARCHAR(100) DEFAULT '' NOT NULL
);
