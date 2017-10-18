(ns sqlilab.main
  (:require [ring.adapter.jetty :as ring-j]
            [ring.middleware.params :as rp]
            [sqlilab.core :refer :all]))

(defn -main
  [& args]
  (populate-db db accounts)
  (ring-j/run-jetty
    (rp/wrap-params
      (make-main-page-handler db))
    {:port 3000}))
