
DROP TABLE IF EXISTS line, node, lobby, player, game, users;

CREATE TABLE users(
                   user_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                   email VARCHAR(30) NOT NULL UNIQUE,
                   nickname VARCHAR(30) NOT NULL,
                   password BINARY(32) NOT NULL,
                   salt BINARY(16) NOT NULL,
                   noob BOOLEAN NOT NULL DEFAULT 1, -- if the user wants help or not, defaults to overeager helpfulness
                   logged_in BOOLEAN NOT NULL DEFAULT 0,
                   game_code INTEGER REFERENCES lobby(game_code), --  or however you'd like it
                   INDEX email_index (email)
);

CREATE TABLE lobby(
                   game_code INTEGER NOT NULL PRIMARY KEY,
                   host INTEGER NOT NULL UNIQUE REFERENCES users(user_id),
                   match_id INTEGER NOT NULL UNIQUE REFERENCES game(match_id)
);

CREATE TABLE game(
                   match_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
                   round INTEGER NOT NULL DEFAULT -1,
                   turn INTEGER NOT NULL DEFAULT 0,
                   ongoing BOOLEAN NOT NULL DEFAULT 0,
                   map_name VARCHAR(6) DEFAULT NULL, -- TODO possible extension
                   INDEX game_ping_index (/*match_id,*/ round, turn) -- UNSURE about this one, *might* improve
);

CREATE TABLE player(
                     match_id INTEGER NOT NULL REFERENCES game(match_id),
                     user_id INTEGER NOT NULL REFERENCES users(user_id),
                     score INTEGER NOT NULL DEFAULT 0,
                     color ENUM('RED', 'ORANGE', 'YELLOW', 'GREEN', 'BLUE', 'PURPLE', 'BUG') NOT NULL,
                     action_item ENUM('CUT', 'BOOST', 'BREAK') DEFAULT NULL, -- TODO decide (this is for sabotage - letting other players see what opportunity this player has)
                     active_in_turn INTEGER NOT NULL,
                     present BOOLEAN NOT NULL DEFAULT 1, -- set to false if this player leaves the match
                     updated_round INTEGER NOT NULL DEFAULT -1, -- TODO remember that players are only updated after round end
                     updated_turn INTEGER NOT NULL DEFAULT 0, -- remove dis
                     CONSTRAINT player_pk PRIMARY KEY (match_id, user_id),
                     INDEX player_update_index (match_id, updated_round, updated_turn) -- added update index - remove when things are reconfigured
);

CREATE TABLE node(
                   match_id INTEGER NOT NULL REFERENCES game(match_id),
                   pos_x INTEGER NOT NULL,
                   pos_y INTEGER NOT NULL,
                   type ENUM('coal', 'gas', 'fission', 'fusion', 'wind', 'solar', 'hydroelectric', 'city') NOT NULL, -- TODO is this necessary at all?
                   level INTEGER NOT NULL DEFAULT 1,
                   broken BOOLEAN NOT NULL DEFAULT 0,
                   owner INTEGER,
                   updated_round INTEGER NOT NULL DEFAULT -1,
                   updated_turn INTEGER NOT NULL DEFAULT 0,
                   CONSTRAINT node_owner_fk FOREIGN KEY (match_id, owner) REFERENCES player(match_id, user_id),
                   CONSTRAINT node_pk PRIMARY KEY (match_id, pos_x, pos_y),
                   INDEX node_update_index (match_id, updated_round, updated_turn) -- added update index
);

CREATE TABLE line(
                   match_id INTEGER NOT NULL REFERENCES game(match_id),
                   from_x INTEGER NOT NULL,
                   from_y INTEGER NOT NULL,
                   to_x INTEGER NOT NULL,
                   to_y INTEGER NOT NULL,
                   level INTEGER NOT NULL DEFAULT 1,
                   broken BOOLEAN NOT NULL DEFAULT 0,
                   owner INTEGER,
                   updated_round INTEGER NOT NULL DEFAULT -1,
                   updated_turn INTEGER NOT NULL DEFAULT 0,
                   CONSTRAINT line_pk PRIMARY KEY (match_id, from_x, from_y, to_x, to_y),
                   CONSTRAINT line_from_fk FOREIGN KEY (match_id, from_x, from_y) REFERENCES node(match_id, pos_x, pos_y),
                   CONSTRAINT line_to_fk FOREIGN KEY (match_id, to_x, to_y) REFERENCES node(match_id, pos_x, pos_y),
                   CONSTRAINT line_owner_fk FOREIGN KEY (match_id, owner) REFERENCES player(match_id, user_id),
                   INDEX line_update_index (match_id, updated_round, updated_turn) -- added update index
);