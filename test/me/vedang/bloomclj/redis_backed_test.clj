(ns me.vedang.bloomclj.redis-backed-test
  (:require [me.vedang.bloomclj.core :refer :all]
            [me.vedang.bloomclj.redis-backed :refer :all]
            [clojure.test :refer :all]))

(def tbf1 (redis-backed-bloom-filter 1000000 0.01 "127.0.0.1" 6379))
(add tbf1 "hello")
(add tbf1 "world")

(def tbf2 (redis-backed-bloom-filter 1000000 0.01 "127.0.0.1" 6379))
(add tbf2 "goodbye")
(add tbf2 "world")

(deftest direct-addition-test
  ;; hello world
  (is (maybe-contains? tbf1 "hello"))
  (is (not (maybe-contains? tbf1 "goodbye")))
  (is (maybe-contains? tbf1 "world"))
  ;; goodbye world
  (is (not (maybe-contains? tbf2 "hello")))
  (is (maybe-contains? tbf2 "goodbye"))
  (is (maybe-contains? tbf2 "world")))

(deftest union-test
  (let [tbf3 (union tbf1 tbf2)]
    (is (maybe-contains? tbf3 "hello"))
    (is (maybe-contains? tbf3 "goodbye"))
    (is (maybe-contains? tbf3 "world"))))

(deftest intersection-test
  (let [tbf4 (intersection tbf1 tbf2)]
    (is (not (maybe-contains? tbf4 "hello")))
    (is (not (maybe-contains? tbf4 "goodbye")))
    (is (maybe-contains? tbf4 "world"))))

