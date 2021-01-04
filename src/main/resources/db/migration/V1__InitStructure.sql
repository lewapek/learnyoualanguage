CREATE TABLE lang (
  id         serial      PRIMARY KEY,
  name       varchar(50) UNIQUE NOT NULL,
  short_name varchar(10) UNIQUE NOT NULL
);

CREATE TABLE word (
  id                 bigserial                PRIMARY KEY,
  name               text                     NOT NULL,
  source_language_id int                      NOT NULL REFERENCES lang (id),
  target_language_id int                      NOT NULL REFERENCES lang (id),
  target_names       text[]                   NOT NULL,
  synonyms           text[]                   NOT NULL,
  usages             text[]                   NOT NULL,
  created            timestamp with time zone NOT NULL,
  updated            timestamp with time zone NOT NULL,
  UNIQUE (name, source_language_id, target_language_id)
);
CREATE UNIQUE INDEX i_word__created ON word (created);
CREATE UNIQUE INDEX i_word__updated ON word (updated);

CREATE TABLE learning_stats (
  id                 bigserial                PRIMARY KEY,
  word_id            bigint                   NOT NULL UNIQUE REFERENCES word (id),
  hits               int                      NOT NULL,
  misses             int                      NOT NULL,
  hits_ratio         real,
  last_hit           timestamp with time zone,
  last_miss          timestamp with time zone,
  max_hits_in_row    int                      NOT NULL,
  max_misses_in_row  int                      NOT NULL,
  last_hits_in_row   int                      NOT NULL,
  last_misses_in_row int                      NOT NULL
);
CREATE INDEX i_learning_stats__hits ON learning_stats (hits);
CREATE INDEX i_learning_stats__misses ON learning_stats (misses);
CREATE INDEX i_learning_stats__hits_ratio ON learning_stats (hits_ratio);
CREATE INDEX i_learning_stats__last_hit ON learning_stats (last_hit);
CREATE INDEX i_learning_stats__last_miss ON learning_stats (last_miss);
