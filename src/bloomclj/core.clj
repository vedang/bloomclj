(ns bloomclj.core)
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
