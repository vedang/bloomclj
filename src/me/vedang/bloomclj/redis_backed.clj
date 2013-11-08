(ns me.vedang.bloomclj.redis-backed
  "Implements a Bloom Filter with Redis as the backing store."
  (:require [me.vedang.bloomclj.core :as bc :refer [IFilter]]
            [taoensso.carmine :as car :refer [wcar]]
            [me.vedang.bloomclj.util.core :refer [rand-str date->string]]
            [clj-time.core :as time]))

;;; ## RedisBackedBloomFilter

;;; This type implements a Bloom Filter with Redis as the
;;; backing store. A quick explanation of the fields is as follows:

;;     n    - The maximum number of elements that will be
;;            inserted into the bloom filter. This defines
;;            the capacity of the bloom filter. Inserting
;;            more elements than n causes fpp to increase.
;;     fpp  - The desired false positive probability.
;;     m    - The total number of bits in the bit array.
;;            Calculated for optimal val using n and fpp.
;;     k    - The optimal number of hash functions that
;;            should be used. Calculated using n and fpp.
;;     spec - Redis Server spec - map containing :host
;;            and :port
;;     bloomkey - Key against which filter is maintained
;;           in Redis. This is auto-generated.


(defn make-redis-conn
  "Helper function to convert the Redis Server spec map into a Redis
  Connection map as required by Carmine."
  [spec]
  {:pool {:max-active 8}
   :spec spec})


(deftype RedisBackedBloomFilter
    [n fpp m k spec bloomkey]
  IFilter
  (add [this elem]
    (wcar (make-redis-conn spec)
          (mapv #(car/setbit bloomkey % "1")
                (bc/get-hash-buckets elem k m)))
    "OK")

  (maybe-contains? [this elem]
    (every? pos?
            (wcar (make-redis-conn spec)
                  (mapv (partial car/getbit bloomkey)
                        (bc/get-hash-buckets elem k m)))))

  (clear [this]
    (wcar (make-redis-conn spec)
          (car/del bloomkey)
          (car/setbit bloomkey (long m) "0"))
    "OK"))


;;; ## Usage:
;;     user> (require '[me.vedang.bloomclj.core
;;                      :refer [add maybe-contains? clear]])
;;     nil
;;     user> (require '[me.vedang.bloomclj.redis-backed
;;                      :refer [redis-backed-bloom-filter]])
;;     nil
;;     user> (redis-backed-bloom-filter 100000 0.01 "127.0.0.1" 6379)
;;     #<RedisBackedBloomFilter me.vedang.bloomclj.redis_backed.RedisBackedBloomFilter@720ade7a>
;;     user> (def rbf *1)
;;     #'user/rbf
;;     user> (add rbf "hello")
;;     "OK"
;;     user> (add rbf "world")
;;     "OK"
;;     user> (maybe-contains? rbf "hello")
;;     true
;;     user> (maybe-contains? rbf "goodbye")
;;     false
;;     user> (clear rbf)
;;     "OK"
;;     user> (maybe-contains? rbf "hello")
;;     false
;;     user>

(defn redis-backed-bloom-filter
  "Construct and return a RedisBackedBloomFilter"
  [n fpp host port]
  (let [m (bc/get-optimal-m n fpp)
        k (bc/get-optimal-k m n)
        server-spec {:host host
                     :port port}
        bloomkey (str "bloomclj:"
                      (rand-str 10)
                      ":"
                      (date->string "yyyyMMddHHmmssSSS" (time/now)))]
    ;; Allocate the bitarray in Redis.
    (wcar (make-redis-conn server-spec) (car/setbit bloomkey (long m) "0"))
    (RedisBackedBloomFilter. n fpp m k server-spec bloomkey)))
