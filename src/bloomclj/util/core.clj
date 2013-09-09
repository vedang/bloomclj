(ns bloomclj.util.core
  (:import (org.joda.time.format DateTimeFormat DateTimeFormatter)
           (org.joda.time ReadableInstant)))


(let [alphabet (vec "abcdefghijklmnopqrstuvwxyz0123456789")]
  (defn rand-str
    "Generate a random string of length l"
    [l]
    (loop [n l res (transient [])]
      (if (zero? n)
        (apply str (persistent! res))
        (recur (dec n) (conj! res (alphabet (rand-int 36))))))))


(defn date->string
  "Convert a JodaTime object to string according to the given pattern."
  [pattern ^ReadableInstant date-obj]
  (let [dtf ^DateTimeFormatter (DateTimeFormat/forPattern pattern)]
    (.print dtf date-obj)))
