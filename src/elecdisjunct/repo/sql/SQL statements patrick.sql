create table users(
user_id integer not null auto_increment,
  email varchar(30),
  password binary(32),
  salt binary(16),
  nickname varchar(30),
  game_code integer,
  constraint user_id_pk primary key (user_id)
);

CREATE TABLE matches(
    match_id integer not null auto_increment,
    round integer,
    turn integer,
    ongoing boolean,
  constraint match_id_pk primary key (match_id)
);

create table lobby (
game_code integer not null,
host varchar(25),
match_id int,
constraint game_code_pk primary key (game_code)
);

alter table lobby
  add constraint match_id_fk foreign key (match_id)
  references matches (match_id);

alter table users
    add constraint game_code_fk foreign key (game_code)
  references lobby (game_code);



--inserts into matches
INSERT INTO matches (ongoing) values ('0');

--insert into user (TEST)
INSERT into users (email, nickname) VALUES ('kali@post.com', 'johnDoe');

--(TEST) insert into lobby
INSERT into lobby (game_code, host, match_id) VALUES ('456789', '3', '1');

--insert into user after creating lobby
UPDATE users set game_code = '456789' where user_id = '3';



--check for max player in a lobby
select COUNT(user_id) from users where game_code = '456789';



-- If host leaves the game
UPDATE users set game_code = NULL where user_id = '3';

DELETE FROM lobby where game_code = '456789';

DELETE FROM matches where match_id = '1';

--if users join the game

SELECT game_code FROM lobby where game_code = '456789';

--lobby class


--if true

--get user_id

--run select that checks max players in a lobby

-- if less than 4

-- create object player

UPDATE users set game_code = '456789' where user_id = '3';

SELECT nickname from users where game_code = '456789';


--before starting the game

--to get match id
SELECT matches.match_id from matches, lobby where lobby.match_id = matches.match_id

UPDATE matches set ongoing = '1', turn = '0', round = '0' where match_id = '2';

--create a select statement with users and that returns match_id for the game and game_code
SELECT lobby.match_id, users.user_id FROM lobby, users, matches WHERE lobby.match_id = matches.match_id and users.game_code = lobby.game_code and lobby.game_code = '456789';

--create a player for each user in lobby (connect values with select)
INSERT into player (match_id, user_id, score) VALUES ('2', '3', '0');

--for each user (do that at the end
UPDATE users set game_code = NULL where game_code = '456789';

--delete from lobby
DELETE FROM lobby where game_code = '456789';



-- create table player
CREATE TABLE player(
    match_id integer not null,
    user_id integer not null,
    score integer,
    color varchar(10),
    active_in_turn varchar(10),
   	CONSTRAINT PRIMARY key (match_id, user_id)
    );

UPDATE player SET match_id = '2', user_id = '3', score = '0' WHERE(
    FROM lobby, matches, users WHERE
    lobby.match_id = matches.match_id and users.game_code = lobby.game_code and lobby.game_code = '456789');

