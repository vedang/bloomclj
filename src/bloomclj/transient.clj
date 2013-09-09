(ns bloomclj.transient
  (:require [bloomclj.core :as bc :refer [IFilter]])
  (:import (java.util BitSet)))

;;; ## TransientBloomFilter

;;; You are probably looking for this.

;;; This type implements a TransientBloomFilter, with memory as the
;;; backing store. A `java.util.BitSet` bitarray is used to store the
;;; Bloom Filter. A quick explanation of the fields is as follows:

;;     n   - The maximum number of elements that will be
;;           inserted into the bloom filter. This defines
;;           the capacity of the bloom filter. Inserting
;;           more elements than n causes fpp to increase.
;;     fpp - The desired false positive probability.
;;     m   - The total number of bits in the bit array.
;;           Calculated for optimal val using n and fpp.
;;     k   - The optimal number of hash functions that
;;           should be used. Calculated using n and fpp.

(deftype TransientBloomFilter
    [^:unsynchronized-mutable bitarray n fpp m k]
  IFilter
  (add [this elem]
    (doseq [b (bc/get-hash-buckets elem k m)]
      (.set bitarray b))
    "OK")

  (maybe-contains? [this elem]
    (every? identity (map #(.get bitarray %)
                          (bc/get-hash-buckets elem k m))))

  (clear [this]
    (.clear bitarray)
    "OK"))


;;; ## Usage:
;;     user> (require '[bloomclj.core
;;                      :refer [add maybe-contains? clear]])
;;     nil
;;     user> (require '[bloomclj.transient
;;                      :refer [transient-bloom-filter]])
;;     nil
;;     user> (transient-bloom-filter 100000 0.01)
;;     #<TransientBloomFilter bloomclj.transient.TransientBloomFilter@7130779c>
;;     user> (def tbf *1)
;;     #'user/tbf
;;     user> (add tbf "hello")
;;     "OK"
;;     user> (add tbf "world")
;;     "OK"
;;     user> (maybe-contains? tbf "hello")
;;     true
;;     user> (maybe-contains? tbf "goodbye")
;;     false
;;     user> (clear tbf)
;;     "OK"
;;     user> (maybe-contains? tbf "hello")
;;     false
;;     user>
(defn transient-bloom-filter
  "Construct and return a TransientBloomFilter."
  [n fpp]
  (let [m (bc/get-optimal-m n fpp)
        k (bc/get-optimal-k m n)]
    (TransientBloomFilter. (BitSet. m) n fpp m k)))
