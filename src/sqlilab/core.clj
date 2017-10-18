(ns sqlilab.core
  (:require [clojure.java.jdbc :as j])
  (:import (java.net URLEncoder)))

(def db {
  :classname "org.h2.Driver"
  :subprotocol "h2:mem"
  :subname "lab;DB_CLOSE_DELAY=-1"
  :user "sa"
  :password ""})

(def accounts [
  ["admin" "not admin"]
  ["mario" "ariom"]
  ["luigi" "mario"]
  ["another user just because" "without a password!"]])

(defn populate-db [db accounts]
  (j/db-do-commands
    db
    (j/create-table-ddl
      :users
      [ [:username "varchar(32)" :primary :key]
        [:password "varchar(32)"]]))
  (doseq [[username password] accounts]
    (j/insert! db :users {:username username :password password})))

(defn layout[^String username, ^String password, usernames]
  (apply str
    "<!DOCTYPE html>"
    "<html>"
      "<head>"
        "<style>"
"body {"
"  font-family: sans;"
"  background-color: lightblue;"
"}"
"dt {"
"  font-weight: bold;"
"}"
        "</style>"
      "</head>"
      "<body>"
        "<h1>SQLi Lab</h1>"
        "<h3>SQL injection - what?</h3>"
        "<p>"
          "Injection attacks happen when data interpolated in a string of a language happens to be meaningful for the language itself.<br>"
          "SQL queries are commonly handled as strings with placeholders that are expected to be replaced by data (e.g. an username) "
            "but when the interpolation is handled unsafely it is possible to slip in more valid SQL, subverting the meaning of the original query.<br>"
          "The login form below implement such a vulnerability. You will be considered logged in if the query (shown below) matches at "
            "least an username-password combination: try to write some SQL in the fields and see what happens! The interpolated string is"
            "shown too to help visualizing what's going on on the server.<br>"
          "<a href=\"https://www.owasp.org/index.php/SQL_Injection\">Read more about SQLi from OWASP</a>"
        "</p>"
        "<form action=\"/\" method=\"POST\">"
          "<label>username<input type=\"text\" name=\"username\"></label>"
          "<label>password<input type=\"text\" name=\"password\"></label>"
          "<input type=\"submit\" value=\"login\">"
        "</form>"
        "<dl class=\"queries\">"
          "<dt>query string interpolation (clojure)</dt>"
          "<dd><code>(str \"select username from users where username = '\" username \"' and password = '\" password \"';\")</code></dd>"
          "<dt>interpolated query string</dt>"
          "<dd><code>\"select username from users where username = '" username "' and password = '" password "';\"</code></dd>"
          "<dt>results (" (count usernames) ")</dt><dd>"
            "<dl class=\"users\">"
              (apply str
                (map
                  (fn [username] (apply str "<dt>user</dt><dd>" username "</dd>"))
                  usernames))
            "</dl>"
          "</dd>"
        "</dl>"
      "</body>"
    "</html>"
    ))

(defn interpolate-query [username password]
  (str "select username from users where username = '" username "' and password = '" password "';"))

(defn login [db username password]
  (map
    (fn [row] (:username row))
    (j/query db (interpolate-query username password))))

(defn post-username [request]
  (or
    (get (:params request) "username")
    ""))
(defn post-password [request]
  (or
    (get (:params request) "password")
    ""))

(defn make-main-page-handler [db]
  (fn [request]
    { :status 200
      :headers {"Content-Type" "text/html"}
      :body (layout
        (post-username request)
        (post-password request)
        (login db (post-username request) (post-password request)))}))
