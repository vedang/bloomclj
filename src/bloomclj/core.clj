(ns bloomclj.core
  (:require [bloomclj.protocols :refer [to-byte-array IFilter]])
  (:import (java.util BitSet)
           (bloomjava.util.hash MurmurHash3)))


(defn- get-hash-buckets
  [e k m]
  (let [b (to-byte-array e)
        [h1 h2] (MurmurHash3/MurmurHash3_x64_128 b 0)]
    (map #(Math/abs (long (mod (+' h1 (*' % h2) ) m))) (range 1 (inc k)))))


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
    (doseq [b (get-hash-buckets elem (.k this) (.m this))]
      (.set (.bitarray this) b)))

  (maybe-contains? [this elem]
    (every? identity (map #(.get (.bitarray this) %)
                          (get-hash-buckets elem (.k this) (.m this))))))


(defonce LN2 (Math/log 2))


(let [denominator (Math/pow LN2 2)]
  (defn- get-optimal-m
    "Given n - max number of elements that will be inserted into the BF
     and   fpp - the desired false positive probability

     Return m - the size of the bit-array that should be used to represent the
                Bloom Filter."
    [^Long n ^Double fpp]
    (Math/ceil (/ (* -1 n (Math/log fpp)) denominator))))


(defn- get-optimal-k
  "Given m - the size of the bit-array used to represent the Bloom Filter
   and   n - the max number of elements that will be inserted into the BF

   Return k - the optimal number of hash functions that should be used when
              inserting the element in the Bloom Filter."
  [m n]
  (Math/ceil (/ (* m LN2) n)))


(defn transient-bloom-filter
  "Construct and return a TransientBloomFilter."
  [n fpp]
  (let [m (get-optimal-m n fpp)
        k (get-optimal-k m n)]
    (TransientBloomFilter. (BitSet. m) n fpp m k)))
