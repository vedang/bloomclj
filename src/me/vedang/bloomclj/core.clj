(ns me.vedang.bloomclj.core
  "## What is a Bloom Filter?

   Wikipedia has a great entry on [Bloom Filters.](http://en.wikipedia.org/wiki/Bloom_filter)
   Quoting from it:

     A Bloom filter, conceived by Burton Howard Bloom in 1970, is a
     space-efficient probabilistic data structure that is used to
     test whether an element is a member of a set. False positive
     matches are possible, but false negatives are not; i.e. a
     query returns either \"inside set (may be wrong)\" or
     \"definitely not in set\". Elements can be added to the set, but
     not removed (though this can be addressed with a \"counting\"
     filter). The more elements that are added to the set, the
     larger the probability of false positives."
  (:require [me.vedang.bloomclj.util.hash :refer [unsigned-murmurhash-32]]))


;;; ## Helper functions for implementing a Bloom Filter

;;; ### 1. get-hash-buckets
(defn get-hash-buckets
  "Given an element `e`, the number of hash functions to run on it
  `k`, and the size of the bit buffer used to store the filter `m`,
  calculate the bits that should be set in the bit buffer if we were
  to add this element to the filter.

  We use Guava's MurmurHash 3 and a combinatorial generation approach as
  described in Cf. Kirsch and Mitzenmacher, [Less Hashing, Same
  Performance: Building a Better Bloom Filter](<https://www.eecs.harvard.edu/~michaelm/postscripts/tr-02-05.pdf>)"
  [e k m]
  (let [h1 (unsigned-murmurhash-32 e)
        h2 (unsigned-murmurhash-32 e :seed h1)]
    (map #(long (mod (+' h1 (*' % h2)) m)) (range k))))


(defonce LN2 (Math/log 2))


;;; ### 2. get-optimal-m

(let [denominator (Math/pow LN2 2)]
  (defn ^Double get-optimal-m
    "Calculate the optimal size for the bitarray of a Bloom Filter
     given the following parameters:

        n - max number of elements that will be inserted into the Bloom Filter
        fpp - the desired false positive probability"
    [^Long n ^Double fpp]
    (Math/ceil (/ (* -1 n (Math/log fpp)) denominator))))


;;; ### 3. get-optimal-k

(defn ^Double get-optimal-k
  "Calculate the optimal number of hash functions that should be run
   on a element when inserting it into a Bloom Filter to support the
   desired False Positive Probability.

       m - the size of the bit-array used to represent the Bloom Filter

       n - the max number of elements that can be inserted into the
       Bloom Filter (without degrading the FPP)"
  [^Double m ^Long n]
  (Math/ceil (/ (* m LN2) n)))


;;; ## The Bloom Filter Protocol

(defprotocol IFilter
  "Define the set of functions that should be implemented by a Bloom Filter."
  (add [this elem])
  (maybe-contains? [this elem])
  (clear [this])
  (union [this other])
  (intersection [this other]))
