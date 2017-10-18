(defproject sqlilab "0.1.0-SNAPSHOT"
  :description "sql injection visualization"
  :url "https://github.com/calign/sqlilab"
  :license {:name "MIT"
            :url "http://www.opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [com.h2database/h2 "1.4.196"]]
  :main sqlilab.main)