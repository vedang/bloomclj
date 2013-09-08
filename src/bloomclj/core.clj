(ns bloomclj.core
  (:require [bloomclj.protocols :refer [to-byte-array]])
  (:import (bloomjava.util.hash MurmurHash)))


(defn get-hash-buckets
  "Cf. Kirsch and Mitzenmacher, \"Less Hashing, Same Performance: Building a
  Better Bloom Filter\".
  <http://www.eecs.harvard.edu/~kirsch/pubs/bbbf/esa06.pdf>"
  [e k m]
  (let [b (to-byte-array e)
        b-len (count b)
        h1 (MurmurHash/hash32 b b-len)
        h2 (MurmurHash/hash32 b b-len h1)]
    (map #(Math/abs (long (mod (+' h1 (*' % h2) ) m))) (range k))))


(defonce LN2 (Math/log 2))


(let [denominator (Math/pow LN2 2)]
  (defn get-optimal-m
    "Given n - max number of elements that will be inserted into the BF
     and   fpp - the desired false positive probability

     Return m - the size of the bit-array that should be used to represent the
                Bloom Filter."
    [^Long n ^Double fpp]
    (Math/ceil (/ (* -1 n (Math/log fpp)) denominator))))


(defn get-optimal-k
  "Given m - the size of the bit-array used to represent the Bloom Filter
   and   n - the max number of elements that will be inserted into the BF

   Return k - the optimal number of hash functions that should be used when
              inserting the element in the Bloom Filter."
  [m n]
  (Math/ceil (/ (* m LN2) n)))


(defprotocol IFilter
  "Implement a Bloom Filter"
  (add [this elem])
  (maybe-contains? [this elem])
  (clear [this]))


(defn add
  "A generic function to add an element to any object that implements IFilter."
  [o i]
  (if (instance? bloomclj.core.IFilter o)
    (.add o i)
    (throw (java.lang.ClassCastException.
            (str (class o) " does not implement IFilter")))))


(defn maybe-contains?
  "A generic function to check existence of an element in a Bloom Filter."
  [o i]
  (if (instance? bloomclj.core.IFilter o)
    (.maybe-contains? o i)
    (throw (java.lang.ClassCastException.
            (str (class o) " does not implement IFilter")))))


(defn clear
  "A generic function to clear the Bloom filter"
  [o]
  (if (instance? bloomclj.core.IFilter o)
    (.clear o)
    (throw (java.lang.ClassCastException.
            (str (class o) " does not implement IFilter")))))
