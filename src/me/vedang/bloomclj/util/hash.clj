(ns me.vedang.bloomclj.util.hash
  (:require [me.vedang.bloomclj.protocols :refer [to-byte-array]])
  (:import (com.google.common.hash Hashing)))


(defn ^Integer murmurhash-32
  "Use Guava's murmur3 Hash function to get hashcode"
  [o & {:keys [seed] :or {seed 0}}]
  (.. (Hashing/murmur3_32 (mod seed Integer/MAX_VALUE))
      newHasher
      (putBytes (to-byte-array o))
      hash
      hashCode))


(defn ^Long unsigned-murmurhash-32
  "In most places in this project we need unsigned hash-code.
Note that this returns a `Long` number."
  [o & {:keys [seed] :or {seed 0}}]
  (bit-and (long (murmurhash-32 o :seed seed))
           (long 0xFFFFFFFF)))
