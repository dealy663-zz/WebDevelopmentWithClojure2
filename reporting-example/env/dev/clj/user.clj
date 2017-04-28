(ns user
  (:require [mount.core :as mount]
            reporting-example.core))

(defn start []
  (mount/start-without #'reporting-example.core/repl-server))

(defn stop []
  (mount/stop-except #'reporting-example.core/repl-server))

(defn restart []
  (stop)
  (start))


