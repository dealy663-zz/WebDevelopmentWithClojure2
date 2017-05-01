(ns db-examples.core
  :require [clojure.java.jdbc :as sql])

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
