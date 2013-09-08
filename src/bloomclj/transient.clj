(ns bloomclj.transient
  (:require [bloomclj.core :as bc :refer [IFilter]])
  (:import (java.util BitSet)))


(deftype TransientBloomFilter
    [^:unsynchronized-mutable bitarray n fpp m k]
  ;; "n - the maximum number of elements that will be inserted into the bloom
  ;;      filter. This defines the capacity of the bloom filter. Inserting more
  ;;      elements than n causes `fpp' to increase.
  ;;  fpp - The desired false positive probability.
  ;;  m - the total number of bits in the bit array. Calculated for optimal val
  ;;      using n and fpp
  ;;  k - The optimal number of hash functions that should be used. Calculated
  ;;      using n and fpp"
  IFilter
  (add [this elem]
    (doseq [b (bc/get-hash-buckets elem k m)]
      (.set bitarray b)))

  (maybe-contains? [this elem]
    (every? identity (map #(.get bitarray %)
                          (bc/get-hash-buckets elem k m)))))


(defn transient-bloom-filter
  "Construct and return a TransientBloomFilter."
  [n fpp]
  (let [m (bc/get-optimal-m n fpp)
        k (bc/get-optimal-k m n)]
    (TransientBloomFilter. (BitSet. m) n fpp m k)))
