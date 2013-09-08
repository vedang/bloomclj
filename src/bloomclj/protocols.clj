(ns bloomclj.protocols)


;;; ## Protocol IByteArray
;;; convert the given input to a byte array.

;;; ### Contributed by: Kiran Kulkarni (@kirankulkarni) <kiran.1267@gmail.com>

(defprotocol IByteArray
  (to-byte-array [this]))


(extend-protocol IByteArray
  nil
  (to-byte-array [x] (byte-array 0))

  java.lang.String
  (to-byte-array [s] (.getBytes s "utf-8"))

  java.lang.Short
  (to-byte-array [short-int] (.. (java.nio.ByteBuffer/allocate 2)
                                 (putShort short-int)
                                 array))

  java.lang.Integer
  (to-byte-array [integer] (.. (java.nio.ByteBuffer/allocate 4)
                               (putShort integer)
                               array))

  java.lang.Long
  (to-byte-array [long-int] (.. (java.nio.ByteBuffer/allocate 8)
                                (putShort long-int)
                                array))

  java.nio.ByteBuffer
  (to-byte-array [byte-buffer] (.array byte-buffer))

  clojure.lang.Keyword
  (to-byte-array [k] (.getBytes (name k) "utf-8"))

  clojure.lang.IPersistentList
  (to-byte-array [l] (.getBytes (binding [*print-dup* false]
                                  (pr-str l))
                                "utf-8"))

  clojure.lang.APersistentVector
  (to-byte-array [v] (.getBytes (binding [*print-dup* false]
                                  (pr-str v))
                                "utf-8"))
  clojure.lang.APersistentMap
  (to-byte-array [m] (.getBytes (binding [*print-dup* false]
                                  (pr-str m))
                                "utf-8"))

  clojure.lang.APersistentSet
  (to-byte-array [s] (.getBytes (binding [*print-dup* false]
                                  (pr-str s))
                                "utf-8")))
