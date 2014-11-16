(ns me.vedang.bloomclj.transient
  "Implements a Bloom Filter in memory. You are probably looking for this."
  (:require [me.vedang.bloomclj.core :as bc :refer [IFilter]])
  (:import (java.util BitSet)))

;;; ## TransientBloomFilter

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

(defprotocol IBitarray
  (get-bitarray [this]))

(deftype TransientBloomFilter
    [^:unsynchronized-mutable ^java.util.BitSet bitarray n fpp m k]
  IBitarray
  (get-bitarray [_] bitarray)
  IFilter
  (add [this elem]
    (doseq [^long b (bc/get-hash-buckets elem k m)]
      (.set ^java.util.BitSet bitarray b))
    "OK")

  (maybe-contains? [this elem]
    (every? identity (map #(.get ^java.util.BitSet bitarray %)
                          (bc/get-hash-buckets elem k m))))

  (clear [this]
    (.clear ^java.util.BitSet bitarray)
    "OK")

  (union [this other]
    (let [bitarray* (.clone bitarray)]
      (.or ^java.util.BitSet bitarray* (get-bitarray other))
      (TransientBloomFilter. bitarray* n fpp m k)))

  (intersection [this other]
    (let [bitarray* (.clone bitarray)]
      (.and ^java.util.BitSet bitarray* (get-bitarray other))
      (TransientBloomFilter. bitarray* n fpp m k))))

;;; ## Usage:
;;     user> (require '[me.vedang.bloomclj.core
;;                      :refer [add maybe-contains? clear]])
;;     nil
;;     user> (require '[me.vedang.bloomclj.transient
;;                      :refer [transient-bloom-filter]])
;;     nil
;;     user> (transient-bloom-filter 100000 0.01)
;;     #<TransientBloomFilter me.vedang.bloomclj.transient.TransientBloomFilter@7130779c>
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
