(ns guestbook.routes.home
  (:require [guestbook.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [guestbook.db.core :as db]
            [ring.util.response :refer [response status]]
            [clojure.tools.logging :as log]))

(defn home-page []
  (layout/render "home.html"))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/"             [] (home-page))
  (GET "/messages"     [] (do (log/debug "getting messages")
                              (let [resp (response (db/get-messages))]
                                (log/debug (str "received messages: " resp))
                                resp)))
  (GET "/about"        [] (about-page)))

