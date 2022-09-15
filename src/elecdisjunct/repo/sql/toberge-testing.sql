INSERT INTO users(email, nickname, password, salt) VALUES
                ('dolan@ducke.quack', 'dolan', 1234, 1234),
                ('reodor@felgen.bike', 'reoDoor', 1234, 1234);

INSERT INTO game(round, turn, ongoing) VALUES (4, 1, 1);

INSERT INTO player(match_id, user_id, score, color, active_in_turn) VALUES
                  (1,         1,      23,     'blue',     1),
                  (1,         2,      19,     'red',      2);

INSERT INTO node(match_id, pos_x, pos_y, type, level, owner, updated_round, updated_turn) VALUES
                (1,           1,    1, 'solar', 1,        1,            2,        1),
                (1,           2,    7, 'coal',  2,        2,            3,        2);

INSERT INTO line(match_id, from_x, from_y, to_x, to_y, owner) VALUES
                (1,           1,      1,     2,    7,     1);


-- spørringene som må gjøres:

-- finne oppdaterte nodes/lines
-- sjekke scoren til spillerne
-- osv

SELECT pos_x, pos_y, level, owner FROM node WHERE match_id = 1 AND (updated_round > 2 OR (updated_round = 2 AND updated_turn > 0));

-- oppdatere lines
-- oppdatere players

-- adding to enum:
ALTER TABLE node MODIFY COLUMN type ENUM('coal', 'gas', 'fission', 'fusion', 'wind', 'solar', 'hydroelectric', 'city') NOT NULL;