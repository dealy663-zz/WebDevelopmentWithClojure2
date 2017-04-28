-- :name add-user! :! :n
-- :doc adds a new user
INSERT INTO users
(id, pass)
VALUES (:id, :pass)

-- :name add-users! :! :n
-- :doc add multiple users
INSERT INTO users
(id, pass)
VALUES :t*:users