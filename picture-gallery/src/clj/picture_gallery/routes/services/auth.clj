(ns picture-gallery.routes.services.auth
  (:require [picture-gallery.db.core :as db]
            [ring.util.http-response :as response]
            [picture-gallery.validation :refer [registration-errors]]
            [buddy.hashers :as hashers]
            [clojure.tools.logging :as log])
  (:import (java.sql SQLException)))

(defn handle-registration-error [e]
  (if (and
        (instance? SQLException e)
        (-> e (.getNextException)
            (.getMessage)
            (.startsWith "ERROR: duplicate key value")))
    (response/precondition-failed
      {:result :error
       :message "user with the selected ID already exists"})
    (do
      (log/error e)
      (response/internal-server-error
        {:result :error
         :message "server error occurred while adding the user"}))))

(defn register! [{:keys [session]} user]
  (if-let [error-msg (registration-errors user)]
    (response/precondition-failed {:result {:error error-msg}})
    (try
      (db/create-user!
        (-> user
            (dissoc :pass-confirm)
            (update :pass hashers/encrypt)))
      (-> {:result :ok}
          (response/ok)
          (assoc :session (assoc session :identity (:id user))))
      (catch Exception e
        (handle-registration-error e)))))

(defn decode-auth [encoded]
  (let [auth (second (.split encoded " "))]
    (-> (.decode (java.util.Base64/getDecoder) auth)
        (String. (java.nio.charset.Charset/forName "UTF-8"))
        (.split ":"))))

(defn authenticate [[id pass]]
  (when-let [user (db/get-user {:id id})]
    (when (hashers/check pass (:pass user))
      id)))

(defn login! [{:keys [session]} auth]
  (if-let [id (authenticate (decode-auth auth))]
    (-> {:result :ok}
        (response/ok)
        (assoc :session (assoc session :identity id)))
    (response/unauthorized {:result :unauthorized
                            :message "login failure"})))

(defn logout! []
  (-> {:result :ok}
      (response/ok)
      (assoc :session nil)))

(defn delete-account! [identity]
  (db/delete-account! identity)
  (-> {:result :ok}
      (response/ok)
      (assoc :session nil)))